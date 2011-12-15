package org.uilib.swt.templating;

import org.apache.log4j.Logger;
import org.uilib.util.StringResourceLoader;

public class ResourceTemplateLoader implements TemplateLoader {

	//~ Static fields/initializers -------------------------------------------------------------------------------------

	private static final Logger L = Logger.getLogger(ResourceTemplateLoader.class);
	private final StringResourceLoader loader = new StringResourceLoader();

	//~ Methods --------------------------------------------------------------------------------------------------------

	@Override
	public String getTemplate(final String templateName) {
		L.debug("loading template: " + templateName);
		return this.loader.get("/resources/components/" + templateName + ".json");
	}
}