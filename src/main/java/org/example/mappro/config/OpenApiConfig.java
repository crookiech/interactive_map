package org.example.mappro.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.example.mappro.tiles.dto.ErrorResponseDto;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Spring OpenAPI example", version = "1.0.0"),
        security = @SecurityRequirement(name = "BearerAuth"))
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        name = "BearerAuth")
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Добавление схемы ошибки
            var errorSchema = new Schema<ErrorResponseDto>();
            openApi.getComponents().addSchemas("ErrorResponseDto", errorSchema);
            
            // Применение схемы к ответам
            for (PathItem pathItem : openApi.getPaths().values()) {
                for (Operation operation : pathItem.readOperations()) {
                    for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                        String status = entry.getKey();
                        ApiResponse response = entry.getValue();
                        if (status.startsWith("4") || status.startsWith("5")) {
                            if (response.getContent() != null) {
                                response.getContent().values()
                                    .forEach(mediaType -> mediaType.setSchema(
                                        new Schema<>().$ref("#/components/schemas/ErrorResponseDto")
                                    ));
                            }
                        }
                    }
                }
            }
        };
    }

    @RestController
    @RequestMapping("/donotuse")
    static public class WorkaroundConfig {

        @io.swagger.v3.oas.annotations.Operation(description = "Don't use. " +
                "This endpoint is a workaround required for configuration",
                deprecated = true,
                responses = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(schema =
                        @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponseDto.class)))
                })
        @Tag(name = "Don't use")
        @DeleteMapping
        public void registerOpenApiComponent() {
        }
    }
}