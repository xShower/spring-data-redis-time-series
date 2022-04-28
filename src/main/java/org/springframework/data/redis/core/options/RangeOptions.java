package org.springframework.data.redis.core.options;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.springframework.data.redis.core.protocol.Aggregation;
import org.springframework.data.redis.core.protocol.Keywords;
import org.springframework.data.redis.core.protocol.Reduce;
import org.springframework.data.redis.core.protocol.entity.Label;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: syh
 * @Date: 2021/11/17
 * @Description:
 */
public class RangeOptions {
    private int count;
    private int align;
    private Aggregation aggregationType;
    private long timeBucket;
    private boolean withLabels;
    private Long[] tsFilters;
    private Boolean vlsFilterByMax;
    private String[] labels;
    private Label[] filters;
    private String groupBy;
    private Reduce reduce;

    public RangeOptions() {
    }

    public RangeOptions max(int count) {
        this.count = count;
        return this;
    }

    public RangeOptions aggregationType(Aggregation aggregationType, long timeBucket) {
        this.aggregationType = aggregationType;
        this.timeBucket = timeBucket;
        return this;
    }

    public RangeOptions withLabels() {
        this.withLabels = true;
        return this;
    }

    public RangeOptions selectedLabels(String ...labels) {
        this.labels = labels;
        return this;
    }

    public RangeOptions filters(Label ...filters) {
        this.filters = filters;
        return this;
    }

    public RangeOptions tsFilters(Long ...tsFilters) {
        this.tsFilters = tsFilters;
        return this;
    }

    public RangeOptions vlsFilter(boolean max) {
        this.vlsFilterByMax = max;
        return this;
    }

    public RangeOptions count(int count) {
        this.count = count;
        return this;
    }

    public RangeOptions align(int align) {
        this.align = align;
        return this;
    }

    public RangeOptions groupBy(String label, Reduce reduce) {
        this.groupBy = label;
        this.reduce = reduce;
        return this;
    }

    public Object[] range() {
        List<Object> options = new ArrayList<>();
        if (!Objects.isNull(tsFilters) && tsFilters.length > 0) {
            options.add(Keywords.FILTER_BY_TS.name());
            for (Long tsFilter : tsFilters) {
                options.add(tsFilter);
            }
        }
        if (!Objects.isNull(vlsFilterByMax)) {
            options.add(Keywords.FILTER_BY_VALUE.name());
            options.add(vlsFilterByMax ? "max" : "min");
        }

        if (this.count > 0) {
            options.add(Keywords.COUNT.name());
            options.add(this.count);
        }

        if (this.align > 0) {
            options.add(Keywords.ALIGN.name());
            options.add(this.align);
        }

        if (this.aggregationType != null) {
            options.add(Keywords.AGGREGATION.name());
            options.add(this.aggregationType.getKey());
            options.add(this.timeBucket);
        }

        return options.toArray();
    }

    public Object[] mRange() {
        List<Object> options = new ArrayList<>();
        if (!Objects.isNull(tsFilters) && tsFilters.length > 0) {
            options.add(Keywords.FILTER_BY_TS.name());
            for (Long tsFilter : tsFilters) {
                options.add(tsFilter);
            }
        }
        if (!Objects.isNull(vlsFilterByMax)) {
            options.add(Keywords.FILTER_BY_VALUE.name());
            options.add(vlsFilterByMax ? "max" : "min");
        }

        if (this.withLabels) {
            options.add(Keywords.WITHLABELS.name());
        }

        if (this.count > 0) {
            options.add(Keywords.COUNT.name());
            options.add(this.count);
        }

        if (this.align > 0) {
            options.add(Keywords.ALIGN.name());
            options.add(this.align);
        }

        if (this.aggregationType != null) {
            options.add(Keywords.AGGREGATION.name());
            options.add(this.aggregationType.getKey());
            options.add(this.timeBucket);
        }

        if (!Objects.isNull(filters) && filters.length > 0) {
            options.add(Keywords.FILTER.name());

            for(int i = 0; i < this.filters.length; ++i) {
                Label label = this.filters[i];
                String express = String.format("%s%s%s",
                        label.getKey(), label.getOperator().getCode(), label.getValue());
                options.add(express);
            }
        }

        if (!StringUtils.isEmpty(groupBy)) {
            options.add(Keywords.GROUPBY.name());
            options.add(groupBy);
            options.add(Keywords.REDUCE.name());
            options.add(reduce.getKey());
        }

        return options.toArray();
    }

    public Object[] mGet() {
        List<Object> options = new ArrayList<>();

        if (this.withLabels) {
            options.add(Keywords.WITHLABELS.name());
        }

        if (this.filters != null && this.filters.length > 0) {
            options.add(Keywords.FILTER.name());

            for(int i = 0; i < this.filters.length; ++i) {
                Label label = this.filters[i];
                String express = String.format("%s%s%s",
                        label.getKey(), label.getOperator().getCode(), label.getValue());
                options.add(express);
            }
        }

        return options.toArray();
    }
}
