package org.example.mappro.tiles.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TileRequest {
    private int z;
    private int x;
    private int y;
    private List<String> types;          // фильтр по типам объектов
    private List<String> severities;     // фильтр по важности нарушений
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;          // нарушения не ранее указанной даты
    private Boolean showCities;          // показывать города (по умолчанию true)
    private String lang;                 // язык (ru/en) для локализации
}
