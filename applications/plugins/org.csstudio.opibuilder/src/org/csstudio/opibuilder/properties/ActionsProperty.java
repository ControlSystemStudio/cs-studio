/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**The widget property for actions.
 * @author Xihui Chen
 *
 */
public class ActionsProperty extends AbstractWidgetProperty {

	/**
	 * XML ELEMENT name <code>ACTION</code>.
	 */
	public static final String XML_ELEMENT_ACTION = "action"; //$NON-NLS-1$

	/**
	 * XML ATTRIBUTE name <code>PATHSTRING</code>.
	 */
	public static final String XML_ATTRIBUTE_ACTION_TYPE = "type"; //$NON-NLS-1$

	/**
	 * XML ATTRIBUTE name <code>HOOK</code>.
	 */
	public static final String XML_ATTRIBUTE_HOOK_FIRST = "hook"; //$NON-NLS-1$
	
	public static final String XML_ATTRIBUTE_HOOK_ALL = "hook_all"; //$NON-NLS-1$

	private boolean showHookOption;

	/**Widget Property Constructor
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 */
	public ActionsProperty(String prop_id, String description,
			WidgetPropertyCategory category) {
		super(prop_id, description, category, new ActionsInput());
		showHookOption = true;
	}

	/**Widget Property Constructor
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param showHookOption true if the hook option is visible in the dialog.
	 */
	public ActionsProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean showHookOption) {
		super(prop_id, description, category, new ActionsInput());
		this.showHookOption = showHookOption;
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		ActionsInput acceptableValue = null;
		if(value instanceof ActionsInput){
			((ActionsInput) value).setWidgetModel(widgetModel);
			acceptableValue = (ActionsInput)value;
		}

		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getActionsPropertyDescriptor(
				prop_id, description, showHookOption);
	}

	@Override
	public ActionsInput readValueFromXML(Element propElement) {
		ActionsInput result = new ActionsInput();
		result.setHookUpFirstActionToWidget(Boolean.parseBoolean(propElement.getAttributeValue(XML_ATTRIBUTE_HOOK_FIRST)));
		if(propElement.getAttribute(XML_ATTRIBUTE_HOOK_ALL) != null)
			result.setHookUpAllActionsToWidget(Boolean.parseBoolean(propElement.getAttributeValue(XML_ATTRIBUTE_HOOK_ALL)));
		for(Object oe : propElement.getChildren(XML_ELEMENT_ACTION)){
			Element se = (Element)oe;
			AbstractWidgetAction action = WidgetActionFactory.createWidgetAction(
					ActionType.parseAction(se.getAttributeValue(XML_ATTRIBUTE_ACTION_TYPE)));
			if(action != null){
				List<?> children = se.getChildren();
				Iterator<?> iterator = children.iterator();
				Set<String> propIdSet = action.getAllPropertyIDs();
				while (iterator.hasNext()) {
					Element subElement = (Element) iterator.next();
					//handle property
					if(propIdSet.contains(subElement.getName())){
						String propId = subElement.getName();
						try {
							action.setPropertyValue(propId,
									action.getProperty(propId).readValueFromXML(subElement));
						} catch (Exception e) {
							String errorMessage = "Failed to read the " + propId + " property for " + action.getDescription() +". " +
							"The default property value will be setted instead. \n" + e;
			                OPIBuilderPlugin.getLogger().log(Level.WARNING, errorMessage, e);
							ConsoleService.getInstance().writeWarning(errorMessage);
						}
					}
				}
				result.getActionsList().add(action);
			}
		}

		return result;
	}

	@Override
	public void writeToXML(Element propElement) {
		ActionsInput actionsInput = (ActionsInput)getPropertyValue();
		propElement.setAttribute(XML_ATTRIBUTE_HOOK_FIRST, "" + actionsInput.isFirstActionHookedUpToWidget()); ////$NON-NLS-1$
		propElement.setAttribute(XML_ATTRIBUTE_HOOK_ALL, "" + actionsInput.isHookUpAllActionsToWidget()); ////$NON-NLS-1$
		
		for(AbstractWidgetAction action : actionsInput.getActionsList()){
				Element actionElement = new Element(XML_ELEMENT_ACTION);
				actionElement.setAttribute(XML_ATTRIBUTE_ACTION_TYPE,
						action.getActionType().toString());
				for(AbstractWidgetProperty property : action.getAllProperties()){
					Element propEle = new Element(property.getPropertyID());
					property.writeToXML(propEle);
					actionElement.addContent(propEle);
				}
				propElement.addContent(actionElement);
		}
	}

	@Override
	public void setWidgetModel(AbstractWidgetModel widgetModel) {
		super.setWidgetModel(widgetModel);
		((ActionsInput)getPropertyValue()).setWidgetModel(widgetModel);
	}

}
