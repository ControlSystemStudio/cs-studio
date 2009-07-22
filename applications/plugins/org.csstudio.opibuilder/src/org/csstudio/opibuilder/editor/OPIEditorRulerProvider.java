package org.csstudio.opibuilder.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.CreateGuideCommand;
import org.csstudio.opibuilder.commands.DeleteGuideCommand;
import org.csstudio.opibuilder.commands.MoveGuideCommand;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.model.RulerModel;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;

/**
 * The RulerProvider for the OPI Editor.
 * @author Xihui Chen
 */
public final class OPIEditorRulerProvider extends RulerProvider {
	
	/**
	 * A PropertyChangeListener for rulers.
	 */
	private PropertyChangeListener rulerListener = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RulerModel.PROPERTY_CHILDREN_CHANGED)) {
				GuideModel guide = (GuideModel)evt.getNewValue();
				if (getGuides().contains(guide)) {
					guide.addPropertyChangeListener(guideListener);
				} else {
					guide.removePropertyChangeListener(guideListener);
				}
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener)listeners.get(i))
							.notifyGuideReparented(guide);
				}
			}
		}
	};
	
	/**
	 * A PropertyChangeListener for guides.
	 */
	private PropertyChangeListener guideListener = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(GuideModel.PROPERTY_CHILDREN_CHANGED)) {
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener)listeners.get(i))
							.notifyPartAttachmentChanged(evt.getNewValue(), evt.getSource());
				}
			} else {
				for (int i = 0; i < listeners.size(); i++) {
					((RulerChangeListener)listeners.get(i))
							.notifyGuideMoved(evt.getSource());
				}
			}
		}
	};
	
	/**
	 * The Model for the rulers.
	 */
	private RulerModel ruler;
	
	/**
	 * Constructor.
	 * @param ruler
	 * 			The RulerModel fore this provider
	 */
	public OPIEditorRulerProvider(final RulerModel ruler) {
		this.ruler = ruler;
		this.ruler.addPropertyChangeListener(rulerListener);
		List<GuideModel> guides = getGuides();
		for (int i = 0; i < guides.size(); i++) {
			((GuideModel)guides.get(i)).addPropertyChangeListener(guideListener);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getRuler() {
		return ruler;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getUnit() {
		return RulerProvider.UNIT_PIXELS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<AbstractWidgetModel> getAttachedModelObjects(final Object guide) {
		return new ArrayList<AbstractWidgetModel>(((GuideModel)guide).getAttachedModels());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Command getMoveGuideCommand(final Object guide, final int pDelta) {
		return new MoveGuideCommand((GuideModel)guide, pDelta);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command getCreateGuideCommand(final int position) {
		return new CreateGuideCommand(ruler, position);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command getDeleteGuideCommand(final Object guide) {
		return new DeleteGuideCommand((GuideModel) guide, ruler);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int[] getGuidePositions() {
		List<GuideModel> guides = getGuides();
		int[] result = new int[guides.size()];
		for (int i = 0; i < guides.size(); i++) {
			result[i] = ((GuideModel)guides.get(i)).getPosition();
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getGuidePosition(final Object guide) {
		return ((GuideModel)guide).getPosition();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<GuideModel> getGuides() {
		return ruler.getGuides();
	}
}
