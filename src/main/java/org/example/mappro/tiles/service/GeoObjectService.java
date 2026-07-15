package org.example.mappro.tiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mappro.tiles.model.GeoObject;
import org.example.mappro.tiles.repository.GeoObjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoObjectService {

    private final GeoObjectRepository repository;

    @Transactional(readOnly = true)
    public GeoObject getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Object not found: " + id));
    }

    @Transactional(readOnly = true)
    public GeoObject getByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new RuntimeException("Object not found: " + name));
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repository.findByName(name).isPresent();
    }
}