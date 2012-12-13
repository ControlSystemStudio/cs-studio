/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;

import java.util.Map;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.csstudio.opibuilder.widgets.util.SingleSourceHelper;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The abstract figure for all SWT widget based figure. Note that there are 
some known issues regarding using SWT native widget in draw2D figure:
<ul>
<li>The order of SWT widget is always on top of draw2D figures.
The order between between SWT widgets works but it is initialized during OPI startup,
so change its order with change order action will only be reflected after reopen/run the opi. </li>
<li>Moving/Resizing a SWT widget is much slower than draw2D figure</li>
</ul>

 * 
 * @author Xihui Chen
 * 
 */
public abstract class AbstractSWTWidgetFigure<T extends Control> extends Figure {
	
	private class ToolTipListener extends MouseTrackAdapter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Control control;
		
		public ToolTipListener(Control control) {
			this.control = control;
		}	
		
		public void mouseEnter(MouseEvent e) {
			control.setToolTipText(editPart.getWidgetModel()
					.getTooltip());
		};
	}
	protected boolean runmode;
	protected AbstractBaseEditPart editPart;
	
	private boolean updateFlag;
	private UpdateListener updateManagerListener;
	private AncestorListener ancestorListener;
	private EditPart parentEditPart;
	private Composite composite;
	
	/**
	 * A composite that will be resized to show part of the widget if needed.
	 */
	private Composite wrapComposite;

	private boolean isIntersectViewPort = true;

	private boolean isShowing = true;
	
	private T swtWidget;
	
	//The scale factor when it was last scaled.
	private double lastScale =0;

	/**Construct the figure with SWT.NONE as style bit.
	 * @param editpart the editpart that holds this figure
	 */
	public AbstractSWTWidgetFigure(final AbstractBaseEditPart editpart){
		this(editpart, SWT.NONE);
	}
	
	/**Construct the figure.
	 * @param editpart the editpart that holds this figure.
	 * @param style style of the SWT widget, 
	 * which will be passed to {@link #createSWTWidget(Composite, int)}.
	 */
	public AbstractSWTWidgetFigure(final AbstractBaseEditPart editpart, final int style) {
		super();
		this.editPart = editpart;		
		this.composite = (Composite) editpart.getViewer().getControl();
		//In RAP, FigureCanvas has an inner canvas wrapped, so everything should be on the inner canvas.
		if(OPIBuilderPlugin.isRAP()){
			Control[] children = composite.getChildren();
			 for(Control control : children){
				 if(control instanceof Canvas)
					 composite = (Composite) control;
			 }
		}
		this.parentEditPart = editpart.getParent();
		this.runmode = editpart.getExecutionMode() == ExecutionMode.RUN_MODE;
		
		if (!isDirectlyOnDisplay()) {
			wrapComposite = new Composite(composite, SWT.NO_BACKGROUND);
			wrapComposite.setLayout(null);
			wrapComposite.setEnabled(runmode);
			wrapComposite.moveAbove(null);
		}
		
		swtWidget=createSWTWidget(getParentComposite(), style);
		
		editpart.addEditPartListener(new EditPartListener.Stub(){
			@Override
			public void partDeactivated(EditPart editpart) {
				dispose();
			}
		});
		
		// the widget should has the same relative position as its parent
		// container.
		ancestorListener = new AncestorListener.Stub() {
			public void ancestorMoved(org.eclipse.draw2d.IFigure arg0) {
				relocateWidget();
				updateWidgetVisibility();								
			}
		};
		addAncestorListener(ancestorListener);
		addFigureListener(new FigureListener() {

			public void figureMoved(IFigure source) {
				relocateWidget();
			}
		});
		
		//hook swt widget with GEF
		composite.getDisplay().asyncExec(new Runnable() {
			
			public void run() {
//				final Control swtWidget = getSWTWidget();
				if (swtWidget == null || swtWidget.isDisposed()) {
					return;
//					throw new RuntimeException("getSWTWidget() is null or disposed!");
				}
				//newly created widget on top	
				if(wrapComposite==null)
					swtWidget.moveAbove(null);
//				if(!runmode)
//					swtWidget.setEnabled(false);
				// select the swt widget when menu about to show

				MenuDetectListener menuDetectListener  = new MenuDetectListener() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void menuDetected(MenuDetectEvent e) {
						editPart.getViewer().select(editPart);								

					}
				};
				
				hookGEFToSWTWidget(swtWidget, menuDetectListener);

				// hook the context menu to combo
				swtWidget.setMenu(editPart.getViewer().getContextMenu()
						.createContextMenu(composite));
			}

			/**Add menu detect listener recursively to all children widgets inside the SWT Widget.
			 * @param swtWidget
			 * @param menuDetectListener
			 * @param toolTipListener 
			 */
			private void hookGEFToSWTWidget(final Control swtWidget,
					MenuDetectListener menuDetectListener) {
				swtWidget.addMenuDetectListener(menuDetectListener);
				SingleSourceHelper.swtWidgetAddMouseTrackListener(swtWidget,new ToolTipListener(swtWidget));
				//hack for composite widget with multiple children.
				if(swtWidget instanceof Composite){
					for(Control control : ((Composite)swtWidget).getChildren()){
						hookGEFToSWTWidget(control, menuDetectListener);						
					}
				}
			}
		});
		
	}

	/**
	 * @return true if this widget is directly put on a display.
	 */
	private boolean isDirectlyOnDisplay(){
		return parentEditPart instanceof DisplayEditpart;
	}
	@Override
	protected void layout() {
		super.layout();
		relocateWidget();
	}

	/**Get the SWT widget on this figure. 
	 * @return the SWT widget.
	 */
	public T getSWTWidget(){
		return swtWidget;
	}

	/**Create the SWT widget.This method will be call in constructor
	 *  {@link #AbstractSWTWidgetFigure(AbstractBaseEditPart, int)}
	 * @param parent the parent composite.
	 * @param style style of the SWT widget, 
	 * which is passed from the constructor 
	 * {@link #AbstractSWTWidgetFigure(AbstractBaseEditPart, int)}
	 * @return the SWT widget.
	 */
	abstract protected T createSWTWidget(Composite parent, int style);
	

	/**
	 * @return the composite
	 */
	private Composite getParentComposite() {
		if (wrapComposite != null)
			return wrapComposite;
		else
			return composite;
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if (getSWTWidget() != null && !getSWTWidget().isDisposed()){
			getSWTWidget().setEnabled(runmode && value);
			if(wrapComposite != null)
				wrapComposite.setEnabled(runmode && value);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		updateWidgetVisibility();
	}

	/**
	 * 
	 */
	private void updateWidgetVisibility() {
		if (isShowing != (isShowing() && isIntersectViewPort)) {
			isShowing = !isShowing;
			if (wrapComposite != null) {
				wrapComposite.setVisible(isShowing);				
			} else
				getSWTWidget().setVisible(isShowing);
		}
	}
	
	@Override
	public Color getForegroundColor() {
		if (getSWTWidget() != null)
			return getSWTWidget().getForeground();
		return super.getForegroundColor();
	}
	
	@Override
	public Color getBackgroundColor() {
		if (getSWTWidget() != null)
			return getSWTWidget().getBackground();
		return super.getBackgroundColor();
	}

	@Override
	public void setForegroundColor(Color fg) {
		if(!runmode)
			super.setForegroundColor(fg);
		if (getSWTWidget() != null)
			getSWTWidget().setForeground(fg);
	}

	@Override
	public void setBackgroundColor(Color bg) {
		if(!runmode)
			super.setBackgroundColor(bg);
		if (getSWTWidget() != null)
			getSWTWidget().setBackground(bg);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		repaintWidget();
		paintOutlineFigure(graphics);
		super.paintClientArea(graphics);
	}
	
	/**Paint an outline figure so it can be viewed in outline view in edit mode.
	 * It is a white filled rectangle with gray border by default. Subclass may override it
	 * accordingly. 
	 * @param graphics The Graphics used to paint
	 */
	protected void paintOutlineFigure(Graphics graphics){
		// draw this so that it can be seen in the outline view
		if (!runmode) {
			graphics.pushState();
			graphics.setBackgroundColor(ColorConstants.white);
			graphics.fillRectangle(getClientArea());
			if(getBorder() == null){
				graphics.setForegroundColor(ColorConstants.gray);
				graphics.drawRectangle(getBounds());
			}
			graphics.popState();
		}
	}

	/**
	 * 
	 */
	protected void repaintWidget() {
		updateWidgetVisibility();
		// the widget should has the same visibility as its parent container.
		// the update listener can only be added when the figure was painted,
		// because
		// the update manager is not assigned until the figure was painted for
		// the first time.
		if (!updateFlag) {
			updateFlag = true;
			if (!isDirectlyOnDisplay()) {
				updateManagerListener = new UpdateListener() {
					public void notifyPainting(Rectangle damage,
							@SuppressWarnings("rawtypes") Map dirtyRegions) {
						updateWidgetVisibility();
					}

					public void notifyValidating() {
					}

				};
				getUpdateManager().addUpdateListener(updateManagerListener);
			}
		}
	}

	/**
	 * relocate the widget so it follows the figure position.
	 */
	protected void relocateWidget() {
		if (wrapComposite != null
				&& getParent().getParent() instanceof Viewport) {
			Rectangle viewPortArea = getParent().getParent().getClientArea();
			Rectangle clientArea = getClientArea();
			getParent().translateToAbsolute(viewPortArea);
			translateToAbsolute(clientArea);
			isIntersectViewPort = viewPortArea.intersects(clientArea);
//			isIntersectViewPort = getParent().getParent().getClientArea()
//					.intersects(getClientArea());
		}
		
		GUIRefreshThread.getInstance(runmode).addIgnorableTask(
				new WidgetIgnorableUITask(this, new Runnable() {

					public void run() {
						if(!getSWTWidget().isDisposed())
							doRelocateWidget();
					}
				}, composite.getDisplay()));

	}

	private void doRelocateWidget() {
		boolean sizeWasSet = false;
		Rectangle clientArea = getClientArea();
		Rectangle rect = clientArea.getCopy();
		translateToAbsolute(rect);
		//scale the font if necessary
		double scale = (double)rect.height/(double)clientArea.height;
		if(Math.abs(scale-1) >0.05){
			if(Math.abs(scale-lastScale) >=0.05){
				FontData fontData = getFont().getFontData()[0];
				FontData newFontData = new FontData(fontData.getName(), 
						(int)(fontData.getHeight()*scale), fontData.getStyle());
				getSWTWidget().setFont(CustomMediaFactory.getInstance().getFont(newFontData));
				lastScale=scale;
			}			
		}else if(getSWTWidget().getFont() != getFont())
			getSWTWidget().setFont(getFont());
		
		//The trim should not be added here 
//		if(getSWTWidget() instanceof Scrollable){
//			org.eclipse.swt.graphics.Rectangle trim = ((Scrollable)getSWTWidget()).computeTrim(0,
//					0, 0, 0);
//			rect.translate(trim.x, trim.y);
//			rect.width += trim.width;
//			rect.height += trim.height;
//		}
		if (wrapComposite != null
				&& getParent().getParent() instanceof Viewport) {
			Rectangle viewPortArea = getParent().getParent().getClientArea();
			getParent().translateToAbsolute(viewPortArea);
			clientArea=rect;
			isIntersectViewPort = viewPortArea.intersects(clientArea);
			if (isIntersectViewPort) {
				// if the SWT widget is cut by viewPort
				if (!viewPortArea.contains(clientArea)) {					
					Rectangle intersection = viewPortArea.getIntersection(clientArea);					
					org.eclipse.swt.graphics.Rectangle oldBounds = wrapComposite.getBounds();
					if (oldBounds.x != (rect.x + intersection.x	- clientArea.x) ||
							oldBounds.y != (rect.y + intersection.y	- clientArea.y) ||
							oldBounds.width != intersection.width || oldBounds.height != intersection.height){
						wrapComposite.setBounds(
								rect.x + intersection.x	- clientArea.x, 
								rect.y + intersection.y	- clientArea.y, 
								intersection.width,
								intersection.height);						
					}
					oldBounds = getSWTWidget().getBounds();
					if (oldBounds.x != (clientArea.x - intersection.x) ||
							oldBounds.y != (clientArea.y - intersection.y) ||
							oldBounds.width != rect.width || oldBounds.height != rect.height){
						getSWTWidget().setBounds(clientArea.x - intersection.x,
								clientArea.y - intersection.y, rect.width, rect.height);
					}
					sizeWasSet = true;
				} else {
					Point oldLoc = getSWTWidget().getLocation();
					if (oldLoc.x != 0 || oldLoc.y != 0)
						getSWTWidget().setLocation(0, 0);
				}
			}
		}

		if (!sizeWasSet) {
			if (wrapComposite != null) {
				Rectangle oldBounds = new Rectangle(wrapComposite.getBounds());
				if (!oldBounds.equals(rect))
					wrapComposite.setBounds(rect.x, rect.y, rect.width,
							rect.height);
				Point oldSize = getSWTWidget().getSize();
				if (oldSize.x != rect.width || oldSize.y != rect.height)
					getSWTWidget().setSize(rect.width, rect.height);
			} else if (!getSWTWidget().getBounds().equals(
					new org.eclipse.swt.graphics.Rectangle(rect.x, rect.y,
							rect.width, rect.height)))
				getSWTWidget().setBounds(rect.x, rect.y, rect.width,
						rect.height);

		}
	}

	@Override
	public void setFont(Font f) {
		super.setFont(f);
		if (getSWTWidget() != null)
			getSWTWidget().setFont(f);
	}

	/**
	 * Dispose SWT widget. The SWT widget will be automatically disposed when widget
	 * editpart is deactivated. Subclass should not dispose the SWT widget again.
	 */
	protected void dispose() {
		if (updateFlag && updateManagerListener != null)
			getUpdateManager().removeUpdateListener(updateManagerListener);
		removeAncestorListener(ancestorListener);
		Runnable task;
		if (wrapComposite != null) {
			task = new Runnable() {
				public void run() {
					if (!wrapComposite.isDisposed()) {
						getSWTWidget().setMenu(null);
						wrapComposite.dispose();
						wrapComposite = null;
					}
				}
			};
		} else {
			task = new Runnable() {
				public void run() {
					if (!getSWTWidget().isDisposed()) {
						getSWTWidget().setMenu(null);
						getSWTWidget().dispose();
//						composite.update();
					}
				}
			};
		}
//		UIBundlingThread.getInstance().addRunnable(composite.getDisplay(), task);
		if (composite.getDisplay().getThread() == Thread.currentThread()) {
			task.run();
		} else
			composite.getDisplay().asyncExec(task);
	}

}
