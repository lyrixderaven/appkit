package org.uilib.swt.templating;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import org.uilib.swt.components.ButtonUI;
import org.uilib.swt.components.ComponentUI;
import org.uilib.swt.components.DatepickerUI;
import org.uilib.swt.components.LabelUI;
import org.uilib.swt.components.PlaceholderUI;
import org.uilib.swt.components.RadioSetUI;
import org.uilib.swt.components.SpacerUI;
import org.uilib.swt.components.TableUI;
import org.uilib.swt.components.TextUI;
import org.uilib.swt.components.UIController;
import org.uilib.util.ResourceToStringSupplier;
import org.uilib.util.StringSupplier;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

// TODO: Texts: getSystemDefault Lang
public final class Templating {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(Templating.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final StringSupplier supplier;
	private final Gson gson;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Templating(final StringSupplier supplier) {
		this.supplier			  = supplier;

		/* configure Gson */
		GsonBuilder gBuilder = new GsonBuilder();
		gBuilder.registerTypeAdapter(Component.class, new ComponentDeserializer());
		gBuilder.registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer());

		/* ...and construct it */
		this.gson = gBuilder.create();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Templating create() {
		return new Templating(new ResourceToStringSupplier());
	}

	public Component create(final String componentType) {
		L.debug("creating component: " + componentType);

		String source = this.supplier.get("components/" + componentType + ".json");
		if (source == null) {
			L.debug("none found for: " + componentType);
			return null;
		}

		try {
			L.debug("deserializing component: " + componentType);
			return this.gson.fromJson(source, Component.class);
		} catch (final JsonParseException e) {
			L.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	// FIXME: Templating: "options" not allowed for clarity
	private final class ComponentDeserializer implements JsonDeserializer<Component> {

		private final ImmutableMap<String, Class<?extends UIController<?>>> controllers;
		private final Type immutableListType = new TypeToken<ImmutableList<Component>>() {}
			.getType();

		public ComponentDeserializer() {

			ImmutableMap.Builder<String, Class<?extends UIController<?>>> map = ImmutableMap.builder();
			map.put("button", ButtonUI.class);
			map.put("datepicker", DatepickerUI.class);
			map.put("label", LabelUI.class);
			map.put("placeholder", PlaceholderUI.class);
			map.put("radioset", RadioSetUI.class);
			map.put("table", TableUI.class);
			map.put("component", ComponentUI.class);
			map.put("spacer", SpacerUI.class);
			map.put("text", TextUI.class);

			this.controllers = map.build();
		}

		private UIController<?> instantiateController(final String type) {
			try {
				return this.controllers.get(type).newInstance();
			} catch (final InstantiationException e) {
				L.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.getMessage(), e);
			} catch (final IllegalAccessException e) {
				L.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}

		@Override
		public Component deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) {

			JsonObject jsonObject = json.getAsJsonObject();

			/* 1. if component is empty, it's a spacer */
			if (jsonObject.entrySet().isEmpty()) {
				return new Component(
					null,
					"spacer",
					ImmutableList.<Component>of(),
					this.instantiateController("spacer"),
					new Options());
			}

			/* 2. read name (if existent) */
			JsonElement jsonName = jsonObject.get("name");
			String name			 = ((jsonName != null) ? jsonName.getAsString() : null);

			/* 3. read type, default to 'component' if non-existant */
			JsonElement jsonType = jsonObject.get("type");
			String componentType = ((jsonType != null) ? jsonType.getAsString() : "component");

			L.debug("deserializing: " + componentType);

			/* 4. read all other parameters (no name, children or type)  into options */
			Map<String, String> options = Maps.newHashMap();
			for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {

				String key = entry.getKey();
				if (key.equals("name") || key.equals("children") || key.equals("type")) {
					continue;
				}

				// FIXME: force that this is a string, boolean or integer
				options.put(key, entry.getValue().getAsString());
			}

			/* 5. try to load component which is called like this type (subcomponent) */
			Component subComp = Templating.this.create(componentType);

			/* 6. Check: if a sub-component was found this component isn't allowed to have children */
			Preconditions.checkState(
				(subComp == null) || (jsonObject.get("children") == null),
				"can either refer to a subcomponent or have children itself");

			UIController<?> controller		  = null;
			ImmutableList<Component> children = null;

			/* 7. if a sub-component was found, we "inline" parts of it in this component by copying it's properties */
			if (subComp != null) {
				/* copy type, controller and children */
				controller     = subComp.getController();
				children	   = subComp.getChildren();

				/* copy the options, which weren't overridden in this composite  */
				for (final Entry<String, String> entry : subComp.getOptions().getMap().entrySet()) {
					if (! options.containsKey(entry.getKey())) {
						options.put(entry.getKey(), entry.getValue());
					}
				}
			} else {
				/* 7b. if it wasn't a reference to a sub-component, we try the registered controllers */
				controller = this.instantiateController(componentType);

				/* deserialize children */
				// FIXME: force this to be an array
				if (jsonObject.has("children")) {
					children = context.deserialize(jsonObject.get("children").getAsJsonArray(), this.immutableListType);
				} else {
					children = ImmutableList.of();
				}
			}

			return new Component(name, componentType, children, controller, new Options(options));
		}
	}

	private static final class ImmutableListDeserializer implements JsonDeserializer<ImmutableList<?>> {
		@Override
		public ImmutableList<?> deserialize(final JsonElement json, final Type type,
											final JsonDeserializationContext context)
									 throws JsonParseException
		{

			Type type2   =
				ParameterizedTypeImpl.make(List.class, ((ParameterizedType) type).getActualTypeArguments(), null);
			List<?> list = context.deserialize(json, type2);

			return ImmutableList.copyOf(list);
		}
	}
}