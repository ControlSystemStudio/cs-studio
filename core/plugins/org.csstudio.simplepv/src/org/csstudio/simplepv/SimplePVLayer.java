/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**The entry of simplepv layer. Clients should use this entry to
 * get the PV factory to create simple pv.
 * @author Xihui Chen
 *
 */
public class SimplePVLayer {
	
	private static final String EXTPOINT_PVFACTORY = "org.csstudio.simplepv.pvfactory"; //$NON-NLS-1$
	
	private static Map<String, AbstractPVFactory> factoryMap = new HashMap<String, AbstractPVFactory>(4);
	
	public static AbstractPVFactory getPVFactory(String factoryName) throws CoreException{
		if(!factoryMap.containsKey(factoryName)){
			AbstractPVFactory pvFactory = createPVFactory(factoryName);
			factoryMap.put(factoryName, pvFactory);
		}		
		return factoryMap.get(factoryName);				
	}
	
	private static AbstractPVFactory createPVFactory(String factoryName) throws CoreException{
		IExtensionRegistry extReg = Platform.getExtensionRegistry();		
		IConfigurationElement[] confElements = 
				extReg.getConfigurationElementsFor(EXTPOINT_PVFACTORY);
		for(IConfigurationElement element : confElements){
			String name = element.getAttribute("id"); //$NON-NLS-1$
			if(name.equals(factoryName)){
				Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
				if(object instanceof AbstractPVFactory){
					return (AbstractPVFactory)object;
				}
			}
		}		
		return null;		
	}
	
	public static String[] getAllPVFactoryExtensions(){
		IExtensionRegistry extReg = Platform.getExtensionRegistry();		
		IConfigurationElement[] confElements = 
				extReg.getConfigurationElementsFor(EXTPOINT_PVFACTORY);
		String[] result = new String[confElements.length];
		int i=0;
		for (IConfigurationElement element : confElements) {
			String name = element.getAttribute("id"); //$NON-NLS-1$
			result[i++] = name;
		}
		return result;
	}
	
	
	

}
