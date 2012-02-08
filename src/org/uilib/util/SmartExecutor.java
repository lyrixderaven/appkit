package org.uilib.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Executor that provides commonly-used methods for running Runnables and is a {@link Throttle}.
 * It uses a Scheduler-Thread to schedule and run tasks.
 *
 */
public final class SmartExecutor implements Throttle, Executor {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(SmartExecutor.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	/* can be null if executorService wasn't created here */
	private final ExecutorService executorService;
	private final Executor executor;
	private final DelayQueue<DelayedRunnable> taskQueue = new DelayQueue<DelayedRunnable>();
	private final Map<String, ThrottledRunnable> throttledTasks = Maps.newHashMap();
	private final Set<Runnable> cancelledTasks = Sets.newHashSet();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private SmartExecutor(final Executor executor) {
		if (executor != null) {
			this.executorService     = null;
			this.executor			 = executor;
		} else {
			this.executorService     = Executors.newCachedThreadPool();
			this.executor			 = this.executorService;

		}
		this.executor.execute(new Scheduler());
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/** creates a new SmartExecutor with a cached thread-pool. It has to be shutdown after use. */
	public static SmartExecutor create() {
		return new SmartExecutor(null);
	}

	/** creates a new SmartExecutor using the given executor */
	public static SmartExecutor create(final Executor executor) {
		return new SmartExecutor(executor);
	}

	/** shut the executor down
	 *
	 * @throws IllegalStateException if this executor was created on top of another
	 *
	 */
	public void shutdown() {
		Preconditions.checkState(
			this.executorService != null,
			"executor-service wasn't created within this instance, dispose it yourself!");
		this.executorService.shutdownNow();
	}

	/** execute a Runnable once */
	@Override
	public void execute(final Runnable runnable) {
		this.executor.execute(runnable);
	}

	/** schedule a Runnable to be executed after a fixed period of time */
	public void schedule(final long delay, final TimeUnit timeUnit, final Runnable runnable) {
		this.taskQueue.put(new DelayedRunnable(runnable, delay, timeUnit));
	}

	/** schedule a Runnable to be executed using a fixed delay between the end of a run and the start of the next */
	public void scheduleAtFixedRate(final long period, final TimeUnit timeUnit, final Runnable runnable) {
		this.taskQueue.put(new RepeatingRunnable(runnable, period, timeUnit));
	}

	/** cancel a scheduled repeating runnable */
	public void cancelRepeatingRunnable(final Runnable runnable) {
		this.cancelledTasks.add(runnable);
	}

	@Override
	public void throttle(final String throttleName, final long delay, final TimeUnit timeUnit, final Runnable runnable) {

		ThrottledRunnable thrTask = new ThrottledRunnable(runnable, throttleName, delay, timeUnit);
		this.throttledTasks.put(thrTask.getThrottleName(), thrTask);
		this.taskQueue.put(thrTask);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private final class Scheduler implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {

					/* wait for the next runnable to become available */
					final DelayedRunnable task = SmartExecutor.this.taskQueue.take();

					if (task instanceof RepeatingRunnable) {

						/* if runnable wasn't cancelled tell executor to run the action and reschedule it afterwards */
						if (! cancelledTasks.contains(task.getRunnable())) {
							SmartExecutor.this.executor.execute(
								new Runnable() {
										@Override
										public void run() {
											task.run();
											SmartExecutor.this.taskQueue.put(((RepeatingRunnable) task).reschedule());
										}
									});
						}
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
				}
			} catch (final InterruptedException e) {
				SmartExecutor.L.debug("scheduler interrupted (shutting down)");
				return;
			}
		}
	}

	/** delayed runnable */
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

		public Runnable getRunnable() {
			return this.runnable;
		}

		@Override
		public void run() {
			this.runnable.run();
		}
	}

	/** repeating runnable */
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

	/** throttled runnable */
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