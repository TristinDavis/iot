package me.vsadokhin.iot.data;

import static com.datastax.driver.core.querybuilder.QueryBuilder.column;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lte;
import static me.vsadokhin.iot.data.MetricTable.METRIC_BY_SENSOR;

import java.util.Arrays;
import java.util.NoSuchElementException;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import me.vsadokhin.iot.data.domain.GetStatisticsRequest;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.exception.GetStatisticsException;
import me.vsadokhin.iot.data.exception.StorageException;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import me.vsadokhin.iot.data.utility.DateUtility;

public class MetricRepository {

    public void insert(Metric metric, MetricTable table) {
        Session session = CassandraSessionUtility.getSession();
        Insert insert = prepareInsert(metric, table);
        try {
            session.execute(insert);
        } catch (DriverException e) {
            throw new StorageException("Failed to insert metric: " + metric, e);
        }
    }

    private Insert prepareInsert(Metric metric, MetricTable table) {
        Insert insert = QueryBuilder.insertInto(table.getTable());
        insert.value("sensor_id", metric.getSensorId());
        insert.value("when", metric.getWhen());
        insert.value("value", metric.getValue());

        if (METRIC_BY_SENSOR.equals(table)) {
            insert.value("week", DateUtility.getWeekStart(metric.getWhen()));
        } else {
            insert.value("type", metric.getType());
            insert.value("day", DateUtility.getDayStart(metric.getWhen()));
        }
        return insert;
    }

    public float getStatistics(GetStatisticsRequest getStatisticsRequest) {
        validate(getStatisticsRequest);
        Statement select = prepareAggregateQuery(getStatisticsRequest);
        Session session = CassandraSessionUtility.getSession();
        try {
            ResultSet result = session.execute(select);
            Row row = result.iterator().next();
            return row.getFloat(getStatisticsRequest.getAggregator());
        } catch (DriverException | NoSuchElementException e) {
            throw new StorageException("Failed to get statistics: " + getStatisticsRequest, e);
        }
    }

    private void validate(GetStatisticsRequest getStatisticsRequest) {
        if (getStatisticsRequest.getSensorId() == null && getStatisticsRequest.getType() == null) {
            throw new GetStatisticsException("Type or sensorId must be specified");
        }
        if (getStatisticsRequest.getSensorId() != null && getStatisticsRequest.getType() != null) {
            throw new GetStatisticsException("Only type or only sensorId must be specified");
        }
        if (!Arrays.asList("min", "max", "avg").contains(getStatisticsRequest.getAggregator())) {
            throw new GetStatisticsException(getStatisticsRequest.getAggregator() + " is not supported aggregator. Supported values: min, max, avg");
        }
        if (getStatisticsRequest.getFrom() > getStatisticsRequest.getTo()) {
            throw new GetStatisticsException("To must be greater than From");
        }
        if (getStatisticsRequest.getSensorId() != null
                && DateUtility.getWeekStart(getStatisticsRequest.getFrom()) != DateUtility.getWeekStart(getStatisticsRequest.getTo())) {
            throw new GetStatisticsException("From and To must point to the same week for aggregate by sensor query");
        }
        if (getStatisticsRequest.getType() != null
                && DateUtility.getDayStart(getStatisticsRequest.getFrom()) != DateUtility.getDayStart(getStatisticsRequest.getTo())) {
            throw new GetStatisticsException("From and To must point to the same day for aggregate by type query");
        }
    }

    private Select.Where prepareAggregateQuery(GetStatisticsRequest getStatisticsRequest) {
        Select select = prepareAggregateSelection(getStatisticsRequest);
        return prepareAggregateWhereStatement(getStatisticsRequest, select);
    }

    private Select prepareAggregateSelection(GetStatisticsRequest getStatisticsRequest) {
        Select.Selection selection = QueryBuilder.select();
        String aggregator = getStatisticsRequest.getAggregator();
        selection.fcall(aggregator, column("value")).as(aggregator);
        return selection.from(getTableForAggregateQuery(getStatisticsRequest));
    }

    private String getTableForAggregateQuery(GetStatisticsRequest getStatisticsRequest) {
        if (getStatisticsRequest.getSensorId() != null) {
            return MetricTable.METRIC_BY_SENSOR.getTable();
        }
        return MetricTable.METRIC_BY_TYPE.getTable();
    }

    private Select.Where prepareAggregateWhereStatement(GetStatisticsRequest getStatisticsRequest, Select select) {
        long from = getStatisticsRequest.getFrom();
        Select.Where where = select.where(gte("when", from));
        where.and(lte("when", getStatisticsRequest.getTo()));
        if (getStatisticsRequest.getSensorId() != null) {
            where.and(eq("sensor_id", getStatisticsRequest.getSensorId()));
            where.and(eq("week", DateUtility.getWeekStart(from)));
        } else {
            where.and(eq("type", getStatisticsRequest.getType()));
            where.and(eq("day", DateUtility.getDayStart(from)));
        }
        return where;
    }
}