package org.appkit.measure;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import java.util.Queue;

/**
 * This static class starts, keeps track of and stops measurement. Measurement can be nested,
 * the internal state is managed using {@link ThreadLocal} variables.
 *
 * Every method has a boolean switch to turn off measurement, so it can be kept in the code
 * and turned of for performance reasons.
 *
 */
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

	private final String name;
	private final Stopwatch watch = new Stopwatch();
	private final long start;
	private final Object data;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	/** instatiate a measurement */
	private Measurement(final String name, final Object data) {
		this.start				  = System.currentTimeMillis();
		this.name				  = name;
		this.watch.start();
		this.data = data;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * sets a Listener to be notified of a new measurement
	 * @see SimpleStatistic
	 */
	public static void notify(final Measurement.Listener listener) {
		Measurement.listener.set(listener);
	}

	/**
	 * performs a zero-duration measurement = a mark
	 *
	 * @param doIt actually do the measurement
	 * @param name name of the measurement
	 * @return the finished measurement
	 */
	public static MeasureData mark(final boolean doIt, final String name) {
		return mark(doIt, null, name);
	}

	/**
	 * performs a zero-duration measurement = a mark and attach data to it
	 *
	 * @param doIt actually do the measurement
	 * @param name name of the measurement
	 * @param data data to be attached
	 * @return the finished measurement
	 */
	public static MeasureData mark(final boolean doIt, final String name, final Object data) {
		if (! doIt) {
			return null;
		}

		MeasureData md = new MeasureData(name, data, System.currentTimeMillis());
		Listener l     = listener.get();
		if (l != null) {
			l.notify(md);
		}

		return md;
	}

	/**
	 * starts a measurement with the given name
	 *
	 * @param doIt actually do the measurement
	 * @param name name of the measurement
	 */
	public static void run(final boolean doIt, final String name) {
		run(doIt, null, name);
	}

	/**
	 * starts a measurement with the given name and attached data
	 *
	 * @param doIt actually do the measurement
	 * @param name name of the measurement
	 */
	public static void run(final boolean doIt, final String name, final Object data) {
		if (! doIt) {
			return;
		}

		runningMeasurements.get().add(new Measurement(name, data));
	}

	/**
	 * stops the currently running measurement = the last that was started in this thread
	 *
	 * @return the finished measurement
	 */
	public static MeasureData stop() {

		Queue<Measurement> rM = runningMeasurements.get();
		if (rM.isEmpty()) {
			return null;
		}

		MeasureData md = rM.poll().stopMeasurement();
		Listener l     = listener.get();
		if (l != null) {
			l.notify(md);
		}

		return md;
	}

	private MeasureData stopMeasurement() {
		this.watch.stop();

		return new MeasureData(this.name, this.data, this.start, this.watch.elapsedMillis());
	}

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	public static interface Listener {
		void notify(final MeasureData data);
	}
}