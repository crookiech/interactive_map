package org.example.mappro.tiles.city.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.city.model.City;
import org.example.mappro.tiles.city.repository.CityRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CityDataInitializer implements ApplicationRunner {
    private static final Map<String, String> ALLOWED_CITIES = Map.ofEntries(
        Map.entry("Novosibirsk", "Новосибирск"),
        Map.entry("Tomsk", "Томск"),
        Map.entry("Omsk", "Омск"),
        Map.entry("Yuzhno-Sakhalinsk", "Сахалин"),
        Map.entry("Khabarovsk", "Хабаровск"),
        Map.entry("Komsomolsk-on-Amur", "Комсомольск-на-Амуре"),
        Map.entry("Irkutsk", "Иркутск"),
        Map.entry("Magistral'nyy", "Магистральное"),
        Map.entry("Lensk", "Ленск"),
        Map.entry("Olyokminsk", "Олёкминск"),
        Map.entry("Yakutsk", "Якутск"),
        Map.entry("Aldan", "Алдан"),
        Map.entry("Belogorsk", "Белогорск"),
        Map.entry("Blagoveshchensk", "Благовещенск"),
        Map.entry("Zavitinsk", "Завитинск"),
        Map.entry("Svobodnyy", "Свободный"),
        Map.entry("Shimanovsk", "Шимановск"),
        Map.entry("Skovorodino", "Сковородино"),
        Map.entry("Tynda", "Тында"),
        Map.entry("Neryungri", "Нерюнгри"),
        Map.entry("Birobidzhan", "Биробиджан"),
        Map.entry("Bureya", "Бурейское"),
        Map.entry("Barabinsk", "Барабинск"),
        Map.entry("Barnaul", "Барнаул"),
        Map.entry("Novokuznetsk", "Новокузнецк"),
        Map.entry("Kemerovo", "Кемерово")
    );
    private static final GeometryFactory GEOMETRY_FACTORY =
        new GeometryFactory(new PrecisionModel(), 4326);

    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS cities_geometry_gix
            ON cities USING GIST (geometry)
            """);

        Set<String> existingIds = cityRepository.findAllExternalIds();
        List<City> missingCities = new ArrayList<>();
        readSelectedCities().values().stream()
            .sorted(Comparator.comparing(node -> node.path("properties").path("ascii_name").asText()))
            .forEach(feature -> {
                String externalId = feature.path("properties").path("id").asText();
                if (!existingIds.contains(externalId)) missingCities.add(toCity(feature));
            });

        if (!missingCities.isEmpty()) {
            cityRepository.saveAll(missingCities);
            log.info("Imported {} cities", missingCities.size());
        }
    }

    private Map<String, JsonNode> readSelectedCities() throws Exception {
        ClassPathResource resource = new ClassPathResource("data/russia-cities.geojson");
        try (InputStream stream = resource.getInputStream()) {
            JsonNode features = objectMapper.readTree(stream).path("features");
            Map<String, JsonNode> selected = new LinkedHashMap<>();
            features.forEach(feature -> {
                JsonNode properties = feature.path("properties");
                String asciiName = properties.path("ascii_name").asText(properties.path("name").asText());
                if (!ALLOWED_CITIES.containsKey(asciiName)) return;
                JsonNode existing = selected.get(asciiName);
                if (existing == null || population(feature) > population(existing)) {
                    selected.put(asciiName, feature);
                }
            });
            return selected;
        }
    }

    private City toCity(JsonNode feature) {
        JsonNode properties = feature.path("properties");
        String asciiName = properties.path("ascii_name").asText();
        JsonNode coordinates = feature.path("geometry").path("coordinates");
        City city = new City();
        city.setExternalId(properties.path("id").asText());
        city.setName(ALLOWED_CITIES.get(asciiName));
        city.setAsciiName(asciiName);
        city.setPopulation(population(feature));
        city.setLabelPriority(properties.path("label_priority").asInt(1));
        city.setVisible(true);
        city.setGeometry(GEOMETRY_FACTORY.createPoint(
            new Coordinate(coordinates.get(0).asDouble(), coordinates.get(1).asDouble())
        ));
        return city;
    }

    private long population(JsonNode feature) {
        return feature.path("properties").path("population").asLong(0);
    }
}
