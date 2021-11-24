package org.springframework.data.redis.core.decode;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.protocol.entity.TimeSeries;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.util.*;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class TimeSeriesDecoder extends AbstractDecoder<Object, List<TimeSeries>> {
    private LabelDecoder labelDecoder;
    private SampleDecoder sampleDecoder;

    public TimeSeriesDecoder(RedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    public void setLabelDecoder(LabelDecoder labelDecoder) {
        this.labelDecoder = labelDecoder;
    }

    public void setSampleDecoder(SampleDecoder sampleDecoder) {
        this.sampleDecoder = sampleDecoder;
    }

    @Override
    public List<TimeSeries> decode(Object key, Object value) {
        List<TimeSeries> timeSeries = new ArrayList<>();
        List parts = (List)value;

        Iterator iter = parts.iterator();

        while(iter.hasNext()) {
            Object part = iter.next();
            List<Object> o = (List)part;
            List<Value> vls = new ArrayList<>();
            String iKey = new String((byte[])o.get(0));
            TimeSeries series = new TimeSeries(iKey);
            // todo label decode
            if (!Objects.isNull(o.get(1))) {
                List labels = (List)o.get(1);
                List data = new ArrayList(labels.size());
                for (Object label : labels) {
                    data.add(this.labelDecoder.decode(null, label));
                }
                series.labels(data);
            }
            List<Object> m = (List)o.get(2);
            if (!Objects.isNull(m) && m.size() > 0) {
                if (m.get(0) instanceof List) {
                    ((List) o.get(2)).forEach((valueObject) -> {
                        vls.add(this.sampleDecoder.decode(iKey, valueObject));
                    });
                } else {
                    vls.add(this.sampleDecoder.decode(iKey, o.get(2)));
                }
            }

            series.results(vls);
            timeSeries.add(series);
        }

        return timeSeries;
    }
}
