package org.example.mappro.tiles.tile.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import org.example.mappro.tiles.grpc.HealthResponse;
import org.example.mappro.tiles.grpc.TileRequest;
import org.example.mappro.tiles.grpc.TileResponse;
import org.example.mappro.tiles.grpc.TileServiceGrpc;
import org.example.mappro.tiles.tile.dto.TileRequestDto;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class TileGrpcService extends TileServiceGrpc.TileServiceImplBase {

    private final TileRESTService tileService;

    @Override
    public void getTile(TileRequest request,
                        StreamObserver<TileResponse> responseObserver) {

        long startTime = System.currentTimeMillis();

        try {
            log.info("gRPC GetTile: z={}, x={}, y={}", 
                    request.getZ(), request.getX(), request.getY());

            TileRequestDto dto = TileRequestDto.builder()
                    .z(request.getZ())
                    .x(request.getX())
                    .y(request.getY())
                    .build();
            
            String geojson = tileService.getTileAsJson(dto);
            int count = countFeatures(geojson);

            TileResponse response = TileResponse.newBuilder()
                    .setGeojson(geojson)
                    .setCount(count)
                    .build();

            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("gRPC GetTile завершён: {} объектов, {} мс", count, elapsedTime);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.warn("Некорректный запрос: {}", e.getMessage());
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Некорректный запрос: " + e.getMessage())
                            .asException()
            );
        } catch (Exception e) {
            log.error("Ошибка в gRPC GetTile: {}", e.getMessage(), e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Внутренняя ошибка сервера: " + e.getMessage())
                            .withCause(e)
                            .asException()
            );
        }
    }

    @Override
    public void healthCheck(Empty request, StreamObserver<HealthResponse> responseObserver) {
        try {
            HealthResponse response = HealthResponse.newBuilder()
                    .setStatus("SERVING")
                    .setVersion("1.0.0")
                    .setUptimeSeconds(System.currentTimeMillis() / 1000)
                    .build();

            log.debug("HealthCheck запрос получен");
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Ошибка в HealthCheck: {}", e.getMessage(), e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Ошибка health check")
                            .asException()
            );
        }
    }

    private int countFeatures(String geojson) {
        if (geojson == null || geojson.isEmpty()) {
            return 0;
        }

        try {
            int count = 0;
            int index = 0;
            String search = "\"type\":\"Feature\"";
            while ((index = geojson.indexOf(search, index)) != -1) {
                count++;
                index += search.length();
            }
            return count;
        } catch (Exception e) {
            log.warn("Ошибка подсчёта объектов: {}", e.getMessage());
            return 0;
        }
    }
}