package org.uilib.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Naming<E> {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(Naming.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final CharMatcher nameFilter =
		CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('0', '9')).or(CharMatcher.anyOf("#.?-"));
	private final NamingFunction namingFunc = new NamingFunction();

	/* dictionary */
	private final Multimap<String, E> nameMap   = HashMultimap.create();
	private final Multimap<String, E> prefixMap = HashMultimap.create();

	/* cache, so that we don't have to check for the instances all the time */
	private Map<Integer, ImmutableSet<?extends E>> cache;

	//~ Methods --------------------------------------------------------------------------------------------------------

	/** creates a new Naming instance */
	public static <T> Naming<T> create() {
		return new Naming<T>();
	}

	/** registers a new object in the name-space */
	public void register(final E object, final String name) {
		Preconditions.checkArgument(
			this.nameFilter.matchesAllOf(name),
			"'%s' didn't satisfy name-filter (a-z 0-9 #.?-)",
			name);

		/* reference-able via name */
		this.nameMap.put(name, object);

		/* reference-able via all prefixes and "" */
		for (final String prefix : this.namingFunc.apply(name)) {
			this.prefixMap.put(prefix, object);
		}
	}

	/** selects an object matching a class */
	public <T extends E> T select(final Class<T> clazz) {
		return this.select("", clazz);
	}

	/** finds all objects matching a class */
	public <T extends E> ImmutableSet<T> find(final Class<T> clazz) {
		return this.find("", clazz);
	}

	/** selects the object matching a class within a name-space */
	public <T extends E> T select(final String searchSpace, final Class<T> clazz) {

		ImmutableSet<T> results = this.find(searchSpace, clazz, false);

		Preconditions.checkState(
			results.size() == 1,
			"search for type %s in '%s' returned %s results",
			clazz.getSimpleName(),
			searchSpace,
			results.size());

		return results.iterator().next();
	}

	/** selects the object matching a class and a name */
	public <T extends E> T exactSelect(final String name, final Class<T> clazz) {

		ImmutableSet<T> results = this.find(name, clazz, true);

		Preconditions.checkState(
			results.size() == 1,
			"search for type %s called '%s' returned %s results",
			clazz.getSimpleName(),
			name,
			results.size());

		return results.iterator().next();
	}

	/** finds all objects matching a class within a name-space */
	public <T extends E> ImmutableSet<T> find(final String nameSpace, final Class<T> clazz) {
		return this.find(nameSpace, clazz, false);
	}

	/** finds all objects matching a class and a name */
	public <T extends E> ImmutableSet<T> exactFind(final String name, final Class<T> clazz) {
		return this.find(name, clazz, true);
	}

	@SuppressWarnings("unchecked")
	private <T extends E> ImmutableSet<T> find(final String searchSpace, final Class<T> clazz, final boolean exactMatch) {
		Preconditions.checkArgument(
			this.nameFilter.matchesAllOf(searchSpace),
			"search-namespace didn't satisfy filter %s",
			this.nameFilter);

		/* hash the request and check we have a cache entry */
		int hash = Objects.hashCode(exactMatch, searchSpace, clazz);
		if ((this.cache != null) && this.cache.containsKey(hash)) {
			return (ImmutableSet<T>) this.cache.get(hash);
		}

		/* find results via exactName or prefixes */
		Collection<E> objects;
		if (exactMatch) {
			objects = this.nameMap.get(searchSpace);
		} else {
			objects = this.prefixMap.get(searchSpace);
		}

		/* check if type matches */
		ImmutableSet.Builder<T> builder = ImmutableSet.builder();
		for (final E object : objects) {
			if (clazz.isAssignableFrom(object.getClass())) {
				builder.add((T) object);
			}
		}

		/* save results in cache */
		ImmutableSet<T> results = builder.build();
		if (this.cache != null) {
			this.cache.put(hash, results);
		}

		return results;
	}

	/** seals the naming, so queries are allowed to be cached */
	public void seal() {
		Preconditions.checkState(this.cache == null, "naming was already sealed");

		this.cache = Maps.newHashMap();
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	/** returns all prefixes of a string, prefixes are separated by a dot */
	private static final class NamingFunction implements Function<String, Iterable<String>> {
		@Override
		public Iterable<String> apply(final String inputString) {

			Set<String> results = Sets.newHashSet();

			/* the empty string is always there */
			results.add("");

			String last = null;
			for (final String s : Splitter.on('.').split(inputString)) {
				if (last == null) {
					last = s;
				} else {
					last = last + "." + s;
				}

				results.add(last);
			}

			return results;
		}
	}
}