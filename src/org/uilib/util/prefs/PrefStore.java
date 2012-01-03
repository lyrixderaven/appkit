package org.uilib.util.prefs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class PrefStore {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final PrefStoreBackend backend;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public PrefStore(final PrefStoreBackend backend) {
		this.backend = backend;
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static PrefStore createJavaPrefStore(final String node) {
		return new PrefStore(new JavaPreferencesBackend(node));
	}

	public void store(final String property, final String value) {
		this.backend.store(property, value);
	}

	public void store(final String property, final int value) {
		this.backend.store(property, String.valueOf(value));
	}

	public String get(final String key, final String def) {

		String pref = this.backend.get(key);
		if (pref == null) {
			return def;
		}

		return pref;
	}

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

	public void remove(final String property) {
		this.backend.remove(property);
	}

	public ImmutableSet<String> getKeys() {
		return this.backend.getKeys();
	}

	public ImmutableSet<String> getKeys(final String match) {

		ImmutableSet.Builder<String> matchingKeys = ImmutableSet.builder();
		for (final String key : this.backend.getKeys()) {
			if (key.startsWith(match)) {
				matchingKeys.add(key);
			}
		}

		return matchingKeys.build();
	}

	public ImmutableMap<String, String> getMap() {

		ImmutableMap.Builder<String, String> hm = ImmutableMap.builder();
		for (final String key : this.getKeys()) {
			hm.put(key, this.get(key, null));
		}

		return hm.build();
	}

	public ImmutableMap<String, String> getMap(final String match) {

		ImmutableMap.Builder<String, String> hm = ImmutableMap.builder();
		for (final String key : this.getKeys(match)) {
			hm.put(key, this.get(key, null));
		}

		return hm.build();
	}

	public ImmutableMap<String, Integer> getMap(final int def) {

		ImmutableMap.Builder<String, Integer> hm = ImmutableMap.builder();
		for (final String key : this.getKeys()) {
			hm.put(key, this.get(key, def));
		}

		return hm.build();
	}

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