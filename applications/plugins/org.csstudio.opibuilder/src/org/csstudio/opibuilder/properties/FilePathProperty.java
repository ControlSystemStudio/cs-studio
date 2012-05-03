/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.util.OPIBuilderMacroUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;



/**
 * The property for file path, which is represented as an {@link IPath}.
 * @author Xihui Chen, Kai Meyer (similar class as in SDS)
 *
 */
public class FilePathProperty extends AbstractWidgetProperty {

	/**
	 * The file extension, which should be accepted.
	 */
	private String[] fileExtensions;
	
	private boolean buildAbsolutePath;
	
	
	/**File Path Property Constructor. The property value type is {@link IPath}.
	 * It will automatically build the absolute path if it is relative path.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 * @param fileExtensions the allowed file extensions in the file open dialog.
	 */
	public FilePathProperty(String prop_id, String description,
			WidgetPropertyCategory category, IPath defaultValue,
			String[] fileExtensions) {		
		this(prop_id, description, category, defaultValue, fileExtensions, true);
		
	}
	
	/**File Path Property Constructor. The property value type is {@link IPath}.
	 * @param prop_id the property id which should be unique in a widget model.
	 * @param description the description of the property,
	 * which will be shown as the property name in property sheet.
	 * @param category the category of the widget.
	 * @param defaultValue the default value when the widget is first created.
	 * @param fileExtensions the allowed file extensions in the file open dialog.
	 * @param buildAbsolutePath true if it should automatically build the absolute path from widget model.
	 */
	public FilePathProperty(String prop_id, String description,
			WidgetPropertyCategory category, IPath defaultValue,
			String[] fileExtensions, boolean buildAbsolutePath) {		
		super(prop_id, description, category,
				defaultValue == null? new Path("") : defaultValue); //$NON-NLS-1$
		this.fileExtensions = fileExtensions;
		this.buildAbsolutePath = buildAbsolutePath;
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		Object acceptedValue = null;
		
		if (value instanceof IPath || value instanceof String) {
			IPath path;
			if(value instanceof String)
				path = ResourceUtil.getPathFromString((String) value);
			else 
				path = (IPath) value;
			if (fileExtensions!=null && fileExtensions.length>0) {
				for (String extension : fileExtensions) {
					if (extension.equalsIgnoreCase(path.getFileExtension())) {
						acceptedValue = path; 
					}
					if(extension.equals("*"))
						acceptedValue = path; 
				}
			} else {
				acceptedValue = path;
			}
			if (path.isEmpty()) {
				acceptedValue = path;
			}
		}
		
		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getFilePathPropertyDescriptor(prop_id, 
				description,
				widgetModel, 
				fileExtensions);
	}
	
	@Override
	public Object getPropertyValue() {
		if(widgetModel !=null && widgetModel.getExecutionMode() == ExecutionMode.RUN_MODE
				&& propertyValue != null &&
				!((IPath)propertyValue).isEmpty()){
			String s = OPIBuilderMacroUtil.replaceMacros(
					widgetModel, propertyValue.toString());
			IPath path = ResourceUtil.getPathFromString(s);
			if(buildAbsolutePath && !path.isAbsolute())
				return ResourceUtil.buildAbsolutePath(widgetModel, path);
			else
				return path;
		}			
		return super.getPropertyValue();
	}
	

	@Override
	public Object readValueFromXML(Element propElement) {
		if(ResourceUtil.isURL(propElement.getText()))
			return new URLPath(propElement.getText());
		return Path.fromPortableString(propElement.getText());
	}

	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(((IPath)getPropertyValue()).toPortableString());
	}

	@Override
	public boolean configurableByRule() {
		return true;
	}
	
	@Override
	public String toStringInRuleScript(Object propValue) {
		return RuleData.QUOTE + super.toStringInRuleScript(propValue) + RuleData.QUOTE;
	}
	
}
