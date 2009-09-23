package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * 
 * @author Helge Rickens, Kai Meyer, Xihui Chen
 *
 */
public final class MenuButtonModel extends AbstractPVWidgetModel {
	private static final boolean DEFAULT_ACTIONS_FROM_PV = false;
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$
	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$

	public static final String PROP_ACTIONS_FROM_PV = "actions_from_pv"; //$NON-NLS-1$
	
	/**
	 * The ID of the text alignment property.
	 */
	public static final String PROP_TEXT_ALIGNMENT = "text_alignment"; //$NON-NLS-1$
	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 40;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 100;
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.MenuButton";
	
	/**
	 * Constructor.
	 */
	public MenuButtonModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setBorderStyle(BorderStyle.BUTTON_RAISED);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_LABEL, "Label",
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		addProperty(new FontProperty(PROP_FONT, "Font",
				WidgetPropertyCategory.Display, new FontData(
						"Arial", 8, SWT.NONE))); //$NON-NLS-1$
		addProperty(new BooleanProperty(PROP_ACTIONS_FROM_PV, "Actions From PV", 
				WidgetPropertyCategory.Behavior, DEFAULT_ACTIONS_FROM_PV));
		removeProperty(PROP_ACTIONS);		
		addProperty(new ActionsProperty(PROP_ACTIONS, "Actions", 
				WidgetPropertyCategory.Behavior, false));
		//addProperty(new ComboProperty(PROP_TEXT_ALIGNMENT, "Text Alignment", 
		//		WidgetPropertyCategory.Display, TextAlignmentEnum.getDisplayNames() ,TextAlignmentEnum.CENTER.getIndex()));
		
		setPropertyVisible(PROP_ACTIONS, !DEFAULT_ACTIONS_FROM_PV);
		setPropertyVisible(PROP_PVNAME, DEFAULT_ACTIONS_FROM_PV);
		
	}
	


	/**
	 * {@inheritDoc}
	 */
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
	public OPIFont getFont() {
		return (OPIFont) getProperty(PROP_FONT).getPropertyValue();
	}
	
	/**
	 * Returns the alignment for the text.
	 * @return int 
	 * 			0 = Center, 1 = Top, 2 = Bottom, 3 = Left, 4 = Right
	 */
	public int getTextAlignment() {
		return (Integer) getProperty(PROP_TEXT_ALIGNMENT).getPropertyValue();
	}

	public boolean isActionsFromPV(){
		return (Boolean)getCastedPropertyValue(PROP_ACTIONS_FROM_PV);
	}
	
}
