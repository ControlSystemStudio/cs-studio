package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class DisplayEditpart extends AbstractContainerEditpart {

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();

		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new RootComponentEditPolicy());
	}
	
	@Override
	public void activate() {
		super.activate();
		initProperties();
	}
	
	private void initProperties() {
		for(String prop_id : getCastedModel().getAllPropertyIDs()){
			getCastedModel().getProperty(prop_id).firePropertyChange(null, 
					getCastedModel().getPropertyValue(prop_id));
		}		
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler backColorHandler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				getViewer().getControl().setBackground(
						CustomMediaFactory.getInstance().getColor((RGB)newValue));
				return false;
			}
		};		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_COLOR_BACKGROUND, backColorHandler);
		
		//grid
		if(getExecutionMode() == ExecutionMode.EDIT_MODE){
			IWidgetPropertyChangeHandler gridColorHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					((ScalableFreeformRootEditPart)getViewer().getRootEditPart())
					.getLayer(LayerConstants.GRID_LAYER).setForegroundColor(CustomMediaFactory.getInstance()
							.getColor((RGB)newValue));
					return false;
				}
			};		
			setPropertyChangeHandler(DisplayModel.PROP_COLOR_FOREGROUND, gridColorHandler);
			
			IWidgetPropertyChangeHandler gridSpaceHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					getViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING, 
							new Dimension((Integer)newValue, (Integer)newValue));
					return false;
				}
			};		
			setPropertyChangeHandler(DisplayModel.PROP_GRID_SPACE, gridSpaceHandler);
			
			IWidgetPropertyChangeHandler showGridHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					getViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, (Boolean)newValue);
					getViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, (Boolean)newValue);
					return false;
				}
			};		
			setPropertyChangeHandler(DisplayModel.PROP_SHOW_GRID, showGridHandler);
			
			IWidgetPropertyChangeHandler showRulerHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					getViewer().setProperty(
							RulerProvider.PROPERTY_RULER_VISIBILITY, (Boolean)newValue);
					return false;
				}
			};		
			setPropertyChangeHandler(DisplayModel.PROP_SHOW_RULER, showRulerHandler);
			
			IWidgetPropertyChangeHandler snapGeoHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					getViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, (Boolean)newValue);
					return false;
				}
			};		
			setPropertyChangeHandler(DisplayModel.PROP_SNAP_GEOMETRY, snapGeoHandler);
			
			IWidgetPropertyChangeHandler showEditRangeHandler = new IWidgetPropertyChangeHandler(){
				public boolean handleChange(Object oldValue, Object newValue,
						IFigure figure) {
					return true;
				}
			};		
			setPropertyChangeHandler(DisplayModel.PROP_SHOW_EDIT_RANGE, showEditRangeHandler);
		}
	}

	@Override
	protected IFigure doCreateFigure() {
		
		Figure f = new FreeformLayer(){
			@Override
			protected void paintFigure(Graphics graphics) {
				super.paintFigure(graphics);
				if(getExecutionMode() == ExecutionMode.EDIT_MODE && 
						((DisplayModel)getModel()).isShowEditRange()){
					graphics.pushState();
					graphics.setLineStyle(SWT.LINE_DASH);
					graphics.setForegroundColor(ColorConstants.black);
					graphics.drawRectangle(
							new Rectangle(new Point(0, 0), getCastedModel().getSize()));
					graphics.popState();
				}
			}
		};
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());
		
		return f;
	}
	
	@Override
	protected synchronized void doRefreshVisuals(IFigure refreshableFigure) {
		super.doRefreshVisuals(refreshableFigure);
		figure.repaint();
	}
	

}
