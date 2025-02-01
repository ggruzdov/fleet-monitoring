package com.github.ggruzdov.fleetmonitoring.feeder.messaging;

import com.github.ggruzdov.fleetmonitoring.feeder.domain.Metric;

public interface MetricSender {

    void send(Metric metric);
}
