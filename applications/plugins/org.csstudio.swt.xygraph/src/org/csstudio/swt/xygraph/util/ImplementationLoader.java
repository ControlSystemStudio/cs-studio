/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.util;

import java.util.logging.Level;

import org.csstudio.swt.xygraph.Activator;
import org.eclipse.osgi.util.NLS;

/**
 * Implementation loader for RAP/RCP single sourcing.
 * @author Xihui Chen
 *
 */
public class ImplementationLoader {

    public static Object newInstance(Class<?> type){
        String name = type.getName();
        Object result = null;
        try {
            result = type.getClassLoader().loadClass(name + "Impl").newInstance(); //$NON-NLS-1$
        } catch (Exception e) {
            Activator.getLogger().log(Level.SEVERE,
                    NLS.bind("Failed to load class {0} from fragment.", name+"Impl"), e); //$NON-NLS-2$
        }
        return result;
    }

}
