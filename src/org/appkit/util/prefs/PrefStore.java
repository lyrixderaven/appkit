package org.appkit.util.prefs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A simple preferences store. All retrieval methods require a default to be specified, which will
 * be returned when the key isn't found or type-conversion from String fails.
 *
 */
public final class PrefStore {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStoreBackend backend;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public PrefStore(final PrefStoreBackend backend) {
		this.backend = backend;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	/**
	 * create a new PrefStore using the JavaPreferences back-end
	 *
	 * @param node preferred JavaPreferences node
	 *
	 */
	public static PrefStore createJavaPrefStore(final String node) {
		return new PrefStore(new JavaPreferencesBackend(node));
	}

	/**
	 * stores a String
	 */
	public void store(final String key, final String value) {
		this.backend.store(key, value);
	}

	/**
	 * stores an int
	 */
	public void store(final String key, final int value) {
		this.backend.store(key, String.valueOf(value));
	}

	/**
	 * retrieves a String
	 *
	 * @param def default to be returned if key wasn't found
	 */
	public String get(final String key, final String def) {

		String pref = this.backend.get(key);
		if (pref == null) {
			return def;
		}

		return pref;
	}

	/**
	 * retrieves an int
	 *
	 * @param def default to be returned if key wasn't found or {@link Integer#valueOf(int)} failed.
	 */
	public int get(final String key, final int def) {

		String pref = this.backend.get(key);
		if (pref == null) {
			return def;
		}

		try {
			return Integer.valueOf(pref);
		} catch (final NumberFormatException e) {
			return def;
		}
	}

	/**
	 * retrieves a boolean
	 *
	 * @param def default to be returned if key wasn't found or stored property is no boolean ("true" or "false")
	 */
	public boolean get(final String key, final boolean def) {

		String pref = this.backend.get(key);
		if (pref == null) {
			return def;
		}

		if (pref.equals("true")) {
			return Boolean.TRUE;
		} else if (pref.equals("false")) {
			return Boolean.FALSE;
		} else {
			return def;
		}
	}

	/**
	 * removes a property
	 */
	public void remove(final String property) {
		this.backend.remove(property);
	}

	/**
	 * returns all stored property-keys
	 */
	public ImmutableSet<String> getKeys() {
		return this.backend.getKeys();
	}

	/**
	 * returns all stored property-keys starting with a string
	 */
	public ImmutableSet<String> getKeys(final String match) {

		ImmutableSet.Builder<String> matchingKeys = ImmutableSet.builder();
		for (final String key : this.backend.getKeys()) {
			if (key.startsWith(match)) {
				matchingKeys.add(key);
			}
		}

		return matchingKeys.build();
	}

	/**
	 * returns all stored properties as a map
	 */
	public ImmutableMap<String, String> getMap() {

		ImmutableMap.Builder<String, String> hm = ImmutableMap.builder();
		for (final String key : this.getKeys()) {
			hm.put(key, this.get(key, null));
		}

		return hm.build();
	}

	/**
	 * returns all stored properties starting with a string as a map
	 */
	public ImmutableMap<String, String> getMap(final String match) {

		ImmutableMap.Builder<String, String> hm = ImmutableMap.builder();
		for (final String key : this.getKeys(match)) {
			hm.put(key, this.get(key, null));
		}

		return hm.build();
	}

	/**
	 * returns all stored properties as a map of integer
	 *
	 * @param def map-value if matched property couldn't be converted to an integer
	 */
	public ImmutableMap<String, Integer> getMap(final int def) {

		ImmutableMap.Builder<String, Integer> hm = ImmutableMap.builder();
		for (final String key : this.getKeys()) {
			hm.put(key, this.get(key, def));
		}

		return hm.build();
	}

	/**
	 * returns all stored properties,  as a map of integer
	 *
	 * @param def map-value if matched property couldn't be converted to an integer
	 */
	public ImmutableMap<String, Integer> getMap(final String match, final int def) {

		ImmutableMap.Builder<String, Integer> hm = ImmutableMap.builder();
		for (final String key : this.getKeys(match)) {
			hm.put(key, this.get(key, def));
		}

		return hm.build();
	}

	public ImmutableMap<String, Boolean> getMap(final boolean def) {

		ImmutableMap.Builder<String, Boolean> hm = ImmutableMap.builder();
		for (final String key : this.getKeys()) {
			hm.put(key, this.get(key, def));
		}

		return hm.build();
	}

	public ImmutableMap<String, Boolean> getMap(final String match, final boolean def) {

		ImmutableMap.Builder<String, Boolean> hm = ImmutableMap.builder();
		for (final String key : this.getKeys(match)) {
			hm.put(key, this.get(key, def));
		}

		return hm.build();
	}
}