/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractContainerEditpart;
import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.GeometryUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.csstudio.swt.widgets.figures.LinkingContainerFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IActionFilter;
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
	public LinkingContainerModel getWidgetModel() {
		return (LinkingContainerModel)getModel();
	}
	


	@Override
	protected void registerPropertyChangeHandlers() {
		
		loadWidgets(getWidgetModel().getOPIFilePath(), false);

		
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

		handler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				if((Boolean)newValue)
					performAutosize();
				return false;
			}
		};
		setPropertyChangeHandler(LinkingContainerModel.PROP_AUTO_SIZE, handler);
		


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
			final DisplayModel tempDisplayModel = new DisplayModel();
			tempDisplayModel.setOpiFilePath(path);
			tempDisplayModel.setViewer((GraphicalViewer) getViewer());
			tempDisplayModel.setDisplayID(getWidgetModel().getRootDisplayModel().getDisplayID());
			//This need to be executed after GUI created.
			UIBundlingThread.getInstance().addRunnable(new Runnable() {				
				@Override
				public void run() {
					tempDisplayModel.setExecutionMode(getExecutionMode());
					tempDisplayModel.setOpiRuntime(getWidgetModel().getRootDisplayModel().getOpiRuntime());					
				}
			});

			XMLUtil.fillDisplayModelFromInputStream(
					ResourceUtil.pathToInputStream(path), tempDisplayModel,
					getViewer().getControl().getDisplay());
			AbstractContainerModel loadTarget = tempDisplayModel;

			if(!getWidgetModel().getGroupName().trim().equals("")){ //$NON-NLS-1$
				AbstractWidgetModel group =
					tempDisplayModel.getChildByName(getWidgetModel().getGroupName());
				if(group != null && group instanceof AbstractContainerModel){
					loadTarget = (AbstractContainerModel) group;
				}
			}
			//Load system macro
			if(loadTarget.getMacrosInput().isInclude_parent_macros()){				
				loadTarget.getMacroMap().putAll(
						(LinkedHashMap<String, String>) tempDisplayModel.getParentMacroMap());
			}
			//Load macro from its macrosInput
			loadTarget.getMacroMap().putAll(loadTarget.getMacrosInput().getMacrosMap());
			//It also include the macros on this linking container 
			//which includes the macros from action and global macros if included
			//It will replace the old one too.
			loadTarget.getMacroMap().putAll(getWidgetModel().getMacroMap());
			
			for(AbstractWidgetModel child : loadTarget.getChildren()){
				getWidgetModel().addChild(child, false); //don't change model's parent.
			}
			getWidgetModel().setBackgroundColor(loadTarget.getBackgroundColor());
			getWidgetModel().setDisplayModel(tempDisplayModel);		
			//Add scripts on display model
			if(getExecutionMode() == ExecutionMode.RUN_MODE)
				getWidgetModel().getScriptsInput().getScriptList().addAll(
					loadTarget.getScriptsInput().getScriptList());
			//tempDisplayModel.removeAllChildren();
		} catch (Exception e) {
			LabelModel loadingErrorLabel = new LabelModel();
			loadingErrorLabel.setLocation(0, 0);
			loadingErrorLabel.setSize(getWidgetModel().getSize().getCopy().shrink(3, 3));
			loadingErrorLabel.setForegroundColor(CustomMediaFactory.COLOR_RED);
			String message = "Failed to load: " + path.toString() + "\n"+ e.getMessage();
			loadingErrorLabel.setText(message);
			loadingErrorLabel.setName("Label");
			getWidgetModel().addChild(loadingErrorLabel);
			Activator.getLogger().log(Level.WARNING, message , e);
			ConsoleService.getInstance().writeError(message);
		}
		if(getWidgetModel().isAutoSize()){
			performAutosize();
		}
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				layout();
				if(//getExecutionMode() == ExecutionMode.RUN_MODE && 
						!getWidgetModel().isAutoFit() && !getWidgetModel().isAutoSize()){					
					Rectangle childrenRange = GeometryUtil.getChildrenRange(LinkingContainerEditpart.this);
					getWidgetModel().setChildrenGeoSize(new Dimension(
						childrenRange.width + childrenRange.x + figure.getInsets().left + figure.getInsets().right-1,
						childrenRange.height +childrenRange.y+ figure.getInsets().top + figure.getInsets().bottom-1));
					getWidgetModel().scaleChildren();
				}
				((LinkingContainerFigure)getFigure()).setZoomToFitAll(getWidgetModel().isAutoFit());
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

	@Override
	public void layout() {
		AbstractLayoutEditpart layoutter = getLayoutWidget();
		if(layoutter != null && layoutter.getWidgetModel().isEnabled()){
			List<AbstractWidgetModel> modelChildren = new ArrayList<AbstractWidgetModel>();
			for(Object child : getChildren()){
				if(child instanceof AbstractBaseEditPart &&
						!(child instanceof AbstractLayoutEditpart)){
					modelChildren.add(((AbstractBaseEditPart)child).getWidgetModel());
				}
			}
			layoutter.layout(modelChildren, getFigure().getClientArea());
		}
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IActionFilter.class)
			return new BaseEditPartActionFilter(){
			@Override
			public boolean testAttribute(Object target, String name,
					String value) {
				if (name.equals("allowAutoSize") && value.equals("TRUE")) //$NON-NLS-1$ //$NON-NLS-2$	
					return getExecutionMode()==ExecutionMode.EDIT_MODE;				
				return super.testAttribute(target, name, value);
			}
		};
		return super.getAdapter(adapter);
	}

}
