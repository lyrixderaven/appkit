package org.uilib.registry;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.io.IOException;
import java.io.InputStream;

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

public final class Images {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L					   = LoggerFactory.getLogger(Images.class);
	private static final BiMap<Object, Image> registry = HashBiMap.create();
	private static final Multiset<Image> usage		   = HashMultiset.create();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	private Images() {}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static void set(final Control control, final Supplier<String> keySupplier) {
		set(control, keySupplier, ResourceStreamSupplier.create());
	}

	public static <E> void set(final Control control, final Supplier<E> keySupplier,
							   final ParamSupplier<E, InputStream> dataSupplier) {
		Preconditions.checkState(
			Display.getDefault().getThread() == Thread.currentThread(),
			"Images is to be used from the display-thread exclusively!");
		Preconditions.checkState(
			control instanceof Label || control instanceof Button || control instanceof Shell,
			"don't know how to set image on {}",
			control);

		E key = keySupplier.get();

		L.debug("loading image {} for {}", key, control);

		/* get image out of cache or load it */
		final Image image;
		if (registry.containsKey(key)) {
			image = registry.get(key);

		} else {

			InputStream in = dataSupplier.get(key);
			if (in == null) {
				L.error("data supplier returned no InputStream for {}", key);
				return;
			}

			image = new Image(Display.getDefault(), in);
			L.debug("created image {}: {}", key, image);
			try {
				in.close();
			} catch (final IOException e) {
				L.error(e.getMessage(), e);
			}

			registry.put(key, image);
		}

		/* register usage */
		usage.setCount(image, usage.count(image) + 1);
		L.debug("usage of {} now {}", image, usage.count(image));

		/* set it */
		if (control instanceof Label) {
			((Label) control).setImage(image);
		} else if (control instanceof Button) {
			((Button) control).setImage(image);
		} else if (control instanceof Shell) {
			((Shell) control).setImage(image);
		} else {
			throw new IllegalStateException();
		}

		/* and add the diposer */
		control.addDisposeListener(
			new DisposeListener() {
					@Override
					public void widgetDisposed(final DisposeEvent event) {
						/* deregister usage */
						usage.setCount(image, usage.count(image) - 1);
						L.debug("usage of {} now {}", image, usage.count(image));

						if (! usage.contains(image)) {
							L.debug("disposing {}", image);

							/* remove first, font appears to change identity after disposal, maybe image does the same */
							registry.inverse().remove(image);
							image.dispose();
						}
					}
				});
	}
}