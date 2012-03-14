package org.appkit.registry;

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

import org.appkit.util.ParamSupplier;
import org.appkit.util.ResourceStreamSupplier;

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

/** <b>SWT Image cache/registry</b>
 * <br />
 * <br />
 * Creates, assigns and caches {@link Image}s. Images can be set on a {@link Control}.
 * Use of an image is deregistered when the control is disposed or manually via the <code>putBack</code> methods.
 * <br />
 * <br />
 * This uses a simple counter to keep of track of usage of Images. If the usage drops to 0, the image
 * is disposed.
 * <br />
 * <br />
 * The methods expect {@link Supplier}s for keys. This can be implemented easily by an Enum for example.
 */
public final class Images {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = LoggerFactory.getLogger(Images.class);

	/* cache / registry */
	private static final BiMap<Integer, Image> imageCache = HashBiMap.create();
	private static final Multiset<Image> usage			  = HashMultiset.create();

	/* currently installed disposeListeners */
	private static final Map<Control, DisposeListener> disposeListeners = Maps.newHashMap();

	/* setters for images */
	private static final Map<Class<?>, ImageInterface> setters = Maps.newHashMap();

	static {
		Preconditions.checkArgument(Display.getCurrent() != null, "can't instantiate Images on a non-display thread");
		addImageSetter(Button.class, new ButtonImageInterface());
		addImageSetter(Label.class, new LabelImageInterface());
		addImageSetter(Shell.class, new ShellImageInterface());
	}

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Images() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * registers an ImageInterface for a control
	 */
	public static <E extends Object> void addImageSetter(final Class<E> clazz, final ImageInterface setter) {
		setters.put(clazz, setter);
	}

	/**
	 * Sets an image on the control. The InputStream for loading the image
	 * is retrieved by passing the key received from the
	 * <code>keySupplier</code> into the a {@link ResourceStreamSupplier}.
	 *
	 * @throws IllegalStateException if called from a non-Display thread
	 * @throws IllegalArgumentException if image couldn't be set
	 */
	public static void set(final Control control, final Supplier<String> keySupplier) {
		set(control, keySupplier, ResourceStreamSupplier.create());
	}

	/**
	 * sets an image on the control. The InputStream for loading the image
	 * is retrieved by passing the key received from the
	 * <code>keySupplier</code> into the <code>dataSupplier</code>
	 *
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
			setters.containsKey(control.getClass()),
			"don't know how to set image on {}, add a image-setter first",
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
		setters.get(control.getClass()).setImage(control, image);

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

	//~ Inner Interfaces -----------------------------------------------------------------------------------------------

	public static interface ImageInterface {
		void setImage(final Object control, final Image image);

		Image getImage(final Object control);
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private static final class ImageDisposeListener implements DisposeListener {
		@Override
		public void widgetDisposed(final DisposeEvent event) {
			putBack((Control) event.widget);
		}
	}

	private static final class LabelImageInterface implements ImageInterface {
		@Override
		public void setImage(final Object o, final Image image) {
			((Label) o).setImage(image);
		}

		@Override
		public Image getImage(final Object o) {
			return ((Label) o).getImage();
		}
	}

	private static final class ButtonImageInterface implements ImageInterface {
		@Override
		public void setImage(final Object o, final Image image) {
			((Button) o).setImage(image);
		}

		@Override
		public Image getImage(final Object o) {
			return ((Button) o).getImage();
		}
	}

	private static final class ShellImageInterface implements ImageInterface {
		@Override
		public void setImage(final Object o, final Image image) {
			((Shell) o).setImage(image);
		}

		@Override
		public Image getImage(final Object o) {
			return ((Shell) o).getImage();
		}
	}
}