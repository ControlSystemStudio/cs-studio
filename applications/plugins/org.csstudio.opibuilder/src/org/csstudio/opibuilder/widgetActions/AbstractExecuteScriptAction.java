/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**The abstract action for executing script.
 * @author Xihui Chen
 *
 */
public abstract class AbstractExecuteScriptAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$
	public static final String PROP_EMBEDDED = "embedded";//$NON-NLS-1$
	public static final String PROP_SCRIPT_TEXT = "scriptText";//$NON-NLS-1$
	
	private BufferedReader reader = null;
	private InputStream inputStream = null;

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""),
				new String[]{getFileExtension()}));
		addProperty(new StringProperty(
				PROP_SCRIPT_TEXT, "Script Text", WidgetPropertyCategory.Basic, 
				getScriptHeader(), true, true));
		BooleanProperty embeddedProperty = new BooleanProperty(
				PROP_EMBEDDED, "Embedded", WidgetPropertyCategory.Basic, false);
		embeddedProperty.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {			
				getProperty(PROP_PATH).setVisibleInPropSheet(
						!((Boolean) evt.getNewValue()));
				getProperty(PROP_SCRIPT_TEXT).setVisibleInPropSheet(
						((Boolean) evt.getNewValue()));
			}
		});
		addProperty(embeddedProperty);	
		getProperty(PROP_SCRIPT_TEXT).setVisibleInPropSheet(false);

	}
	

	protected IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}

	protected IPath getAbsolutePath(){
		//read file
		IPath absolutePath = getPath();
		if(!getPath().isAbsolute()){
    		absolutePath =
    			ResourceUtil.buildAbsolutePath(getWidgetModel(), getPath());
    	}
		return absolutePath;
	}
	
	protected boolean isEmbedded(){
		return (Boolean)getPropertyValue(PROP_EMBEDDED);
	}
	
	protected String getScriptText(){
		return (String)getPropertyValue(PROP_SCRIPT_TEXT);
	}


	@Override
	public String getDefaultDescription() {
		String desc = super.getDefaultDescription();
		if(isEmbedded())
			return desc;
		return  desc + " " + getPath(); //$NON-NLS-1$
	}
	
	/**Get reader of the script file.An instance will be created for later to use.
	 * Muse call {@link #closeReader()} to close this reader.
	 * @return the reader
	 * @throws Exception
	 */
	protected BufferedReader getReader() throws Exception{
		if(reader == null){
			inputStream  = ResourceUtil.pathToInputStream(getAbsolutePath(), false);
			reader = new BufferedReader(new InputStreamReader(inputStream));
		}
		return reader;
	}
	
	protected void closeReader(){
		if(reader !=null){
			try {
				inputStream.close();
				reader.close();
			} catch (IOException e) {			
			}
			inputStream =null;
			reader = null;
		}
	}

	protected abstract String getFileExtension();
	
	protected abstract String getScriptHeader();
}
