package org.uilib.measure;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import java.util.Map;

import org.apache.log4j.Logger;

public class SimpleStatistic implements Measurement.Listener {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(SimpleStatistic.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Map<String, Long> maxMap   = Maps.newHashMap();
	private final Map<String, Long> minMap   = Maps.newHashMap();
	private final Map<String, Long> countMap = Maps.newHashMap();
	private final Map<String, Long> sumMap   = Maps.newHashMap();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void input(final MeasureData data) {

		String description = Joiner.on(".").join(data.getDescription());

		Long max		   = this.maxMap.get(description);
		if ((max == null) || (data.getDuration() > max)) {
			max = data.getDuration();
		}
		this.maxMap.put(description, max);

		Long min = this.minMap.get(description);
		if ((min == null) || (data.getDuration() < min)) {
			min = data.getDuration();
		}
		this.minMap.put(description, min);

		Long oldCount = this.countMap.get(description);
		if (oldCount == null) {
			oldCount = 0L;
		}
		this.countMap.put(description, oldCount + 1);

		Long oldSum = this.sumMap.get(description);
		if (oldSum == null) {
			oldSum = 0L;
		}
		this.sumMap.put(description, oldSum + data.getDuration());

		Long avg		 = (oldSum + data.getDuration()) / (oldCount + 1);

		StringBuilder sb = new StringBuilder();
		sb.append("finished ");
		sb.append(description);
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