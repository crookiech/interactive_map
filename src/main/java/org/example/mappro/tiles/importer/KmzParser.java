package org.example.mappro.tiles.importer;

import org.example.mappro.tiles.importer.model.ImportedPlacemark;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class KmzParser {
    private static final long MAX_KML_BYTES = 50L * 1024 * 1024;
    private static final int MAX_ENTRIES = 1_000;
    private final KmlGeometryParser kmlParser = new KmlGeometryParser();

    public List<ImportedPlacemark> parse(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new KmzImportException("KMZ file is empty");
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".kmz")) {
            throw new KmzImportException("Only .kmz files are supported");
        }

        try (ZipInputStream zip = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            int entryCount = 0;
            while ((entry = zip.getNextEntry()) != null) {
                if (++entryCount > MAX_ENTRIES) throw new KmzImportException("KMZ contains too many files");
                if (!entry.isDirectory() && entry.getName().toLowerCase(Locale.ROOT).endsWith(".kml")) {
                    return kmlParser.parse(readLimited(zip));
                }
            }
            throw new KmzImportException("KMZ does not contain a KML document");
        } catch (IOException e) {
            throw new KmzImportException("Cannot read KMZ file", e);
        }
    }

    private String readLimited(ZipInputStream zip) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        long total = 0;
        int read;
        while ((read = zip.read(buffer)) != -1) {
            total += read;
            if (total > MAX_KML_BYTES) throw new KmzImportException("Unpacked KML is larger than 50 MB");
            output.write(buffer, 0, read);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
