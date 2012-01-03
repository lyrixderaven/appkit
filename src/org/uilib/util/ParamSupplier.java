package org.uilib.util;

public interface ParamSupplier<K, V> {

	//~ Methods --------------------------------------------------------------------------------------------------------

	public V get(final K key);
}