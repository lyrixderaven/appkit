package org.uilib.swt.templating;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Locale;
import java.util.Map;

public class Options {

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final ImmutableMap<String, String> options;
	private final ImmutableMap<String, Boolean> boolOpts;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Options() {
		this(ImmutableMap.<String, String>of());
	}

	public Options(final Map<String, String> options) {
		this.options = ImmutableMap.copyOf(options);

		ImmutableMap.Builder<String, Boolean> map = ImmutableMap.builder();
		map.put("true", Boolean.TRUE);
		map.put("yes", Boolean.TRUE);
		map.put("false", Boolean.FALSE);
		map.put("no", Boolean.FALSE);

		this.boolOpts = map.build();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public ImmutableMap<String, String> getMap() {
		return this.options;
	}

	public boolean get(final String key, final boolean def) {

		String option = this.options.get(key);
		if (option == null) {
			return def;
		} else {

			String bOption = option.toLowerCase(Locale.ENGLISH);
			Preconditions.checkArgument(boolOpts.containsKey(bOption), bOption + " no a valid boolean option");

			return boolOpts.get(bOption);
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

		String option = this.options.get(key);
		if (option == null) {
			return ImmutableList.of();
		} else {
			return ImmutableList.copyOf(Splitter.on(' ').trimResults().split(option));
		}
	}
}