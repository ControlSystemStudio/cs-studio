package org.csstudio.opibuilder.commands;


import org.csstudio.opibuilder.model.GuideModel;
import org.csstudio.opibuilder.model.RulerModel;
import org.eclipse.gef.commands.Command;

/**
 * A Command to create a Guide.
 * @author Xihui Chen
 *
 */
public final class CreateGuideCommand extends Command {
	
	/**
	 * The position.
	 */
	private int _position;
	/**
	 * The parnet for this guide.
	 */
	private RulerModel _parent;
	/**
	 * The GuideModel, which should be created.
	 */
	private GuideModel _guide;
	
	/**
	 * Constructor.
	 * @param parent
	 * 				The parent for this Guide
	 * @param position
	 * 				The position
	 */
	public CreateGuideCommand(final RulerModel parent, final int position) {
		super();
		_parent = parent;
		_position = position;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_guide = new GuideModel(_position);
		_parent.addGuide(_guide);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		_parent.removeGuide(_guide);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		_parent.addGuide(_guide);
	}

}
