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

import org.uilib.swt.templating.components.ButtonUI;
import org.uilib.swt.templating.components.ComponentUI;
import org.uilib.swt.templating.components.DatepickerUI;
import org.uilib.swt.templating.components.LabelUI;
import org.uilib.swt.templating.components.PlaceholderUI;
import org.uilib.swt.templating.components.RadioSetUI;
import org.uilib.swt.templating.components.SpacerUI;
import org.uilib.swt.templating.components.TableUI;
import org.uilib.swt.templating.components.TextUI;
import org.uilib.swt.templating.components.UIController;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class Templating {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(Templating.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private TemplateLoader loader;
	private final Gson gson;

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Templating(final TemplateLoader loader) {
		this.loader				  = loader;

		/* configure Gson */
		GsonBuilder gBuilder = new GsonBuilder();
		gBuilder.registerTypeAdapter(Component.class, new ComponentDeserializer());
		gBuilder.registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer());

		/* ...and construct it */
		this.gson = gBuilder.create();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public Component create(final String name) {
		L.debug("creating component: " + name);

		String source = this.loader.getTemplate(name);
		if (source == null) {
			L.debug("none found for: " + name);
			return null;
		}

		try {
			L.debug("deserializing component: " + name);
			return this.gson.fromJson(source, Component.class);
		} catch (final JsonParseException e) {
			L.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private final class ComponentDeserializer implements JsonDeserializer<Component> {

		private final ImmutableMap<String, UIController> controllers;

		public ComponentDeserializer() {

			ImmutableMap.Builder<String, UIController> map = ImmutableMap.builder();
			map.put("button", new ButtonUI());
			map.put("datepicker", new DatepickerUI());
			map.put("label", new LabelUI());
			map.put("placeholder", new PlaceholderUI());
			map.put("radioset", new RadioSetUI());
			map.put("table", new TableUI());
			map.put("component", new ComponentUI());
			map.put("spacer", new SpacerUI());
			map.put("text", new TextUI());

			this.controllers = map.build();
		}

		@Override
		public Component deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) {

			JsonObject jsonObject = json.getAsJsonObject();

			/* if component is empty, it's a spacer */
			if (jsonObject.entrySet().isEmpty()) {
				return new Component(
					null,
					"spacer",
					ImmutableList.<Component>of(),
					this.controllers.get("spacer"),
					new Options());
			}

			/* name: read name */
			JsonElement jsonName = jsonObject.get("name");
			String name			 = ((jsonName != null) ? jsonName.getAsString() : null);

			/* type: what is it, defaults: to component */
			JsonElement jsonType = jsonObject.get("type");
			String componentType = ((jsonType != null) ? jsonType.getAsString() : "component");
			L.debug("deserializing: " + componentType);

			/* load parameters into options except for name, children and type */
			Map<String, String> options = Maps.newHashMap();
			for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {

				// FIXME: Predicate
				String key = entry.getKey();
				if (key.equals("name") || key.equals("children") || key.equals("type")) {
					continue;
				}

				String value = entry.getValue().getAsString();
				options.put(key, value);
			}

			/** load potentially existing component-description for type */
			// FIXME: Templating: kann defaults überschreiben
			Component subComp = Templating.this.create(componentType);

			/** Check: either this component has children itself or the type refers to a sub-component */
			Preconditions.checkState(
				(subComp == null) || (jsonObject.get("children") == null),
				"can either refer to a subcomponent or have children itself");

			UIController controller			  = null;
			ImmutableList<Component> children = null;

			if (subComp == null) {
				/** set the controller */
				controller = this.controllers.get(componentType);

				if (jsonObject.has("children")) {

					Type listType = new TypeToken<ImmutableList<Component>>() {}
						.getType();
					children = context.deserialize(jsonObject.get("children").getAsJsonArray(), listType);
				} else {
					children = ImmutableList.of();
				}
			} else {
				/** if it's subcomponent, use it's type + controller and children */
				controller     = subComp.getController();
				children	   = subComp.getChildren();

				/** and copy the options */
				for (final Entry<String, String> entry : subComp.getOptions().getMap().entrySet()) {
					if (! options.containsKey(entry.getKey())) {
						options.put(entry.getKey(), entry.getValue());
					}
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