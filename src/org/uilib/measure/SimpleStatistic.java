package org.uilib.measure;

import com.google.common.collect.Maps;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a simple Measurement.Listener which keeps track of max,min and avg values of each measurement
 * with a specific name
 *
 */
public class SimpleStatistic implements Measurement.Listener {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(SimpleStatistic.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Map<String, Long> maxMap   = Maps.newHashMap();
	private final Map<String, Long> minMap   = Maps.newHashMap();
	private final Map<String, Long> countMap = Maps.newHashMap();
	private final Map<String, Long> sumMap   = Maps.newHashMap();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void notify(final MeasureData data) {

		String name = data.getName();

		Long max    = this.maxMap.get(name);
		if ((max == null) || (data.getDuration() > max)) {
			max = data.getDuration();
		}
		this.maxMap.put(name, max);

		Long min = this.minMap.get(name);
		if ((min == null) || (data.getDuration() < min)) {
			min = data.getDuration();
		}
		this.minMap.put(name, min);

		Long oldCount = this.countMap.get(name);
		if (oldCount == null) {
			oldCount = 0L;
		}
		this.countMap.put(name, oldCount + 1);

		Long oldSum = this.sumMap.get(name);
		if (oldSum == null) {
			oldSum = 0L;
		}
		this.sumMap.put(name, oldSum + data.getDuration());

		Long avg		 = (oldSum + data.getDuration()) / (oldCount + 1);

		StringBuilder sb = new StringBuilder();
		sb.append("finished ");
		sb.append(name);
		sb.append(": ");
		sb.append(data.getDuration());
		sb.append(" (");
		sb.append(min);
		sb.append(" <  ~");
		sb.append(avg);
		sb.append("  > ");
		sb.append(max);
		sb.append(")");

		L.debug(sb.toString());
	}
}