package org.uilib.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SmartExecutor implements Throttle, Executor {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(SmartExecutor.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ExecutorService executor					    = Executors.newCachedThreadPool();
	private final DelayQueue<DelayedRunnable> taskQueue		    = new DelayQueue<DelayedRunnable>();
	private final Map<String, ThrottledRunnable> throttledTasks = Maps.newHashMap();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	/* schedule a Runnable to be executed a fixed period of time after it was scheduled
	 * if a new Runnable with the same throttleName is scheduled before this one was called, it will overwrite this */
	public SmartExecutor() {
		this.executor.execute(new Scheduler());
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/* execute a Runnable once */
	@Override
	public void execute(final Runnable runnable) {
		this.executor.execute(runnable);
	}

	/* schedule a Runnable to be executed after a fixed period of time */
	public void schedule(final long delay, final TimeUnit timeUnit, final Runnable runnable) {
		this.taskQueue.put(new DelayedRunnable(runnable, delay, timeUnit));
	}

	/* schedule a Runnable to be executed using a fixed delay between the end of a run and the start of the next one */
	public void scheduleAtFixedRate(final long period, final TimeUnit timeUnit, final Runnable runnable) {
		this.taskQueue.put(new RepeatingRunnable(runnable, period, timeUnit));
	}

	/* shut the the executor down */
	public void shutdown() {
		this.executor.shutdownNow();
	}

	@Override
	public void throttle(final String throttleName, final long delay, final TimeUnit timeUnit, final Runnable runnable) {

		// FIXME: executor: TASKQUEUE is not THREADSAFE!
		final ThrottledRunnable thrRunnable = new ThrottledRunnable(runnable, throttleName, delay, timeUnit);
		this.throttledTasks.put(throttleName, thrRunnable);
		this.taskQueue.put(thrRunnable);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private static class DelayedRunnable implements Delayed, Runnable {

		protected final Runnable runnable;
		private final long endOfDelay;

		public DelayedRunnable(final Runnable runnable, final long delay, final TimeUnit delayUnit) {
			this.runnable	    = runnable;
			this.endOfDelay     = delayUnit.toMillis(delay) + System.currentTimeMillis();
		}

		@Override
		public int compareTo(final Delayed other) {

			final Long delay1 = this.getDelay(TimeUnit.MILLISECONDS);
			final Long delay2 = other.getDelay(TimeUnit.MILLISECONDS);

			return delay1.compareTo(delay2);
		}

		@Override
		public long getDelay(final TimeUnit unit) {
			return unit.convert(this.endOfDelay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}

		@Override
		public void run() {
			this.runnable.run();
		}
	}

	private static final class RepeatingRunnable extends DelayedRunnable {

		private final long periodInMillis;

		public RepeatingRunnable(final Runnable runnable, final long period, final TimeUnit delayUnit) {
			super(runnable, period, delayUnit);

			this.periodInMillis = delayUnit.convert(period, TimeUnit.MILLISECONDS);
		}

		public RepeatingRunnable reschedule() {
			return new RepeatingRunnable(this.runnable, this.periodInMillis, TimeUnit.MILLISECONDS);
		}
	}

	private final class Scheduler implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {

					/* wait for the next runnable to become available */
					final DelayedRunnable task = SmartExecutor.this.taskQueue.take();

					if (task instanceof RepeatingRunnable) {
						/* tell executor to run the action and reschedule it afterwards */
						SmartExecutor.this.executor.execute(
							new Runnable() {
									@Override
									public void run() {
										task.run();
										SmartExecutor.this.taskQueue.put(((RepeatingRunnable) task).reschedule());
									}
								});
					} else if (task instanceof ThrottledRunnable) {

						final ThrottledRunnable thrTask = (ThrottledRunnable) task;

						/* run only if this is the latest task in given throttle, otherwise skip execution */
						if (SmartExecutor.this.throttledTasks.get(thrTask.getThrottleName()) == thrTask) {
							SmartExecutor.this.executor.execute(task);
						}
					} else {
						/* tell the executor to just run the action */
						SmartExecutor.this.executor.execute(task);
					}
				} catch (final InterruptedException e) {
					SmartExecutor.L.debug("scheduler interrupted (shutting down)");
					return;
				}
			}
		}
	}

	private static final class ThrottledRunnable extends DelayedRunnable {

		private final String throttleName;

		public ThrottledRunnable(final Runnable runnable, final String throttleName, final long period,
								 final TimeUnit delayUnit) {
			super(runnable, period, delayUnit);

			this.throttleName = throttleName;
		}

		public String getThrottleName() {
			return this.throttleName;
		}
	}
}