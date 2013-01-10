package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringTableProperty.TitlesProvider;
import org.csstudio.opibuilder.util.ImplementationLoader;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**Single source helper for PropertyDescriptor. IMPL can be null.
 * @author Xihui Chen
 *
 */
public abstract class PropertySSHelper {
	
	private static final PropertySSHelper IMPL =			
			(PropertySSHelper)ImplementationLoader.loadObjectInPlugin(
					"org.csstudio.opibuilder.editor",  //$NON-NLS-1$
					"org.csstudio.opibuilder.properties.support.PropertySSHelperImpl", //$NON-NLS-1$
					false);
	
	
	public static PropertySSHelper getIMPL(){
		return IMPL;
	}

	public abstract PropertyDescriptor getActionsPropertyDescriptor(final String prop_id,
			final String description, final boolean showHookOption);

	public  abstract PropertyDescriptor getBooleanPropertyDescriptor(
			String prop_id, String description);

	public  abstract PropertyDescriptor getOPIColorPropertyDescriptor(
			String prop_id, String description);

	public abstract PropertyDescriptor getDoublePropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getFilePathPropertyDescriptor(String prop_id,
			String description, AbstractWidgetModel widgetModel,
			String[] fileExtensions);

	public abstract PropertyDescriptor getOPIFontPropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getIntegerPropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getMacrosPropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getPointlistPropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getRulesPropertyDescriptor(String prop_id,
			AbstractWidgetModel widgetModel, String description);

	public abstract PropertyDescriptor getScriptPropertyDescriptor(String prop_id,
			AbstractWidgetModel widgetModel, String description);

	public abstract PropertyDescriptor getStringListPropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getMultiLineTextPropertyDescriptor(
			String prop_id, String description);

	public abstract PropertyDescriptor getTextPropertyDescriptor(String prop_id,
			String description);

	public abstract PropertyDescriptor getStringTablePropertyDescriptor(String prop_id,
			String description, TitlesProvider titlesProvider);

	public abstract PropertyDescriptor getComplexDataPropertyDescriptor(String prop_id,
			String description, String dialogTitle);

	public abstract PropertyDescriptor FilePathPropertyDescriptorWithFilter(
			String prop_id, String description, AbstractWidgetModel widgetModel, String[] filters);
	
	public abstract PropertyDescriptor getMatrixPropertyDescriptor(String prop_id,
			String description);
	
}
