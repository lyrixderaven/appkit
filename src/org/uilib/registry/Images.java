package org.uilib.registry;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uilib.util.ParamSupplier;
import org.uilib.util.ResourceStreamSupplier;

/** <b>SWT Image cache/registry</b>
 *
 * Creates, assigns and caches {@link Image}s. Images can be set on a {@link Control}.
 * Use of an image is deregistered when the control is disposed or manually via the <code>putBack</code> methods.
 *
 * This uses a simple counter to keep of track of usage of Images. If the usage drops to 0, the image
 * is disposed.
 *
 * The methods expect {@link Supplier}s for keys. This can be implemented easily by an Enum for example.
 *
 * <b>TODO:</b>"ImageSetable" interface to set Image on arbitrary things and more controls
 */
public final class Images {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Images.class);

	/* cache / registry */
	private static final BiMap<Integer, Image> imageCache			    = HashBiMap.create();
	private static final Multiset<Image> usage						    = HashMultiset.create();
	private static final Map<Control, DisposeListener> disposeListeners = Maps.newHashMap();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Images() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * Sets an image on the control. The InputStream for loading the image
	 * is retrieved by passing the key received from the
	 * <code>keySupplier</code> into the a {@link ResourceStreamSupplier}.
	 */
	public static void set(final Control control, final Supplier<String> keySupplier) {
		set(control, keySupplier, ResourceStreamSupplier.create());
	}

	/**
	 * sets an image on the control. The InputStream for loading the image
	 * is retrieved by passing the key received from the
	 * <code>keySupplier</code> into the <code>dataSupplier</code>
	 *
	 * <b>TODO</b>This works only for {@link Button}s, {@link org.eclipse.swt.widgets.Text}s and {@link Label}s at the moment
	 * @throws IllegalStateException if called from a non-Display thread
	 * @throws IllegalArgumentException if image couldn't be set
	 */
	public static <E> void set(final Control control, final Supplier<E> keySupplier,
							   final ParamSupplier<E, InputStream> dataSupplier) {
		/* check for UI-thread and if control is imageable */
		Preconditions.checkState(
			Display.getCurrent() != null,
			"Images is to be used from the display-thread exclusively!");
		Preconditions.checkArgument(
			control instanceof Label || control instanceof Button || control instanceof Shell,
			"don't know how to set image on {}",
			control);

		/* if we already set an image on this control, remove it */
		if (disposeListeners.containsKey(control)) {
			putBack(control);
		}

		/* get image out of cache or load it */
		final E key		  = keySupplier.get();
		int hash		  = Objects.hashCode(key);
		final Image image;

		L.debug("setting image {} on {}", key, control);
		if (imageCache.containsKey(hash)) {
			image		  = imageCache.get(hash);

		} else {

			InputStream in = dataSupplier.get(key);
			if (in == null) {
				L.error("data supplier returned no InputStream for {}", key);
				return;
			}

			image = new Image(Display.getCurrent(), in);
			L.debug("created image: {}", image);
			try {
				in.close();
			} catch (final IOException e) {
				L.error(e.getMessage(), e);
			}

			imageCache.put(hash, image);
		}

		/* increase usage-counter */
		usage.setCount(image, usage.count(image) + 1);
		L.debug("usage of {} now {}", image, usage.count(image));

		/* set image */
		if (control instanceof Label) {
			((Label) control).setImage(image);
		} else if (control instanceof Button) {
			((Button) control).setImage(image);
		} else if (control instanceof Shell) {
			((Shell) control).setImage(image);
		} else {
			throw new IllegalStateException();
		}

		/* and add the disposer */
		DisposeListener listener = new ImageDisposeListener();
		disposeListeners.put(control, listener);
		control.addDisposeListener(listener);
	}

	/**
	 * deregisters use of an image of a control
	 *
	 * @throws IllegalStateException if control isn't registered
	 */
	public static void putBack(final Control control) {
		Preconditions.checkState(disposeListeners.containsKey(control), "control {} not registered", control);

		/* remove control out of registry and remove listener */
		control.removeDisposeListener(disposeListeners.remove(control));

		/* get the image */
		Image image;
		if (control instanceof Label) {
			image = ((Label) control).getImage();
		} else if (control instanceof Button) {
			image = ((Button) control).getImage();
		} else if (control instanceof Shell) {
			image = ((Shell) control).getImage();
		} else {
			throw new IllegalStateException();
		}

		/* decrease usage-counter */
		usage.setCount(image, usage.count(image) - 1);
		L.debug("usage of {} now {}", image, usage.count(image));

		/* if usage is 0 dispose it */
		if (! usage.contains(image)) {
			L.debug("disposing {}", image);
			imageCache.inverse().remove(image);
			image.dispose();
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private static final class ImageDisposeListener implements DisposeListener {
		@Override
		public void widgetDisposed(final DisposeEvent event) {
			putBack((Control) event.widget);
		}
	}
}