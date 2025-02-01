package com.github.ggruzdov.fleetmonitoring.processor.service;

import com.github.ggruzdov.fleetmonitoring.processor.domain.Metric;
import com.github.ggruzdov.fleetmonitoring.processor.repository.MetricRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;
    
    @Value("${batch.size}")
    private int batchSize;
    
    private final BlockingQueue<Metric> metricsQueue = new LinkedBlockingQueue<>();

    @PreDestroy
    void tearDown() {
        flushMetrics();
    }
    
    public void queueMetric(Metric metric) {
        if (!metricsQueue.offer(metric)) {
            throw new IllegalStateException("Metrics queue is full");
        }

        if (metricsQueue.size() >= batchSize) {
            flushMetrics();
        }
    }
    
    @Scheduled(fixedDelayString = "${batch.flush-interval-ms}")
    public void flushMetrics() {
        List<Metric> batchMetrics = new ArrayList<>();
        metricsQueue.drainTo(batchMetrics);
        
        if (!batchMetrics.isEmpty()) {
            try {
                metricRepository.saveMetricsInBatch(batchMetrics);
                log.info("Saved batch of {} metrics", batchMetrics.size());
            } catch (Exception e) {
                // For production add some retry mechanism
                log.error("Error saving metrics batch", e);
            }
        }
    }
}