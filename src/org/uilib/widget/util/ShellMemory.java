package org.uilib.widget.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.LoggingRunnable;
import org.uilib.util.SWTSyncedRunnable;
import org.uilib.util.SmartExecutor;
import org.uilib.util.Throttle;
import org.uilib.util.prefs.PrefStore;

public final class ShellMemory {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L		   = LoggerFactory.getLogger(ShellMemory.class);
	private static final int THROTTLE_TIME = 100;

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStore prefStore;
	private final Throttle throttle;
	private final Shell shell;
	private final String memoryKey;
	private final int defaultX;
	private final int defaultY;
	private final int defaultWidth;
	private final int defaultHeight;
	private final boolean defaultMaximized;
	private final boolean sizeOnly;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	protected ShellMemory(final PrefStore prefStore, final SmartExecutor executor, final Shell shell,
						  final String memoryKey, final int defaultWidth, final int defaultHeight, final int defaultX,
						  final int defaultY) {
		this(prefStore, executor, shell, memoryKey, defaultWidth, defaultHeight, defaultX, defaultY, false, false);
	}

	protected ShellMemory(final PrefStore prefStore, final SmartExecutor executor, final Shell shell,
						  final String memoryKey, final int defaultWidth, final int defaultHeight, final int defaultX,
						  final int defaultY, final boolean defaultMaximized, final boolean sizeOnly) {
		this.prefStore			  = prefStore;
		this.throttle			  = executor.createThrottle(THROTTLE_TIME, TimeUnit.MILLISECONDS);
		this.shell				  = shell;
		this.memoryKey			  = memoryKey;
		this.defaultX			  = defaultX;
		this.defaultY			  = defaultY;
		this.defaultWidth		  = defaultWidth;
		this.defaultHeight		  = defaultHeight;
		this.defaultMaximized     = defaultMaximized;
		this.sizeOnly			  = sizeOnly;

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

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private class ShellChanged implements ControlListener {
		@Override
		public void controlMoved(final ControlEvent event) {
			this.controlResized(event);
		}

		@Override
		public void controlResized(final ControlEvent event) {

			final String maximizedString = String.valueOf(shell.getMaximized());
			final String positionString;
			final String sizeString;

			/* if shell was maximized don't store position and size */
			if (shell.getMaximized()) {
				positionString			 = null;
				sizeString				 = null;
			} else {

				/* if size only, don't store position */
				if (sizeOnly) {
					positionString = null;
				} else {

					Point pos = shell.getLocation();
					positionString = Joiner.on(",").join(pos.x, pos.y);
				}

				Point size = shell.getSize();
				sizeString = Joiner.on(",").join(size.x, size.y);
			}

			Runnable runnable =
				new LoggingRunnable() {
					@Override
					public void runChecked() {
						L.debug("writing out maximized {} to key {}", maximizedString, memoryKey);
						prefStore.store(memoryKey + ".maximized", maximizedString);

						if (positionString != null) {
							L.debug("writing out position {} to key {}", positionString, memoryKey);
							prefStore.store(memoryKey + ".position", positionString);
						}

						if (sizeString != null) {
							L.debug("writing out size {} to key {}", sizeString, memoryKey);
							prefStore.store(memoryKey + ".size", sizeString);
						}
					}
				};

			throttle.schedule(new SWTSyncedRunnable(Display.getCurrent(), runnable));

		}
	}
}