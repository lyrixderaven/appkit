package org.uilib.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Naming<E> {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private static final Logger L							 = LoggerFactory.getLogger(Naming.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final Function<String, Iterable<String>> prefixFunction  = new PrefixFunction();
	private final Function<String, Iterable<String>> postfixFunction = new PostfixFunction();

	/* dictionary */
	private final Multimap<String, E> exactMap   = HashMultimap.create();
	private final Multimap<String, E> prefixMap  = HashMultimap.create();
	private final Multimap<String, E> postfixMap = HashMultimap.create();

	/* cache, so that we don't have to check for the instances all the time */
	private Map<Integer, ImmutableSet<?extends E>> cache;

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("exact matches:\n");

		List<String> keys = Lists.newArrayList(exactMap.keySet());
		Collections.sort(keys, Ordering.natural());
		for (final String k : keys) {
			sb.append("\t");
			sb.append(k);
			sb.append(": ");
			sb.append(this.exactMap.get(k));
			sb.append("\n");
		}

		sb.append("prefix matches:\n");
		keys = Lists.newArrayList(prefixMap.keySet());
		Collections.sort(keys, Ordering.natural());
		for (final String k : keys) {
			sb.append("\t");
			sb.append(k);
			sb.append(": ");
			sb.append(this.prefixMap.get(k));
			sb.append("\n");
		}

		sb.append("postfix matches:\n");
		keys = Lists.newArrayList(postfixMap.keySet());
		Collections.sort(keys, Ordering.natural());
		for (final String k : keys) {
			sb.append("\t");
			sb.append(k);
			sb.append(": ");
			sb.append(this.postfixMap.get(k));
			sb.append("\n");
		}

		return sb.toString();
	}

	/** creates a new Naming instance */
	public static <T> Naming<T> create() {
		return new Naming<T>();
	}

	/** seals the naming, so queries are allowed to be cached */
	public void seal() {
		Preconditions.checkState(! this.isSealed(), "naming was already sealed");

		this.cache = Maps.newHashMap();
	}

	/** returns if naming was sealed */
	public boolean isSealed() {
		return (this.cache != null);
	}

	/** attach naming to this */
	public void attach(final String subName, final Naming<E> naming) {
		this.checkValid(subName);
		Preconditions.checkArgument(naming.isSealed(), "naming to attach must be sealed first");
		Preconditions.checkArgument(! this.isSealed(), "naming was already sealed");

		/* generate exact entries (name+name) and postfix map (postfixes+name) */
		for (final Entry<String, E> entry : naming.exactMap.entries()) {
			this.exactMap.put(subName + "." + entry.getKey(), entry.getValue());

			for (final String postfix : this.postfixFunction.apply(subName)) {
				this.postfixMap.put(postfix + "." + entry.getKey(), entry.getValue());
			}
		}

		/* copy other postfixes */
		this.postfixMap.putAll(naming.postfixMap);

		/* prefix entries */
		for (final Entry<String, E> prefixEntry : naming.prefixMap.entries()) {
			this.prefixMap.put(subName + "." + prefixEntry.getKey(), prefixEntry.getValue());
		}
	}

	/** registers a new object with the given name*/
	public void register(final String name, final E object) {
		this.checkValid(name);

		/* reference-able via exact name */
		this.exactMap.put(name, object);

		/* reference-able via all prefixes */
		for (final String prefix : this.prefixFunction.apply(name)) {
			this.prefixMap.put(prefix, object);
		}

		/* reference-able via all postfixes */
		for (final String postfix : this.postfixFunction.apply(name)) {
			this.postfixMap.put(postfix, object);
		}
	}

	/** selects the object matching a class */
	public <T extends E> T select(final Class<T> clazz) {
		return this.select("", clazz);
	}

	/** finds all objects matching a class */
	public <T extends E> ImmutableSet<T> find(final Class<T> clazz) {
		return this.find("", clazz);
	}

	/** selects the object matching a class and a name-query */
	public <T extends E> T select(final String query, final Class<T> clazz) {
		return this.select(query, clazz, false);
	}

	/** finds all objects matching a class and a name-query */
	public <T extends E> ImmutableSet<T> find(final String query, final Class<T> clazz) {
		return this.find(query, clazz, false);
	}

	/** selects the object matching a class and a query */
	public <T extends E> T select(final String query, final Class<T> clazz, final boolean exactNameMatch) {

		ImmutableSet<T> results = this.find(query, clazz, exactNameMatch);

		Preconditions.checkState(
			results.size() == 1,
			"search for type %s query '%s' returned %s results",
			clazz.getSimpleName(),
			query,
			results.size());

		return results.iterator().next();
	}

	/** finds all objects matching a class and a name-query */
	@SuppressWarnings("unchecked")
	private <T extends E> ImmutableSet<T> find(final String query, final Class<T> clazz, final boolean exactMatch) {

		/* cache: hash the request and check if we have an entry */
		int hash = Objects.hashCode(exactMatch, query, clazz);
		if ((this.cache != null) && this.cache.containsKey(hash)) {
			return (ImmutableSet<T>) this.cache.get(hash);
		}

		/* find results */
		Collection<E> objects;
		if (exactMatch) {
			objects = this.exactMap.get(query);
		} else {
			objects = this.prefixMap.get(query);
			objects.addAll(this.postfixMap.get(query));
		}

		/* of these, find results that match given type */
		ImmutableSet.Builder<T> builder = ImmutableSet.builder();
		for (final E object : objects) {
			if (clazz.isAssignableFrom(object.getClass())) {
				builder.add((T) object);
			}
		}

		/* cache: save results */
		ImmutableSet<T> results = builder.build();
		if (this.cache != null) {
			this.cache.put(hash, results);
		}

		return results;
	}

	private final void checkValid(final String name) {

		/* split name into parts (separated by dot) */
		List<String> parts = Lists.newArrayList(Splitter.on('.').split(name));

		/* check if there's no empty name in it */
		Preconditions.checkArgument(! parts.contains(""), "name '%s' invalid, no empty sub-names allowed");
		Preconditions.checkArgument(! parts.isEmpty(), "name '%s' invalid, no empty names allowed");
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private static final class PrefixFunction implements Function<String, Iterable<String>> {
		@Override
		public Iterable<String> apply(final String inputString) {

			Set<String> prefixes = Sets.newHashSet();

			/* split name into parts (separated by dot) */
			List<String> parts   = Lists.newArrayList(Splitter.on('.').split(inputString));

			for (int i = 1; i <= parts.size(); i++) {
				prefixes.add(Joiner.on('.').join(parts.subList(0, i)));
			}

			return prefixes;
		}
	}

	private static final class PostfixFunction implements Function<String, Iterable<String>> {
		@Override
		public Iterable<String> apply(final String inputString) {

			Set<String> postfixes = Sets.newHashSet();

			/* split name into parts (separated by dot) */
			List<String> parts    = Lists.newArrayList(Splitter.on('.').split(inputString));

			for (int i = 0; i < parts.size(); i++) {
				postfixes.add(Joiner.on('.').join(parts.subList(i, parts.size())));
			}

			return postfixes;
		}
	}
}