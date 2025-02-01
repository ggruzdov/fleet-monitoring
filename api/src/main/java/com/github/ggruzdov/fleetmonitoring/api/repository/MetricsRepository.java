package com.github.ggruzdov.fleetmonitoring.api.repository;

import com.github.ggruzdov.fleetmonitoring.api.domain.MetricName;
import com.github.ggruzdov.fleetmonitoring.api.request.GetVehicleHistogramMetricRequest;
import com.github.ggruzdov.fleetmonitoring.api.request.GetVehicleSingleMetricRequest;
import com.github.ggruzdov.fleetmonitoring.api.response.GetVehicleHistogramMetricResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MetricsRepository {

    private final JdbcTemplate jdbcTemplate;

    public Float getAvg(GetVehicleSingleMetricRequest request, MetricName metricName) {
        var sql = """
                SELECT ROUND(AVG(m.value)::numeric, 2)
                FROM metrics m WHERE vehicle_id = ? and m.name = ? and m.time >= ? and m.time <= ?
            """;
        return getAggregatedValue(request, metricName, sql);
    }

    public Float getMax(GetVehicleSingleMetricRequest request, MetricName metricName) {
        var sql = """
                SELECT ROUND(MAX(m.value)::numeric, 2)
                FROM metrics m WHERE vehicle_id = ? and m.name = ? and m.time >= ? and m.time <= ?
            """;
        return getAggregatedValue(request, metricName, sql);
    }

    public Float getTotalMileage(GetVehicleSingleMetricRequest request) {
        var sql = """
                SELECT ROUND(MAX(m.value)::numeric, 2) - ROUND(MIN(m.value)::numeric, 2)
                FROM metrics m WHERE vehicle_id = ? and m.name = ? and m.time >= ? and m.time <= ?
            """;
        return getAggregatedValue(request, MetricName.MILEAGE, sql);
    }

    public List<GetVehicleHistogramMetricResponse> getMaxByInterval(GetVehicleHistogramMetricRequest request, MetricName metricName) {
        var sql = """
                SELECT time_bucket('%d minutes', m.time) AS time_bucket, MAX(m.value) AS max_value
                FROM metrics m
                WHERE m.vehicle_id = ? AND m.name = ? AND m.time >= ? AND m.time <= ?
                GROUP BY time_bucket
                ORDER BY time_bucket;
            """.formatted(request.interval());

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> new GetVehicleHistogramMetricResponse(rs.getTimestamp(1).toInstant(), rs.getFloat(2)),
            request.vehicleId(),
            metricName.name(),
            Timestamp.from(request.from()),
            Timestamp.from(request.to())
        );
    }

    private Float getAggregatedValue(GetVehicleSingleMetricRequest request, MetricName metricName, String sql) {
        return jdbcTemplate.queryForObject(
            sql,
            Float.class,
            request.vehicleId(),
            metricName.name(),
            Timestamp.from(request.from()),
            Timestamp.from(request.to())
        );
    }
}
