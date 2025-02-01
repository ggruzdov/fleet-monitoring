-- In this demo we follow a narrow table design
-- https://www.timescale.com/learn/designing-your-database-schema-wide-vs-narrow-postgres-tables
CREATE TABLE metrics (
     time       timestamptz      NOT NULL,
     vehicle_id INT              NOT NULL,
     device_id  INT              NOT NULL,
     name       TEXT             NOT NULL,
     value      DOUBLE PRECISION NOT NULL
);

CREATE INDEX ON metrics (vehicle_id, name);

-- Converting metrics into hypertable. Default chuck is 7 days
-- https://www.timescale.com/learn/is-postgres-partitioning-really-that-hard-introducing-hypertables
SELECT create_hypertable('metrics', by_range('time'));
SELECT set_chunk_time_interval('metrics', INTERVAL '1 hours'); -- just for demo

-- Add compression
-- https://www.timescale.com/blog/building-columnar-compression-in-a-row-oriented-database
ALTER TABLE metrics SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'vehicle_id,name'
);