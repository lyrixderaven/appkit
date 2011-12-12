package org.uilib.measure;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import java.util.Queue;

public final class Measurement {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final ThreadLocal<Queue<Measurement>> runningMeasurements =
		new ThreadLocal<Queue<Measurement>>() {
			@Override
			protected Queue<Measurement> initialValue() {
				return Lists.newLinkedList();
			}
		};

	private static final ThreadLocal<Measurement.Listener> listener			 =
		new ThreadLocal<Measurement.Listener>() {
			@Override
			protected Measurement.Listener initialValue() {
				return null;
			}
		};


	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final String description[];
	private final Stopwatch watch = new Stopwatch();
	private final long start;
	private final Object data;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Measurement(final String description[], final Object data) {
		this.start				  = System.currentTimeMillis();
		this.description		  = description;
		this.watch.start();
		this.data = data;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void notify(final Measurement.Listener listener) {
		Measurement.listener.set(listener);
	}

	public static MeasureData mark(final boolean doIt, String... description) {
		return mark(doIt, null, description);
	}

	public static MeasureData mark(final boolean doIt, final Object data, String... description) {
		if (! doIt) {
			return null;
		}

		MeasureData md = new MeasureData(description, data, System.currentTimeMillis());
		Listener l     = listener.get();
		if (l != null) {
			l.input(md);
		}

		return md;
	}

	public static void run(final boolean doIt, String... description) {
		run(doIt, null, description);
	}

	public static void run(final boolean doIt, final Object data, String... description) {
		if (! doIt) {
			return;
		}

		runningMeasurements.get().add(new Measurement(description, data));
	}

	public static MeasureData stop() {

		Queue<Measurement> rM = runningMeasurements.get();
		if (rM.isEmpty()) {
			return null;
		}

		MeasureData md = rM.poll().stopMeasurement();
		Listener l     = listener.get();
		if (l != null) {
			l.input(md);
		}

		return md;
	}

	private MeasureData stopMeasurement() {
		this.watch.stop();

		return new MeasureData(this.description, this.data, this.start, this.watch.elapsedMillis());
	}

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	public static interface Listener {
		void input(final MeasureData data);
	}
}