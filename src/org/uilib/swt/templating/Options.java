package org.uilib.swt.templating;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

// TODO: Idee: OptionsBuilder .get("sd").withDefault(false)
public class Options {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final static Logger L = Logger.getLogger(Options.class);
	private final static ImmutableMap<String, Boolean> boolTransl = ImmutableMap.of("true", Boolean.TRUE, "yes", Boolean.TRUE, "false", Boolean.FALSE, "no", Boolean.FALSE);
	private final static CharMatcher nameFilter = CharMatcher.inRange('a', 'z').or(CharMatcher.anyOf("?_-"));

	private final ImmutableMap<String, String> options;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public static Options set(String boolOpt) {
		return new Options(ImmutableMap.of(boolOpt, "yes"));
	}

	public static Options empty() {
		return new Options(ImmutableMap.<String,String>of());
	}

	public static Options of(Map<String,String> options) {
		return new Options(options);
	}

	private Options(final Map<String, String> options) {

		/* run through options */
		Multimap<String, String> mmap = HashMultimap.create();
		for (Map.Entry<String,String> opt : options.entrySet()) {

			if (!opt.getKey().equals("options")) {
				mmap.put(opt.getKey(), opt.getValue());
			} else {
				for (String subOpt : Splitter.on(' ').trimResults().split(opt.getValue())) {
					mmap.put(subOpt, "yes");
				}
			}
		}

		Map<String,String> map = Maps.newHashMap();
		for (Map.Entry<String, Collection<String>> entry : mmap.asMap().entrySet()) {
			String key = entry.getKey();

			// FIXME: this doesn't find range=yes options=range
			Preconditions.checkArgument(nameFilter.matchesAllOf(key), "only a-z, '-' and '?' allowed in name");
			Preconditions.checkArgument(entry.getValue().size() == 1, "specify option '%s' only once", key);
			map.put(key,entry.getValue().iterator().next());
		}

		this.options = ImmutableMap.copyOf(map);

		L.debug("created options: " + this.options);
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Options withDefaults(Options defaults) {
		Map<String,String> newOptions = Maps.newHashMap();
		newOptions.putAll(this.options);

		for (Entry<String,String> entry : defaults.getMap().entrySet()) {
			if (!newOptions.containsKey(entry.getKey())) {
				newOptions.put(entry.getKey(), entry.getValue());
			}
		}

		return new Options(newOptions);
	}

	public boolean get(final String key, final boolean def) {

		String option = this.options.get(key);
		if (option == null) {
			return def;
		} else {
			Preconditions.checkArgument(boolTransl.containsKey(option), option + " no a valid boolean option");

			return boolTransl.get(option);
		}
	}

	public int get(final String key, final int def) {

		String option = this.options.get(key);
		if (option == null) {
			return def;
		} else {
			return Integer.valueOf(option);
		}
	}

	public String get(final String key, final String def) {

		String option = this.options.get(key);
		if (option == null) {
			return def;
		} else {
			return option;
		}
	}

	public ImmutableList<String> get(final String key) {

		Preconditions.checkArgument(!key.equals("options"), "'options' is translated into boolean options");

		String option = this.options.get(key);
		if (option == null) {
			return ImmutableList.of();
		} else {
			return ImmutableList.copyOf(Splitter.on(' ').trimResults().split(option));
		}
	}

	public ImmutableMap<String, String> getMap() {
		return this.options;
	}
}