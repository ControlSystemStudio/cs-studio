/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.widgets.Display;

/**The figure of linking container, which can host children widgets from another OPI file.
 * @author Xihui Chen
 *
 */
public class LinkingContainerFigure extends Figure implements Introspectable {
	
	private ScalableFreeformLayeredPane pane;
	
	
	private ZoomManager zoomManager;
	
	private boolean zoomToFitAll;
	
	@SuppressWarnings("deprecation")
	public LinkingContainerFigure() {
		ScrollPane scrollPane = new ScrollPane();
		pane = new ScalableFreeformLayeredPane();
		pane.setLayoutManager(new FreeformLayout());
		setLayoutManager(new StackLayout());
		add(scrollPane);
		FreeformViewport viewPort = new FreeformViewport();
		scrollPane.setViewport(viewPort);
		scrollPane.setContents(pane);	
		
		zoomManager = new ZoomManager(pane, viewPort){
			@Override
			protected double getFitPageZoomLevel() {
				double fitPageZoomLevel = super.getFitPageZoomLevel();
				if(fitPageZoomLevel<=0){
					fitPageZoomLevel = 0.1;					
				}
				return fitPageZoomLevel;
				
			}
		};
		
		addFigureListener(new FigureListener(){
			public void figureMoved(IFigure source) {
				Display.getDefault().asyncExec(new Runnable(){
					public void run() {
						updateZoom();
					}
				});
				
			}
		});
		
		
		updateZoom();
	}
	
	public IFigure getContentPane(){
		return pane;
	}
	
	public boolean isZoomToFitAll() {
		return zoomToFitAll;
	}
	
	@Override
	public void setBorder(Border border) {
		super.setBorder(border);
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				updateZoom();
			}
		});
	}

	public void setZoomToFitAll(boolean zoomToFitAll) {
		this.zoomToFitAll = zoomToFitAll;
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				updateZoom();
			}
		});
	}
	
	/**
	 * Refreshes the zoom.
	 */
	public void updateZoom() {		

		if (zoomToFitAll) {
			zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
		}else
			zoomManager.setZoom(1.0);
	}
	
	public ZoomManager getZoomManager() {
		return zoomManager;
	}

	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}
	

	
	
	
}
