/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.figures;

import java.util.Map;

import org.csstudio.opibuilder.datadefinition.WidgetIgnorableUITask;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.csstudio.opibuilder.widgets.util.SingleSourceHelper;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.AncestorListener;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scrollable;

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
public abstract class AbstractSWTWidgetFigure extends Figure {

	protected boolean runmode;
	private boolean updateFlag;
	private UpdateListener updateManagerListener;
	private AncestorListener ancestorListener;
	protected AbstractContainerModel parentModel;
	protected Composite composite;
	protected AbstractBaseEditPart editPart;

	/**
	 * A composite that will be resized to show part of the widget if needed.
	 */
	protected Composite wrapComposite;

	protected boolean isIntersectViewPort = true;

	protected boolean isShowing = true;
	
	private Rectangle oldClientArea;

	/**Construct the figure.
	 * @param editpart the editpart that holds this figure.
	 */
	public AbstractSWTWidgetFigure(final AbstractBaseEditPart editpart) {
		super();
		this.editPart = editpart;
		this.composite = (Composite) editpart.getViewer().getControl();
		this.parentModel = editpart.getWidgetModel().getParent();
		this.runmode = editpart.getExecutionMode() == ExecutionMode.RUN_MODE;
		if (!(parentModel instanceof DisplayModel)) {
			wrapComposite = new Composite(composite, SWT.NO_BACKGROUND);
			wrapComposite.setLayout(null);
			wrapComposite.setEnabled(runmode);
			wrapComposite.moveAbove(null);
		}
		
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
				final Control swtWidget = getSWTWidget();
				if (swtWidget == null) {
					throw new RuntimeException("getSWTWidget() returns null!");
				}
				//newly created widget on top	
				if(wrapComposite==null)
					swtWidget.moveAbove(null);
				swtWidget.setEnabled(runmode);
				// select the combo when mouse down
//				swtWidget
//						.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {
//							@Override
//							public void mouseDown(
//									org.eclipse.swt.events.MouseEvent e) {
//								editPart.getViewer().select(editPart);								
//							}
//						});
				swtWidget.addMenuDetectListener(new MenuDetectListener() {
					
					@Override
					public void menuDetected(MenuDetectEvent e) {
						editPart.getViewer().select(editPart);								

					}
				});
				// update tooltip
				SingleSourceHelper.swtWidgetAddMouseTrackListener(swtWidget, 
						new MouseTrackAdapter() {
					@Override
					public void mouseEnter(MouseEvent e) {
						swtWidget.setToolTipText(editPart.getWidgetModel()
								.getTooltip());
					}
				});

				// hook the context menu to combo
				swtWidget.setMenu(editPart.getViewer().getContextMenu()
						.createContextMenu(composite));
			}
		});
		
	}

	@Override
	protected void layout() {
		super.layout();
		if(!getClientArea().equals(oldClientArea)){
			relocateWidget();
			oldClientArea = getClientArea();
		}
	}

	/**Get the SWT widget. 
	 *<b>Note: </b> 
	 * The SWT widget should be created in the composite returned from {@link #getParentComposite()}. 
	 * @return the swt widget.
	 */
	abstract public Control getSWTWidget();

	public Composite getComposite() {
		return composite;
	}

	/**
	 * @return the composite
	 */
	public Composite getParentComposite() {
		if (wrapComposite != null)
			return wrapComposite;
		else
			return composite;
	}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		if (getSWTWidget() != null && !getSWTWidget().isDisposed())
			getSWTWidget().setEnabled(runmode && value);
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
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		if (getSWTWidget() != null)
			getSWTWidget().setForeground(fg);
	}

	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		if (getSWTWidget() != null)
			getSWTWidget().setBackground(bg);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		repaintWidget();
		super.paintClientArea(graphics);
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
			if (!(parentModel instanceof DisplayModel)) {
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
			isIntersectViewPort = getParent().getParent().getClientArea()
					.intersects(getClientArea());
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
		final Rectangle clientArea = getClientArea();
		final Rectangle rect = clientArea.getCopy();
		translateToAbsolute(rect);
		if(getSWTWidget() instanceof Scrollable){
			org.eclipse.swt.graphics.Rectangle trim = ((Scrollable)getSWTWidget()).computeTrim(0,
					0, 0, 0);
			rect.translate(trim.x, trim.y);
			rect.width += trim.width;
			rect.height += trim.height;
		}
		if (wrapComposite != null
				&& getParent().getParent() instanceof Viewport) {
			Rectangle viewPortArea = getParent().getParent().getClientArea();
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
			} else
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
	 * Dispose SWT widget. Subclass should dispose its widget before calling super.dispose()
	 * because the parent composite might be disposed from here.  
	 */
	public void dispose() {
		if (updateFlag && updateManagerListener != null)
			getUpdateManager().removeUpdateListener(updateManagerListener);
		removeAncestorListener(ancestorListener);
		if (wrapComposite != null) {
			UIBundlingThread.getInstance().addRunnable(
					composite.getDisplay(),new Runnable() {
						public void run() {
							if (!wrapComposite.isDisposed()) {
								getSWTWidget().setMenu(null);
								wrapComposite.dispose();								
								wrapComposite = null;		
							}
						}
					});
		}else{
			UIBundlingThread.getInstance().addRunnable(composite.getDisplay(),
					new Runnable() {
						public void run() {
							if (!getSWTWidget().isDisposed()) {
								getSWTWidget().setMenu(null);
								getSWTWidget().dispose();
								composite.update();
							}
						}
					});
		}
			
	}

}
