/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.actions;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.visualparts.PropertiesSelectDialog;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.Transfer;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**The action that only copy properties from a widget.
 * @author Xihui Chen, Joerg Rathlev (part of the code is copied from SDS)
 *
 */
public class CopyPropertiesAction extends SelectionAction {
	
	private static final String ROOT_ELEMENT = "PropCopyData";  //$NON-NLS-1$

	public static final String PROPID_ELEMENT = "Properties";  //$NON-NLS-1$

	public static final String ID = "org.csstudio.opibuilder.actions.copyproperties";
	
	/**
	 * @param part the OPI Editor
	 * @param pasteWidgetsAction pass the paste action will 
	 * help to update the enable state of the paste action
	 * after copy action invoked.
	 */
	public CopyPropertiesAction(OPIEditor part) {
		super(part);
		setText("Copy Properties...");
		setId(ID);
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/copy_properties.png"));
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedWidgetModels().size() == 1 &&
				!(getSelectedWidgetModels().get(0) instanceof DisplayModel))
			return true;
		return false;
	}
	
	
	@Override
	public void run() {
		PropertiesSelectDialog dialog = new PropertiesSelectDialog(null, getSelectedWidgetModels().get(0));
		if(dialog.open() == Window.OK){
			List<String> propList = dialog.getOutput();
			if(!propList.isEmpty()){
				AbstractWidgetModel widget = getSelectedWidgetModels().get(0);
				Element widgetElement = XMLUtil.widgetToXMLElement(widget);
				
				Element propertisElement = new Element(PROPID_ELEMENT);
				
				for(String propID : propList){
					propertisElement.addContent(new Element(propID));
				}
				Element rootElement = new Element(ROOT_ELEMENT);
				
				rootElement.addContent(widgetElement);
				rootElement.addContent(propertisElement);
				
				XMLOutputter xmlOutputter = new XMLOutputter(Format.getRawFormat());
				String xmlString = xmlOutputter.outputString(rootElement);
				
				((OPIEditor)getWorkbenchPart()).getClipboard()
					.setContents(new Object[]{xmlString}, 
					new Transfer[]{PropertiesCopyDataTransfer.getInstance()});
			}
		}
		
	}
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
		List<?> selection = getSelectedObjects();
	
		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
	
		for (Object o : selection) {
			if (o instanceof EditPart) {
				selectedWidgetModels.add((AbstractWidgetModel) ((EditPart) o)
						.getModel());
			}
		}
		return selectedWidgetModels;
	}

}
