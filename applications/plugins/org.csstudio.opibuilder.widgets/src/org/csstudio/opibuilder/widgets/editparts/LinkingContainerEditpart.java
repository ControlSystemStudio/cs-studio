package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.widgets.figures.LinkingContainerFigure;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

public class LinkingContainerEditpart extends AbstractContainerEditpart{

	@Override
	protected IFigure doCreateFigure() {
		LinkingContainerFigure f = new LinkingContainerFigure();		
		f.setZoomToFitAll(getCastedModel().isAutoFit());
		return f;
	}
	
	
	@Override
	protected void createEditPolicies() {	
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, null);				
		installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
		
	}
	
	@Override
	public void activate() {
		super.activate();		
	}
	
	@Override
	public LinkingContainerModel getCastedModel() {
		return (LinkingContainerModel)getModel();
	}
	

	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if(newValue != null && newValue instanceof IPath){						
					IPath path = (IPath)newValue;					
					loadWidgets(path);			
				}
				return true;
			}
		};
		
		setPropertyChangeHandler(LinkingContainerModel.PROP_OPI_FILE, handler);
		
		
		//load
		
		loadWidgets(getCastedModel().getOPIFilePath());
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((LinkingContainerFigure)figure).setZoomToFitAll((Boolean)newValue);
				((LinkingContainerFigure)figure).updateZoom();
				return true;
			}
		};
		setPropertyChangeHandler(LinkingContainerModel.PROP_ZOOMTOFITALL, handler);
		
		
		
	}
	
	
	/**
	 * @param path the path of the OPI file
	 */
	private void loadWidgets(IPath path) {
		Object[] children = getCastedModel().getChildren().toArray();
		for(Object child : children)
			getCastedModel().removeChild((AbstractWidgetModel) child);
		
		DisplayModel displayModel = new DisplayModel();
		try {
			XMLUtil.fillDisplayModelFromInputStream(
					ResourceUtil.pathToInputStream(path), displayModel);
			//for(AbstractWidgetModel child : displayModel.getChildren())	
				getCastedModel().addChild(displayModel);
		} catch (Exception e) {
			LabelModel loadingErrorLabel = new LabelModel();
			loadingErrorLabel.setLocation(0, 0);
			loadingErrorLabel.setSize(getCastedModel().getSize().getCopy().shrink(5, 5));
			loadingErrorLabel.setText(e.getMessage());
			getCastedModel().addChild(loadingErrorLabel);						
		}
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				((LinkingContainerFigure)getFigure()).updateZoom();				
			}
		});
	}
	
	@Override
	public IFigure getContentPane() {
		return ((LinkingContainerFigure)getFigure()).getContentPane();
	}

}
