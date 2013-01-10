/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**Tweak GEF ZoomAction
 * Base zoom action that only dedicates to a part. The GEF ZoomAction's zoom manager is 
 * not changeable, which result in using RetargetAction to switch parts. This doesn't work
 * for OPIView which has its own toolbar.
 *  Sub-classes can perform zoom in or zoom out.
 * 
 * @author danlee, Xihui Chen
 */
abstract class PartZoomAction extends Action implements ZoomListener, Disposable {

	/**
	 * The ZoomManager used to zoom in or out
	 */
	protected ZoomManager zoomManager;
	private IWorkbenchPart part;

	/**
	 * Constructor a empty ZoomAction. Its part need to be set to make this action work.
	 * 
	 * @param text
	 *            the action's text, or <code>null</code> if there is no text
	 * @param image
	 *            the action's image, or <code>null</code> if there is no image
	 */
	public PartZoomAction(String text, ImageDescriptor image) {
		super(text, image);
	}
	
	/**
	 * @param part a part which must have a ZoomManager Adapter.
	 */
	public void setPart(IWorkbenchPart part){
		if(this.part == part)
			return;
		this.part = part;
		if(zoomManager != null)
			zoomManager.removeZoomListener(this);
		ZoomManager newZoomManager = (ZoomManager)part.getAdapter(ZoomManager.class);
		if(newZoomManager != null){
			newZoomManager.addZoomListener(this);
			zoomManager = newZoomManager;
		}
	}
	
	/**
	 * @see org.eclipse.gef.Disposable#dispose()
	 */
	public void dispose() {
		if(zoomManager != null)
			zoomManager.removeZoomListener(this);
	}

}
