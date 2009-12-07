package org.csstudio.opibuilder.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.support.ActionsPropertyDescriptor;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.logging.CentralLogger;
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
	public static final String XML_ATTRIBUTE_HOOK = "hook"; //$NON-NLS-1$
	
	private boolean showHookOption;
	
	public ActionsProperty(String prop_id, String description,
			WidgetPropertyCategory category) {
		super(prop_id, description, category, new ActionsInput());
		showHookOption = true;
	}

	
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
		return new ActionsPropertyDescriptor(prop_id, description, showHookOption);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ActionsInput readValueFromXML(Element propElement) {
		ActionsInput result = new ActionsInput();
		result.setHookUpToWidget(Boolean.parseBoolean(propElement.getAttributeValue(XML_ATTRIBUTE_HOOK)));
		for(Object oe : propElement.getChildren(XML_ELEMENT_ACTION)){
			Element se = (Element)oe;
			AbstractWidgetAction action = WidgetActionFactory.createWidgetAction(
					ActionType.parseAction(se.getAttributeValue(XML_ATTRIBUTE_ACTION_TYPE)));
			if(action != null){
				List children = se.getChildren();
				Iterator iterator = children.iterator();
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
							CentralLogger.getInstance().error(errorMessage, e);
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
		propElement.setAttribute(XML_ATTRIBUTE_HOOK, "" + actionsInput.isHookedUpToWidget());
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
