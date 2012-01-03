package org.uilib.util.prefs;

import com.google.common.collect.ImmutableSet;

public interface PrefStoreBackend {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public String get(final String key);

	public void store(final String key, final String value);

	public ImmutableSet<String> getKeys();

	void remove(final String key);
}