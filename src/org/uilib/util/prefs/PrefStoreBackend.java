package org.uilib.util.prefs;

import com.google.common.collect.ImmutableSet;

/**
 * backend for a preferences store
 *
 * @see PrefStore
 *
 */
public interface PrefStoreBackend {

	//~ Methods --------------------------------------------------------------------------------------------------------

	/** retrieve data for a key */
	public String get(final String key);

	/** stores data for a key */
	public void store(final String key, final String value);

	/** returns all keys */
	public ImmutableSet<String> getKeys();

	/** removes a key */
	void remove(final String key);
}