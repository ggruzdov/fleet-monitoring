package com.github.ggruzdov.fleetmonitoring.processor.repository;

import com.github.ggruzdov.fleetmonitoring.processor.domain.Metric;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MetricRepository {

    @Value("${batch.size}")
    private int batchSize;
    private final JdbcTemplate jdbcTemplate;

    public void saveMetricsInBatch(List<Metric> metrics) {
        String sql = "INSERT INTO metrics (time, vehicle_id, device_id, name, value) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, metrics, batchSize, (ps, metric) -> {
            ps.setTimestamp(1, java.sql.Timestamp.from(metric.time()));
            ps.setInt(2, metric.vehicleId());
            ps.setInt(3, metric.deviceId());
            ps.setString(4, metric.name());
            ps.setDouble(5, metric.value());
        });
    }
}
