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

/**The entry point of simplepv layer. Clients should use this entry to
 * get the PV factory to create PVs.
 * @author Xihui Chen
 *
 */
public class SimplePVLayer {

    private static final String EXTPOINT_PVFACTORY = "org.csstudio.simplepv.pvfactory"; //$NON-NLS-1$

    private static Map<String, AbstractPVFactory> factoryMap = new HashMap<String, AbstractPVFactory>(4);

    /**If there is only one {@link AbstractPVFactory} implementation, return it.
     * If there are multiple implementations, return the default PV factory which
     * is configured in the preference.
     * @return Default PV Factory or null if not exist.
     * @throws CoreException on loading extensions error.
     */
    public static AbstractPVFactory getPVFactory() throws CoreException{
        String[] allPVFactoryExtensions = getAllPVFactoryExtensions();
        if(allPVFactoryExtensions.length==1)
            return getPVFactory(allPVFactoryExtensions[0]);

        String defaultPVFactoryID = PreferenceHelper.getDefaultPVFactoryID();
        if(defaultPVFactoryID == null)
            return null;

        return getPVFactory(defaultPVFactoryID);
    }


    /**Get a PV Factory from its ID.
     * @param pvFactoryId ID of the PV Factory extension.
     * @return the PV Factory. null if not exist.
     * @throws CoreException on loading extensions error.
     */
    public static AbstractPVFactory getPVFactory(String pvFactoryId) throws CoreException{
        if(!factoryMap.containsKey(pvFactoryId)){
            AbstractPVFactory pvFactory = createPVFactory(pvFactoryId);
            factoryMap.put(pvFactoryId, pvFactory);
        }
        return factoryMap.get(pvFactoryId);
    }

    private static AbstractPVFactory createPVFactory(String pvFactoryID) throws CoreException{
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        IConfigurationElement[] confElements =
                extReg.getConfigurationElementsFor(EXTPOINT_PVFACTORY);
        for(IConfigurationElement element : confElements){
            String name = element.getAttribute("id"); //$NON-NLS-1$
            if(name.equals(pvFactoryID)){
                Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
                if(object instanceof AbstractPVFactory){
                    return (AbstractPVFactory)object;
                }
            }
        }
        return null;
    }

    /**Get all available PV Factory extensions.
     * @return the IDs of all available PV Factory extensions.
     */
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
