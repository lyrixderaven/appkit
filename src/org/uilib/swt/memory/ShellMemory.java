package org.uilib.swt.memory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import org.uilib.swt.SWTSyncedRunnable;
import org.uilib.util.PrefStore;
import org.uilib.util.SmartExecutor;

// TODO: ShellMemory: Sorting
// TODO: ShellMemory: Builder
// TODO: ShellMemory: Monitor(size)
public final class ShellMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = Logger.getLogger(ShellMemory.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Shell shell;
	private final String memoryKey;
	private final int defaultX;
	private final int defaultY;
	private final int defaultWidth;
	private final int defaultHeight;
	private final boolean defaultMaximized;
	private final boolean sizeOnly;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private ShellMemory(final PrefStore prefStore, final Shell shell, final String memoryKey, final int defaultWidth,
						final int defaultHeight, final int defaultX, final int defaultY) {
		this(prefStore, shell, memoryKey, defaultWidth, defaultHeight, defaultX, defaultY, false, false);
	}

	private ShellMemory(final PrefStore prefStore, final Shell shell, final String memoryKey, final int defaultWidth,
						final int defaultHeight, final int defaultX, final int defaultY,
						final boolean defaultMaximized, final boolean sizeOnly) {
		this.prefStore										 = prefStore;
		this.shell											 = shell;
		this.memoryKey										 = memoryKey;
		this.defaultX										 = defaultX;
		this.defaultY										 = defaultY;
		this.defaultWidth									 = defaultWidth;
		this.defaultHeight									 = defaultHeight;
		this.defaultMaximized								 = defaultMaximized;
		this.sizeOnly										 = sizeOnly;

		if (! sizeOnly) {

			/* position shell */
			String posString	  = this.prefStore.get(memoryKey + ".position", "");
			List<String> position = Lists.newArrayList(Splitter.on(",").split(posString));
			if (position.size() == 2) {
				try {

					int x = Integer.valueOf(position.get(0));
					int y = Integer.valueOf(position.get(1));
					this.shell.setLocation(x, y);

				} catch (final NumberFormatException e) {
					this.shell.setLocation(this.defaultX, this.defaultY);
				}
			} else {
				this.shell.setLocation(this.defaultX, this.defaultY);
			}
		}

		/* size shell */
		String sizeString  = this.prefStore.get(memoryKey + ".size", "");
		List<String> sizes = Lists.newArrayList(Splitter.on(",").split(sizeString));
		if (sizes.size() == 2) {
			try {

				int width  = Integer.valueOf(sizes.get(0));
				int height = Integer.valueOf(sizes.get(1));
				this.shell.setSize(width, height);

			} catch (final NumberFormatException e) {
				this.shell.setSize(this.defaultWidth, this.defaultHeight);
			}
		} else {
			this.shell.setSize(this.defaultWidth, this.defaultHeight);
		}

		/* set maximize shell */
		boolean maximized = this.prefStore.get(memoryKey + ".maximized", this.defaultMaximized);
		this.shell.setMaximized(maximized);

		/* add listener */
		this.shell.addControlListener(new ShellChanged());
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void install(final PrefStore prefStore, final Shell shell, final String memoryKey,
							   final int defaultWidth, final int defaultHeight, final int defaultX, final int defaultY) {
		new ShellMemory(prefStore, shell, memoryKey, defaultWidth, defaultHeight, defaultX, defaultY, false, false);
	}

	public static void installSizeOnly(final PrefStore prefStore, final Shell shell, final String memoryKey,
									   final int defaultWidth, final int defaultHeight) {
		new ShellMemory(prefStore, shell, memoryKey, defaultWidth, defaultHeight, 0, 0, false, true);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	// FIXME: throttle call raus, swt Sync Runnable weg?
	private class ShellChanged implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {
			this.controlResized(event);
		}

		@Override
		public void controlResized(final ControlEvent event) {
			SmartExecutor.instance().throttle(
				memoryKey,
				50,
				TimeUnit.MILLISECONDS,
				new SWTSyncedRunnable() {
						@Override
						protected void runChecked() {
							if (shell.isDisposed()) {
								return;
							}

							prefStore.store(memoryKey + ".maximized", String.valueOf(shell.getMaximized()));

							/* if shell was maximized don't store position and size */
							if (shell.getMaximized()) {
								return;
							}

							/* if size only, don't store position */
							if (! sizeOnly) {

								Point pos = shell.getLocation();
								prefStore.store(memoryKey + ".position", Joiner.on(",").join(pos.x, pos.y));
							}

							Point size = shell.getSize();
							prefStore.store(memoryKey + ".size", Joiner.on(",").join(size.x, size.y));
						}
					});
		}
	}
}