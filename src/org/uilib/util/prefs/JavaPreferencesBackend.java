package org.uilib.util.prefs;

import com.google.common.collect.ImmutableSet;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class JavaPreferencesBackend implements PrefStoreBackend {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Preferences prefs;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public JavaPreferencesBackend(final String node) {
		this.prefs = Preferences.userRoot().node(node);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public void store(final String key, final String value) {
		this.prefs.put(key, value);
	}

	@Override
	public String get(final String key) {
		return this.prefs.get(key, null);
	}

	@Override
	public ImmutableSet<String> getKeys() {
		try {

			ImmutableSet.Builder<String> keys = ImmutableSet.builder();

			for (final String key : this.prefs.keys()) {
				keys.add(key);
			}
			return keys.build();

		} catch (final BackingStoreException e) {
			return ImmutableSet.of();
		}
	}

	@Override
	public void remove(final String key) {
		this.prefs.remove(key);
	}
}