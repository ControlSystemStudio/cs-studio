/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv.utilitypv;

import java.util.concurrent.Executor;

import org.csstudio.simplepv.AbstractPVFactory;
import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;

/**A Factory to create PV that is implemented on top of Utility.PV.
 * This is only used for compatibility of old BOY OPIs. Other tools should
 * not use this. In this implementation, maxUpdateRate and bufferAllValues
 * are ignored.
 * @author Xihui Chen
 *
 */
public class UtilityPVFactory extends AbstractPVFactory {


    /* (non-Javadoc)
     * @see org.csstudio.simplepv.AbstractPVFactory#createPV(java.lang.String, boolean, int, boolean, java.util.concurrent.Executor, org.csstudio.simplepv.ExceptionHandler)
     *
     */
    @Override
    public IPV createPV(String name, boolean readOnly, long minUpdatePeriod, boolean bufferAllValues,
            Executor notificationThread, ExceptionHandler exceptionHandler) throws Exception {
        return new UtilityPV(name, readOnly, notificationThread, exceptionHandler);
    }

}
