package org.uilib.templating;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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

import org.uilib.templating.components.ButtonUI;
import org.uilib.templating.components.ComponentGridUI;
import org.uilib.templating.components.ComponentUI;
import org.uilib.templating.components.DatepickerUI;
import org.uilib.templating.components.LabelUI;
import org.uilib.templating.components.RadioSetUI;
import org.uilib.templating.components.SpacerUI;
import org.uilib.templating.components.StackUI;
import org.uilib.templating.components.TableUI;
import org.uilib.templating.components.TextUI;
import org.uilib.util.ResourceToStringSupplier;
import org.uilib.util.StringSupplier;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class Templating {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(Templating.class);

	//~ Instance fields ------------------------------------------------------------------------------------------------

	private final StringSupplier supplier;
	private final Gson gson;

	/* mutable */
	private final Map<String, Class<?extends ComponentUI>> types = Maps.newHashMap();

	//~ Constructors ---------------------------------------------------------------------------------------------------

	public Templating(final StringSupplier supplier) {
		this.supplier = supplier;

		/* built in types */
		this.registerType(ButtonUI.class, "button");
		this.registerType(DatepickerUI.class, "datepicker");
		this.registerType(LabelUI.class, "label");
		this.registerType(StackUI.class, "stack");
		this.registerType(RadioSetUI.class, "radioset");
		this.registerType(TableUI.class, "table");
		this.registerType(ComponentGridUI.class, "grid");
		this.registerType(SpacerUI.class, "spacer");
		this.registerType(TextUI.class, "text");

		/* configure Gson */
		GsonBuilder gBuilder = new GsonBuilder();
		gBuilder.registerTypeAdapter(Component.class, new ComponentDeserializer());
		gBuilder.registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer());

		/* ...and construct it */
		this.gson = gBuilder.create();
	}

	//~ Methods --------------------------------------------------------------------------------------------------------

	public static Templating fromResources() {
		return new Templating(new ResourceToStringSupplier());
	}

	public void registerType(final Class<?extends ComponentUI> ui, final String typeName) {
		Preconditions.checkState(! this.types.containsKey(typeName), "type %s already registered", typeName);

		this.types.put(typeName, ui);
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

			Component component = this.gson.fromJson(source, Component.class);

			return component;
		} catch (final JsonParseException e) {
			L.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	//~ Inner Classes --------------------------------------------------------------------------------------------------

	private final class ComponentDeserializer implements JsonDeserializer<Component> {

		private final Type immutableListType = new TypeToken<ImmutableList<Component>>() {}
			.getType();

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
					Options.empty());
			}

			/* 2. name (if existent) */
			JsonElement jsonName = jsonObject.get("name");
			String name			 = ((jsonName != null) ? jsonName.getAsString() : null);

			/* 3. type, default to 'component' if non-existant */
			JsonElement jsonType = jsonObject.get("type");
			String componentType = ((jsonType != null) ? jsonType.getAsString() : "grid");

			L.debug("deserializing: " + componentType);

			/* 4. options = all other parameters (no name, children or type) */
			Map<String, String> map = Maps.newHashMap();
			for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {

				String key		   = entry.getKey();
				JsonElement option = entry.getValue();

				if (key.equals("name") || key.equals("children") || key.equals("type")) {
					continue;
				}

				Preconditions.checkState(option.isJsonPrimitive(), "option %s is no json-primitive", option);
				map.put(key, option.getAsString());
			}

			Options options					  = Options.of(map);

			/* 5. deserialize children */
			ImmutableList<Component> children = null;
			if (jsonObject.has("children")) {
				Preconditions.checkState(jsonObject.get("children").isJsonArray(), "children is not array");
				children = context.deserialize(jsonObject.get("children").getAsJsonArray(), this.immutableListType);
			} else {
				children = ImmutableList.of();
			}

			/* 6. ui instantiation */
			ComponentUI compUI = this.instantiateController(componentType);
			;

			return new Component(name, componentType, children, compUI, options);
		}

		private ComponentUI instantiateController(final String type) {
			Preconditions.checkArgument(types.containsKey(type), "no type '%s' registered", type);
			try {
				return types.get(type).newInstance();
			} catch (final InstantiationException e) {
				L.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.getMessage(), e);
			} catch (final IllegalAccessException e) {
				L.error(e.getMessage(), e);
				throw new IllegalArgumentException(e.getMessage(), e);
			}
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