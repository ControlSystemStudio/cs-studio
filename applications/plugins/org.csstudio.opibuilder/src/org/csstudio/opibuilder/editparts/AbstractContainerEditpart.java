package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.editpolicies.WidgetXYLayoutEditPolicy;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.rulers.RulerProvider;

public abstract class AbstractContainerEditpart extends AbstractBaseEditPart {	

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, 
				getExecutionMode() == ExecutionMode.EDIT_MODE ? new WidgetXYLayoutEditPolicy() : null);
	
		//the snap feedback effect
		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$
	}
	
	@Override
	public void activate() {
		super.activate();
		((AbstractContainerModel)getModel()).getChildrenProperty().addPropertyChangeListener(
				new PropertyChangeListener() {
					
					public void propertyChange(PropertyChangeEvent evt) {
						UIBundlingThread.getInstance().addRunnable(new Runnable() {
							
							public void run() {
								refreshChildren();
							}
						});
						
					}
				});
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		((AbstractContainerModel)getModel()).getChildrenProperty().removeAllPropertyChangeListeners();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		return ((AbstractContainerModel)getModel()).getChildren();
	}

	@Override
	public AbstractContainerModel getCastedModel() {
		return (AbstractContainerModel)getModel();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		//make snap to G work
		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();
			Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGuides(this));
			val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGeometry(this));
			val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGrid(this));
			
			if (snapStrategies.size() == 0)
				return null;
			if (snapStrategies.size() == 1)
				return snapStrategies.get(0);

			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++)
				ss[i] = (SnapToHelper)snapStrategies.get(i);
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}

}
