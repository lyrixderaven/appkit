package org.appkit.util;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This serves as a generic dictionary which allows objects to be registered under names.
 * The methods for retrieving the values allow you to specify a class which is assignable
 * from the object. The object will be cast to the class upon retrieval.
 *
 */
public final class Naming<E> {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(Naming.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	/* dictionary */
	private final Multimap<String, E> data = HashMultimap.create();

	/* cache, so that we don't have to check for the instances all the time */
	private Map<Integer, ImmutableSet<?extends E>> cache;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {

		StringBuilder sb				   = new StringBuilder();

		sb.append("data:\n");

		List<String> keys = Lists.newArrayList(this.data.keySet());
		Collections.sort(keys, Ordering.natural());
		for (final String k : keys) {
			sb.append("\t");
			sb.append(k);
			sb.append(": ");
			sb.append(this.data.get(k));
			sb.append("\n");
		}

		return sb.toString();
	}

	/** creates a new Naming instance */
	public static <T> Naming<T> create() {
		return new Naming<T>();
	}

	/** seals the naming, so queries are allowed to be cached
	 *
	 * @throws IllegalStateException if naming was already sealed
	 */
	public void seal() {
		Preconditions.checkState(! this.isSealed(), "naming was already sealed");

		this.cache = Maps.newHashMap();
	}

	/** returns if naming was sealed */
	public boolean isSealed() {
		return (this.cache != null);
	}

	/** registers a new object with the given name
	 *
	 * @throws IllegalStateException if naming was already sealed
	 * @throws IllegalArgumentException if name or object is null
	 *
	 */
	public void register(final String name, final E object) {
		Preconditions.checkState(! this.isSealed(), "naming was already sealed");
		Preconditions.checkArgument((object != null) && (name != null), "null-names or objects are not allowedl");

		/* reference-able via exact name */
		this.data.put(name, object);
	}

	public ImmutableMultimap<String, E> asMap() {
		return ImmutableMultimap.copyOf(this.data);
	}

	/**
	 * selects the object matching a class
	 *
	 * @throws IllegalStateException if not exactly 1 was found
	 *
	 */
	public <T extends E> T select(final Class<T> clazz) {
		return this.select(null, clazz);
	}

	/**
	 * checks if exactly 1 object with the given name and class exists
	 */
	public <T extends E> boolean exists(final String name, final Class<T> clazz) {
		try {
			this.select(name, clazz);

			return true;
		} catch (final IllegalStateException e) {
			return false;
		}
	}

	/**
	 * checks if exactly 1 object with the given class exists
	 */
	public <T extends E> boolean exists(final Class<T> clazz) {
		return this.exists(null, clazz);
	}

	/** returns all objects matching a class */
	public <T extends E> ImmutableSet<T> find(final Class<T> clazz) {
		return this.find(null, clazz);
	}

	/**
	 * selects the object matching a class and a name
	 *
	 * @throws IllegalStateException if not exactly 1 was found
	 *
	 */
	public <T extends E> T select(final String name, final Class<T> clazz) {

		ImmutableSet<T> results = this.find(name, clazz);

		Preconditions.checkState(
			results.size() == 1,
			"search for type %s name '%s' returned %s results instead of exactly 1",
			clazz.getSimpleName(),
			name,
			results.size());

		return results.iterator().next();
	}

	/** returns all objects matching a class and a name */
	@SuppressWarnings("unchecked")
	public <T extends E> ImmutableSet<T> find(final String name, final Class<T> clazz) {

		/* cache: hash the request and check if we have an entry */
		int hash = Objects.hashCode(name, clazz);
		if ((this.cache != null) && this.cache.containsKey(hash)) {
			return (ImmutableSet<T>) this.cache.get(hash);
		}

		/* find results */
		Collection<E> objects;
		if (name == null) {
			objects = this.data.values();
		} else {
			objects = this.data.get(name);
		}

		/* of these, find results that match given type */
		ImmutableSet.Builder<T> builder = ImmutableSet.builder();
		for (final E object : objects) {
			if (clazz.isAssignableFrom(object.getClass())) {
				builder.add((T) object);
			}
		}

		ImmutableSet<T> results = builder.build();

		/* cache: save results */
		if (this.cache != null) {
			this.cache.put(hash, results);
		}

		return results;
	}
}