/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.datadefinition.PropertiesCopyData;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**The transfer for clip board related actions.
 * @author Xihui Chen
 */
public class PropertiesCopyDataTransfer extends ByteArrayTransfer {



	private static PropertiesCopyDataTransfer instance;

	private static final String TYPE_NAME = "PropertiesCopyDataTransfer:"  //$NON-NLS-1$
			+ System.currentTimeMillis();

	private static final int TYPEID = registerType(TYPE_NAME);


	public synchronized static PropertiesCopyDataTransfer getInstance() {
		if(instance == null)
			instance = new PropertiesCopyDataTransfer();
		return instance;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] {TYPEID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] {TYPE_NAME};
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (!isSupportedType(transferData) || !(checkInput(object))) {
			DND.error(DND.ERROR_INVALID_DATA);
		}

		try {
			super.javaToNative(((String)object).getBytes("UTF-8"), transferData); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			ErrorHandlerUtil.handleError("Convert to UTF-8 bytes failed", e);
		}
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		if(!isSupportedType(transferData))
			return null;
		byte[] bytes = (byte[])super.nativeToJava(transferData);
		if(bytes == null)
			return null;
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document doc = saxBuilder.build(new ByteArrayInputStream(bytes)); //$NON-NLS-1$
			Element root = doc.getRootElement();

			List<String> propIDList = new ArrayList<String>();
			AbstractWidgetModel widgetModel = null;
			for(Object o : root.getChildren()){
				if(o instanceof Element){
					Element e = (Element)o;
					if(e.getName().equals(CopyPropertiesAction.PROPID_ELEMENT))
						for(Object po : e.getChildren()){
							Element pe = (Element)po;
							propIDList.add(pe.getName());
						}
					else
						widgetModel = XMLUtil.XMLElementToWidget(e);
				}
			}
			return new PropertiesCopyData(widgetModel, propIDList);
		} catch (Exception e) {
	         OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to transfer XML to widget", e); //$NON-NLS-1$
		}
		return null;

	}

	/**
	 * Checks the provided input, which must be {@link PropertiesCopyData}
	 *
	 * @param input
	 *            the input to check
	 * @return true, if the input object is valid, false otherwise
	 */
	private boolean checkInput(final Object input) {
		if(input == null)
			return false;
		return input instanceof String;
	}

}
