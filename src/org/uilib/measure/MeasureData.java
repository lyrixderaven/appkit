package org.uilib.measure;

public final class MeasureData {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String description[];
	private final Object data;
	private final long start;
	private final long duration;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	/* for marker */
	public MeasureData(final String description[], final Object data, final long start) {
		this(description, data, start, 0);
	}

	public MeasureData(final String description[], final Object data, final long start, final long duration) {
		this.description     = description;
		this.data			 = data;
		this.start			 = start;
		this.duration		 = duration;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String[] getDescription() {
		return description;
	}

	public long getStart() {
		return start;
	}

	public long getDuration() {
		return duration;
	}

	public Object getData() {
		return data;
	}
}