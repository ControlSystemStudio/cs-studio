package org.csstudio.opibuilder.widgets.editparts;

import java.util.LinkedHashMap;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.swt.widgets.figures.LinkingContainerFigure;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**The Editpart Controller for a linking Container
 * @author Xihui Chen
 *
 */
public class LinkingContainerEditpart extends AbstractContainerEditpart{


	@Override
	protected IFigure doCreateFigure() {
		LinkingContainerFigure f = new LinkingContainerFigure();		
		f.setZoomToFitAll(getWidgetModel().isAutoFit());
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
		loadWidgets(getWidgetModel().getOPIFilePath(), false);
	}
	
	@Override
	public LinkingContainerModel getWidgetModel() {
		return (LinkingContainerModel)getModel();
	}
	

	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				if(newValue != null && newValue instanceof IPath){						
					IPath absolutePath = (IPath)newValue;
					if(!absolutePath.isAbsolute())
						absolutePath = ResourceUtil.buildAbsolutePath(
								getWidgetModel(), absolutePath);				
					loadWidgets(absolutePath, true);			
				}
				return true;
			}
		};
		
		setPropertyChangeHandler(LinkingContainerModel.PROP_OPI_FILE, handler);
			
		//load from group
		handler = new IWidgetPropertyChangeHandler() {			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				loadWidgets(getWidgetModel().getOPIFilePath(), true);
				return false;
			}
		};
		
		setPropertyChangeHandler(LinkingContainerModel.PROP_GROUP_NAME, handler);
		
		
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
	private synchronized void loadWidgets(IPath path, boolean checkSelf) {	
		getWidgetModel().removeAllChildren();
		if(path ==null || path.isEmpty())
			return;
		
		try {
			if(checkSelf && getExecutionMode() == ExecutionMode.EDIT_MODE){
				IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().getActiveEditor();
				if(activeEditor != null){
					IEditorInput input = activeEditor.getEditorInput();			
					if(path.equals(ResourceUtil.getPathInEditor(input))){
						getWidgetModel().getProperty(
								LinkingContainerModel.PROP_OPI_FILE).
								setPropertyValue(new Path(""), false);
						throw new Exception("It is not allowed to link to the OPI file itself.");
					}
				}
			}
			DisplayModel tempDisplayModel = new DisplayModel();
			tempDisplayModel.setOpiFilePath(path);
			XMLUtil.fillDisplayModelFromInputStream(
					ResourceUtil.pathToInputStream(path), tempDisplayModel);
			AbstractContainerModel loadTarget = tempDisplayModel;
			if(!getWidgetModel().getGroupName().trim().equals("")){ //$NON-NLS-1$
				AbstractWidgetModel group = 
					tempDisplayModel.getChildByName(getWidgetModel().getGroupName());
				if(group != null && group instanceof AbstractContainerModel)
					loadTarget = (AbstractContainerModel) group;
			}
			if(tempDisplayModel.getMacrosInput().isInclude_parent_macros())
				tempDisplayModel.setMacroMap(
						(LinkedHashMap<String, String>) tempDisplayModel.getParentMacroMap());
			tempDisplayModel.getMacroMap().putAll(tempDisplayModel.getMacrosInput().getMacrosMap());
			tempDisplayModel.getMacroMap().putAll(getWidgetModel().getMacroMap());	
			for(AbstractWidgetModel child : loadTarget.getChildren()){	
				getWidgetModel().addChild(child, false);
			}
			getWidgetModel().setBackgroundColor(tempDisplayModel.getBackgroundColor());
			tempDisplayModel.removeAllChildren();
		} catch (Exception e) {
			LabelModel loadingErrorLabel = new LabelModel();
			loadingErrorLabel.setLocation(0, 0);
			loadingErrorLabel.setSize(getWidgetModel().getSize().getCopy().shrink(3, 3));
			loadingErrorLabel.setForegroundColor(CustomMediaFactory.COLOR_RED);
			String message = "Failed to load: " + path.toString() + "\n"+ e.getMessage();
			loadingErrorLabel.setText(message);
			loadingErrorLabel.setName("Label");
			getWidgetModel().addChild(loadingErrorLabel);		
			CentralLogger.getInstance().error(this, e);
			ConsoleService.getInstance().writeError(message);
		}
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				((LinkingContainerFigure)getFigure()).updateZoom();				
			}
		});
	}
	
	
	/**
	 * {@inheritDoc} Overidden, to set the selection behaviour of child
	 * EditParts.
	 */
	@Override
	protected final EditPart createChild(final Object model) {
		EditPart result = super.createChild(model);

		// setup selection behavior for the new child
		if (getExecutionMode() == ExecutionMode.EDIT_MODE && 
				result instanceof AbstractBaseEditPart) {
			((AbstractBaseEditPart) result).setSelectable(false);
		}

		return result;
	}
	
	@Override
	public IFigure getContentPane() {
		return ((LinkingContainerFigure)getFigure()).getContentPane();
	}

}
