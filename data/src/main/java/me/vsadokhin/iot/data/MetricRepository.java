package me.vsadokhin.iot.data;

import static com.datastax.driver.core.querybuilder.QueryBuilder.column;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lt;
import static me.vsadokhin.iot.data.MetricTable.METRIC_BY_SENSOR;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import me.vsadokhin.iot.data.domain.GetStatisticsRequest;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import me.vsadokhin.iot.data.utility.DateUtility;

public class MetricRepository {

    public void insert(Metric metric, MetricTable table) {
        Session session = CassandraSessionUtility.getSession();
        Insert insert = prepareInsert(metric, table);
        session.execute(insert);
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
        // TODO Vasiliy validate getStatisticsRequest
        Statement select = prepareAggregateQuery(getStatisticsRequest);
        Session session = CassandraSessionUtility.getSession();
        ResultSet result = session.execute(select);
        Row row = result.iterator().next();
        return row.getFloat(getStatisticsRequest.getAggregator());
    }

    private Select.Where prepareAggregateQuery(GetStatisticsRequest getStatisticsRequest) {
        Select select = prepareAggregateSelection(getStatisticsRequest);
        return prepareAggregateWhereStatement(getStatisticsRequest, select);
    }

    private Select prepareAggregateSelection(GetStatisticsRequest getStatisticsRequest) {
        Select.Selection selection = QueryBuilder.select();
        String aggregator = getStatisticsRequest.getAggregator();
        selection.fcall(aggregator, column("value")).as(aggregator);
        return selection.from(getStatisticsRequest.getMetricTable().getTable());
    }

    private Select.Where prepareAggregateWhereStatement(GetStatisticsRequest getStatisticsRequest, Select select) {
        long from = getStatisticsRequest.getFrom();
        Select.Where where = select.where(gte("when", from));
        where.and(lt("when", getStatisticsRequest.getTo()));
        if (getStatisticsRequest.getType() != null) {
            where.and(eq("type", getStatisticsRequest.getType()));
        }
        if (getStatisticsRequest.getSensorIds() != null && !getStatisticsRequest.getSensorIds().isEmpty()) {
            where.and(in("sensor_id", getStatisticsRequest.getSensorIds()));
        }
        if (METRIC_BY_SENSOR.equals(getStatisticsRequest.getMetricTable())) {
            where.and(eq("week", DateUtility.getWeekStart(from)));
        } else {
            where.and(eq("day", DateUtility.getDayStart(from)));
        }
        return where;
    }
}