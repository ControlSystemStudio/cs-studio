package org.csstudio.sds.components.model;

import org.csstudio.sds.components.internal.localization.Messages;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.model.properties.ActionDataProperty;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * 
 * @author Helge Rickens, Kai Meyer
 *
 */
public final class MenuButtonModel extends AbstractWidgetModel {
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$
	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$
	
	/**
	 * The ID of the text alignment property.
	 */
	public static final String PROP_TEXT_ALIGNMENT = "textAlignment"; //$NON-NLS-1$
	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 20;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;
	
	/**
	 * The default value of the text alignment property.
	 */
	private static final int DEFAULT_TEXT_ALIGNMENT = 0;
	/**
	 * The ID of the {@link ActionData} property.
	 */
	public static final String PROP_ACTIONDATA1 = "actionData1"; //$NON-NLS-1$
	/**
	 * The ID of the {@link ActionData} property.
	 */
	public static final String PROP_ACTIONDATA2 = "actionData2"; //$NON-NLS-1$
	/**
	 * The ID of the {@link ActionData} property.
	 */
	public static final String PROP_ACTIONDATA3 = "actionData3"; //$NON-NLS-1$
	
	/**
	 * The labels for the text alignment property.
	 */
	private static final String[] SHOW_LABELS = new String[] {"Center", "Top", "Bottom", "Left", "Right"};
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.MenuButton";
	
	
	

	public MenuButtonModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}


	@Override
	protected void configureProperties() {
		addProperty(PROP_LABEL, new StringProperty(Messages.LabelElement_LABEL,
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		addProperty(PROP_FONT, new FontProperty("Font",
				WidgetPropertyCategory.Display, new FontData(
						"Arial", 8, SWT.NONE))); //$NON-NLS-1$
		addProperty(PROP_TEXT_ALIGNMENT, new OptionProperty("Text Alignment", 
				WidgetPropertyCategory.Display, SHOW_LABELS, DEFAULT_TEXT_ALIGNMENT));
		addProperty(PROP_ACTIONDATA1, new ActionDataProperty("Action Data1",
				WidgetPropertyCategory.Behaviour, new ActionData(ActionType.UNKNOWN)));
		addProperty(PROP_ACTIONDATA2, new ActionDataProperty("Action Data2",
				WidgetPropertyCategory.Behaviour, new ActionData(ActionType.UNKNOWN)));
		addProperty(PROP_ACTIONDATA3, new ActionDataProperty("Action Data3",
						WidgetPropertyCategory.Behaviour, new ActionData(ActionType.UNKNOWN)));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	/**
	 * Return the label text.
	 * 
	 * @return The label text.
	 */
	public String getLabel() {
		return (String) getProperty(PROP_LABEL).getPropertyValue();
	}

	/**
	 * Return the label font.
	 * 
	 * @return The label font.
	 */
	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}
	
	/**
	 * Returns the alignment for the text.
	 * @return int 
	 * 			0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
	 */
	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGNMENT).getPropertyValue();
	}
	
	public ActionData getActionData1() {
		return (ActionData) getProperty(PROP_ACTIONDATA1).getPropertyValue();
	}
	public ActionData getActionData2() {
		return (ActionData) getProperty(PROP_ACTIONDATA2).getPropertyValue();
	}
	public ActionData getActionData3() {
		return (ActionData) getProperty(PROP_ACTIONDATA3).getPropertyValue();
	}


}
