/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.csstudio.opibuilder.widgets.model.SashContainerModel;
import org.csstudio.swt.widgets.figures.SashContainerFigure;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

/**
 * The Editpart Controller for a Sash Container
 * 
 * @author Xihui Chen
 * 
 */
public class SashContainerEditPart extends AbstractContainerEditpart {

	private GroupingContainerModel groupContainer1, groupContainer2;

	@Override
	protected IFigure doCreateFigure() {
		final SashContainerFigure figure = new SashContainerFigure();		
		SashContainerModel model = getWidgetModel();		
		figure.setSashStyle(model.getSashStyle());
		figure.setSashWidth(model.getSashWidth());
		figure.setHorizontal(model.isHorizontal());
		figure.setOpaque(!getWidgetModel().isTransparent());
		figure.setSashPosition(model.getSashPosition());
		

		return figure;
	}

	@Override
	public void activate() {
		super.activate();
		if(getWidgetModel().getChildren().size()==0){
			groupContainer1 = createGroupingContainerModel(true);
			groupContainer2 = createGroupingContainerModel(false);
			UIBundlingThread.getInstance().addRunnable(new Runnable() {

				@Override
				public void run() {
					getWidgetModel().addChild(groupContainer1);
					getWidgetModel().addChild(groupContainer2);
				}
			});
		}else{
			groupContainer1 = (GroupingContainerModel) getWidgetModel().getChildren().get(0);
			groupContainer2 = (GroupingContainerModel) getWidgetModel().getChildren().get(1);			
		}
		getSashFigure().addLayoutListener(new LayoutListener.Stub(){	
			
			@Override
			public void postLayout(IFigure container) {


				Rectangle[] bounds = getSashFigure().getSubPanelsBounds();
				if(groupContainer1.getBounds().equals(bounds[0]) &&
						groupContainer2.getBounds().equals(bounds[1]))
					return;
				//make sure the origin size has been recorded.
				groupContainer1.getOriginSize();
				groupContainer2.getOriginSize();
				groupContainer1.setBounds(bounds[0]);
				groupContainer2.setBounds(bounds[1]);
				if(getExecutionMode() == ExecutionMode.RUN_MODE){
					if(getWidgetModel().isPanel1AutoScaleChildren())
						groupContainer1.scaleChildren();
					if(getWidgetModel().isPanel2AutoScaleChildren())
						groupContainer2.scaleChildren();
				}
				if(getExecutionMode() == ExecutionMode.EDIT_MODE){
					getViewer().getEditDomain().getCommandStack().execute(
						new SetWidgetPropertyCommand(getWidgetModel(), SashContainerModel.PROP_SASH_POSITION, 
						getSashFigure().getSashPosition()));
				}
			}
		});		
		
		getSashFigure().setSashPosition(getWidgetModel().getSashPosition());

		
	}
	
	private GroupingContainerModel createGroupingContainerModel(boolean isPanel1){
		GroupingContainerModel groupingContainerModel = new GroupingContainerModel();
		groupingContainerModel.setName(isPanel1? "Panel 1" : "Panel 2");
		groupingContainerModel.setBorderStyle(BorderStyle.NONE);
		groupingContainerModel.setPropertyValue(GroupingContainerModel.PROP_TRANSPARENT, true);

		return groupingContainerModel;
	}
	
	/**
	 * {@inheritDoc} Overidden, to set the selection behaviour of child
	 * EditParts.
	 */
	@Override
	protected final EditPart createChild(final Object model) {
		EditPart result = super.createChild(model);

		// setup selection behavior for the new child
		if (result instanceof AbstractBaseEditPart) {
			((AbstractBaseEditPart) result).setSelectable(false);
		}

		return result;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, null);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
		
		//the snap feedback effect
		installEditPolicy("Snap Feedback", null); //$NON-NLS-1$
		if(getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE, null);

	}

	@Override
	public SashContainerModel getWidgetModel() {
		return (SashContainerModel) super.getWidgetModel();
	}

	@Override
	protected void registerPropertyChangeHandlers() {

		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				getSashFigure().setSashPosition((Double)newValue);
				return false;
			}
		};
		
		setPropertyChangeHandler(SashContainerModel.PROP_SASH_POSITION, handler);

		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				getSashFigure().setSashStyle(getWidgetModel().getSashStyle());
				return false;
			}
		};
		
		setPropertyChangeHandler(SashContainerModel.PROP_SASH_STYLE, handler);
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				getSashFigure().setSashWidth((Integer)newValue);
				return false;
			}
		};
		
		setPropertyChangeHandler(SashContainerModel.PROP_SASH_WIDTH, handler);
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				getSashFigure().setHorizontal((Boolean)newValue);
				return false;
			}
		};
		
		setPropertyChangeHandler(SashContainerModel.PROP_HORIZONTAL, handler);		
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				getSashFigure().setOpaque(!(Boolean)newValue);
				return false;
			}
		};
		
		setPropertyChangeHandler(SashContainerModel.PROP_TRANSPARENT, handler);
		
	}

	private SashContainerFigure getSashFigure() {
		return (SashContainerFigure) getFigure();
	}

	@Override
	public IFigure getContentPane() {
		return getSashFigure().getContentPane();
	}	


}
