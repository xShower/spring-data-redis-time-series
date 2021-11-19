package org.springframework.data.redis.core.decode;

import org.springframework.data.redis.core.protocol.entity.TimeSeries;
import org.springframework.data.redis.core.protocol.entity.Value;

import java.util.*;

/**
 * @Auther: syh
 * @Date: 2021/11/18
 * @Description:
 */
public class TimeSeriesDecoder implements Decoder<Object, List<TimeSeries>> {
    private LabelDecoder labelDecoder = new LabelDecoder();
    private SampleDecoder sampleDecoder = new SampleDecoder();

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
            // series.labels(this.labelDecoder.decode(null, o.get(1)));
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
