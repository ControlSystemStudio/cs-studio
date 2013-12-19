/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

/** Listener gets notified when model changes.
 *  @author Kay Kasemir
 *  @author benhadj naceur @  sopra group - iter
 */
public interface ModelListener
{
	/** Invoked when the model changed in some way.
	 *  <p>
	 *  <b>Note:</b> Call can originate from non-GUI thread.
	 *  
	 *  @param model Model that has new data or is somehow different
	 */
    public void modelChanged(Model model);
    
    /**
     * Listener on error model.
     *
     * @param errorMsg the error msg
     */
    public void onErrorModel(final String errorMsg);
    
    
}
