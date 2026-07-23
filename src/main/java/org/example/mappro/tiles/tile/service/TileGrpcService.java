package org.example.mappro.tiles.tile.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.example.mappro.tiles.grpc.ChunkResponse;
import org.example.mappro.tiles.grpc.HealthResponse;
import org.example.mappro.tiles.grpc.TileRequest;
import org.example.mappro.tiles.grpc.TileServiceGrpc;
import org.example.mappro.tiles.tile.dto.TileRequestDto;
import java.time.LocalDate;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class TileGrpcService extends TileServiceGrpc.TileServiceImplBase {

    private final TileMVTGrpcService tileMVTService;

    @Override
    public void getTile(TileRequest request, StreamObserver<ChunkResponse> responseObserver) {

        long startTime = System.currentTimeMillis();

        try {
            log.info("gRPC GetTile MVT: z={}, x={}, y={}", request.getZ(), request.getX(), request.getY());

            TileRequestDto dto = TileRequestDto.builder()
                .z(request.getZ())
                .x(request.getX())
                .y(request.getY())
                .types(request.getTypesList().isEmpty() ? null : request.getTypesList())
                .severities(request.getSeveritiesList().isEmpty() ? null : request.getSeveritiesList())
                .fromDate(request.getFromDate() != null && !request.getFromDate().isEmpty() ? LocalDate.parse(request.getFromDate()) : null)
                .showCities(request.getShowCities())
                .lang(request.getLang())
                .build();
            
            byte[] mvtData = tileMVTService.getTileAsMVT(dto);
            
            if (mvtData == null || mvtData.length == 0) {
                ChunkResponse emptyResponse = ChunkResponse.newBuilder()
                    .setData(ByteString.EMPTY)
                    .setIsLast(true)
                    .build();
                responseObserver.onNext(emptyResponse);
                responseObserver.onCompleted();
                return;
            }

            int chunkSize = 1024 * 1024;
            int totalChunks = (int) Math.ceil((double) mvtData.length / chunkSize);
            
            for (int i = 0; i < totalChunks; i++) {
                int from = i * chunkSize;
                int to = Math.min(from + chunkSize, mvtData.length);
                byte[] chunk = new byte[to - from];
                System.arraycopy(mvtData, from, chunk, 0, chunk.length);
                
                ChunkResponse response = ChunkResponse.newBuilder()
                    .setData(ByteString.copyFrom(chunk))
                    .setIsLast(i == totalChunks - 1)
                    .build();
                
                responseObserver.onNext(response);
                log.debug("Отправлен чанк {}/{} ({} байт)", i + 1, totalChunks, chunk.length);
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("gRPC GetTile MVT завершён: {} байт, {} чанков, {} мс", mvtData.length, totalChunks, elapsedTime);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            log.warn("Некорректный запрос: {}", e.getMessage());
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Некорректный запрос: " + e.getMessage())
                    .asException()
            );
        } catch (Exception e) {
            log.error("Ошибка в gRPC GetTile MVT: {}", e.getMessage(), e);
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
}