/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editpolicies;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.CellEditorActionHandler;

/**This handle allows setting pv name directly on widget.
 * @author Xihui Chen
 *
 */
public class PVWidgetSelectionHandle extends AbstractHandle {

	private static final Dimension PREFERED_SIZE = new Dimension(10, 10);
	private static final String TIP = "<Set PV Name>";
	private  static final Color handleBackColor = CustomMediaFactory.getInstance().getColor(127, 127, 127);
	private  static final Color handleFilledBackColor = CustomMediaFactory.getInstance().getColor(255, 128, 0);
	
	private Dimension textExtents;
	private AbstractWidgetModel widgetModel;
	private String pvName = "";

	public PVWidgetSelectionHandle(final GraphicalEditPart owner) {
		super(owner, new Locator() {
			
			@Override
			public void relocate(IFigure target) {
				IFigure ownerFigure = owner.getFigure();
				Dimension preferedSize = target.getPreferredSize();
				Point targetLocation = ownerFigure.getBounds().getLocation();
				ownerFigure.translateToAbsolute(targetLocation);
				target.translateToRelative(targetLocation);
				targetLocation.translate(-3, -preferedSize.height-2);
				target.setBounds(new Rectangle(targetLocation, preferedSize));
			}
		});			
		setCursor(Cursors.HAND);
				
		if(owner.getModel() instanceof AbstractWidgetModel)
			this.widgetModel = (AbstractWidgetModel) owner.getModel();
		
		if(widgetModel instanceof IPVWidgetModel){
			String p = ((IPVWidgetModel)widgetModel).getPVName();
			if(p!=null && !p.isEmpty()){
				pvName = p;
			}
		}
		setToolTip(new Label(pvName.isEmpty()? "Click to set PV name.":pvName));
		setBorder(new LineBorder(ColorConstants.white));
	}
	

	
	private Dimension getTextExtent(){
		if(textExtents == null){
			textExtents = TextUtilities.INSTANCE.getTextExtents(pvName, getFont());
		}
		return textExtents;
	}
	
	@Override
	protected DragTracker createDragTracker() {
		DragEditPartsTracker tracker = new DragEditPartsTracker(getOwner()){
			@Override
			protected boolean handleButtonDown(int button) {
				
				if((button == 1 || button==3) && widgetModel instanceof IPVWidgetModel){
					
					DirectEditManager directEditManager = new PVNameDirectEditManager(getOwner(), new CellEditorLocator() {
						
						@Override
						public void relocate(CellEditor celleditor) {
							Rectangle rect;
							int width=120;
							if(!pvName.isEmpty() && getTextExtent().width>120)
								width = getTextExtent().width+4;
							
							rect = new Rectangle(PVWidgetSelectionHandle.this.getLocation(), 
										new Dimension(width, getTextExtent().height));
							
							translateToAbsolute(rect);							
							Text control = (Text) celleditor.getControl();
							org.eclipse.swt.graphics.Rectangle trim = control .computeTrim(0, 0, 0, 0);
							rect.translate(trim.x, trim.y);
							rect.width += trim.width;
							rect.height += trim.height;
							control.setBounds(rect.x, rect.y, rect.width, rect.height);
						}
					}); 					
					directEditManager.show();

					
				}				
				return true;
			}
		};
		tracker.setDefaultCursor(getCursor());
		return tracker;
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);		
		graphics.setAlpha(150);
		if(pvName == null|| pvName.isEmpty())
			graphics.setBackgroundColor(handleBackColor);
		else
			graphics.setBackgroundColor(handleFilledBackColor);
		graphics.fillRectangle(getClientArea());
		graphics.setAlpha(250);		
	}



	

	
	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {			
		return PREFERED_SIZE;
	}
	
	final class PVNameDirectEditManager extends DirectEditManager{

		private boolean committing;
		private IActionBars actionBars;
		private CellEditorActionHandler actionHandler;
		private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
		public PVNameDirectEditManager(GraphicalEditPart source,
				CellEditorLocator locator) {
			super(source, null, locator);
		}
		
			
			protected CellEditor createCellEditorOn(Composite composite) {
				final TextCellEditor cellEditor = new TextCellEditor(
				(Composite) getEditPart().getViewer().getControl(), SWT.SINGLE);
				
				return cellEditor;
			};
			
			@Override
			protected void initCellEditor() {
				getCellEditor().getControl().setBackground(ColorConstants.white);
				getCellEditor().setValue(pvName.isEmpty()?TIP:pvName);
				getCellEditor().getControl().moveAbove(null);
				// Hook the cell editor's copy/paste actions to the actionBars so that they can
				// be invoked via keyboard shortcuts.
				IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if(activeEditor != null){
					actionBars = activeEditor.getEditorSite().getActionBars();
					saveCurrentActions(actionBars);
					actionHandler = new CellEditorActionHandler(actionBars);
					actionHandler.addCellEditor(getCellEditor());
					actionBars.updateActionBars();
				}
			}
		
			/**
			 * Commits the current value of the cell editor by getting a {@link Command}
			 * from the source edit part and executing it via the {@link CommandStack}.
			 * Finally, {@link #bringDown()} is called to perform and necessary cleanup.
			 */
			protected void commit() {
				if (committing)
					return;
				committing = true;
				try {
					eraseFeedback();
					String newName = (String) getCellEditor().getValue();
					if (isDirty()&&!newName.equals(TIP)) {
						CommandStack stack = getEditPart().getViewer().getEditDomain()
								.getCommandStack();
						stack.execute(new SetWidgetPropertyCommand(
								widgetModel,
								IPVWidgetModel.PROP_PVNAME,
								newName));
					}
				} finally {
					bringDown();
					committing = false;
				}
			}
			
			private void restoreSavedActions(IActionBars actionBars){
				actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
				actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
				actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
				actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
				actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
				actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), find);
				actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
				actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
			}

			private void saveCurrentActions(IActionBars actionBars) {
				copy = actionBars.getGlobalActionHandler(ActionFactory.COPY.getId());
				paste = actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId());
				delete = actionBars.getGlobalActionHandler(ActionFactory.DELETE.getId());
				selectAll = actionBars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
				cut = actionBars.getGlobalActionHandler(ActionFactory.CUT.getId());
				find = actionBars.getGlobalActionHandler(ActionFactory.FIND.getId());
				undo = actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
				redo = actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
			}
			
			protected void bringDown() {

				if (actionHandler != null) {
					actionHandler.dispose();
					actionHandler = null;
				}
				if (actionBars != null) {
					restoreSavedActions(actionBars);
					actionBars.updateActionBars();
					actionBars = null;
				}
				
				super.bringDown();

			}
		
		
	}
	
}
