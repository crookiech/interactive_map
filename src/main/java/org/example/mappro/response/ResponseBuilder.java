package org.example.mappro.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseBuilder {

    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String description, Object data) {
        Map<String, Object> responseDetails = new LinkedHashMap<>();
        responseDetails.put("code", status.value());
        responseDetails.put("description", description);

        Map<String, Object> finalResponse = new LinkedHashMap<>();
        finalResponse.put("response", responseDetails);
        finalResponse.put("data", data);

        return new ResponseEntity<>(finalResponse, status);
    }

}
