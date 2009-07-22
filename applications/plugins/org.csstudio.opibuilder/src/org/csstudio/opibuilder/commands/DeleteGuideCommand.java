package org.csstudio.opibuilder.commands;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.model.RulerModel;
import org.eclipse.gef.commands.Command;

/**
 * A Command to delete a guide.
 * @author Xihui Chen
 */
public final class DeleteGuideCommand extends Command {

	/**
	 * The RulerModel.
	 */
	private RulerModel parent;
	/**
	 * The GuideModel.
	 */
	private GuideModel guide;
	/**
	 * A Map of {@link AbstractWidgetModel} and Integers.
	 */
	private Map<AbstractWidgetModel, Integer> _oldParts;
	
	/**
	 * Constructor.
	 * @param guide
	 * 			The GuideModel
	 * @param parent
	 * 			The RulerModel
	 */
	public DeleteGuideCommand(final GuideModel guide, final RulerModel parent) {
		this.guide = guide;
		this.parent = parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		_oldParts = new HashMap<AbstractWidgetModel, Integer>(guide.getMap());
		Iterator<AbstractWidgetModel> iter = _oldParts.keySet().iterator();
		while (iter.hasNext()) {
			guide.detachPart(iter.next());
		}
		parent.removeGuide(guide);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void undo() {
		parent.addGuide(guide);
		Iterator<AbstractWidgetModel> iter = _oldParts.keySet().iterator();
		while (iter.hasNext()) {
			AbstractWidgetModel model = iter.next();
			guide.attachPart(model, ((Integer)_oldParts.get(model)).intValue());
		}
	}
}
