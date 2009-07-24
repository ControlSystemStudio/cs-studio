package org.csstudio.opibuilder.commands;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.util.GuideUtil;
import org.eclipse.gef.commands.Command;

/**
 * A command to change a Guide.
 * @author Kai Meyer
 */
public final class ChangeGuideCommand extends Command {

	/**
	 * The AbstzractWidgetModel.
	 */
	private AbstractWidgetModel _model;
	/**
	 * The old guide.
	 */
	private GuideModel _oldGuide;
	/**
	 * The new guide.
	 */
	private GuideModel _newGuide;
	/**
	 * The old alignment.
	 */
	private int _oldAlign;
	/**
	 * The new alignment.
	 */
	private int _newAlign;
	/**
	 * The orientation of the guide.
	 */
	private boolean _horizontal;
	
	/**
	 * Constructor.
	 * @param model
	 * 			The AbstractWidgetModel
	 * @param horizontalGuide
	 * 			The horizontal guide
	 */
	public ChangeGuideCommand(final AbstractWidgetModel model, final boolean horizontalGuide) {
		super();
		_model = model;
		_horizontal = horizontalGuide;
	}
	
	/**
	 * Changes the guide.
	 * @param newGuide
	 * 			The new guide
	 * @param newAlignment
	 * 			The new alignment
	 */
	protected void changeGuide(final GuideModel newGuide, final int newAlignment) {
		// You need to re-attach the part even if the oldGuide and the newGuide are the same
		// because the alignment could have changed
		if (newGuide != null) {
			newGuide.attachPart(_model, newAlignment);
		}
	}
		
	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		// Cache the old values
		_oldGuide = GuideUtil.getInstance().getGuide(_model, _horizontal);		
		if (_oldGuide != null) {
			_oldAlign = _oldGuide.getAlignment(_model);
		}
		redo();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void redo() {
		changeGuide(_newGuide, _newAlign);
	}
	
	/**
	 * Sets the new guide.
	 * @param guide
	 * 			The new guide
	 * @param alignment
	 * 			The new alignment
	 */
	public void setNewGuide(final GuideModel guide, final int alignment) {
		_newGuide = guide;
		_newAlign = alignment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void undo() {
		changeGuide(_oldGuide, _oldAlign);
	}

}
