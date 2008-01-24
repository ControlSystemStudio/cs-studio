package org.csstudio.sds.ui.internal.commands;

import org.csstudio.sds.model.StateMemento;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.internal.editor.DropPvRequest;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.progress.UIJob;

/**
 * A command, which changes the settings from a Widget, caused by a Drag and
 * Drop.
 * 
 * @author Sven Wende, Kai Meyer
 */
public final class ChangeSettingsFromDroppedPvCommand extends Command {
	/**
	 * The Abstract EditPart.
	 */
	private AbstractWidgetEditPart _editPart;
	/**
	 * The Request.
	 */
	private DropPvRequest _request;

	/**
	 * The old alias value.
	 */
	private String _oldValue;
	

	private StateMemento _widgetStateMemento;
	
	/**
	 * Constructor.
	 * 
	 * @param request
	 *            The Request
	 * @param editPart
	 *            The EditPart for the Widget
	 */
	public ChangeSettingsFromDroppedPvCommand(final DropPvRequest request,
			final AbstractWidgetEditPart editPart) {
		assert request != null;
		assert editPart != null;
		_request = request;
		_editPart = editPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		_oldValue = _editPart.getWidgetModel().getAliases().get("channel");
		_editPart.getWidgetModel()
				.setAliasValue("channel", _request.getProcessVariableAddress().getFullName());
		
		// remember the state
		_widgetStateMemento =  _editPart.getWidgetModel().getStateMemento();
		
		// connect
		_editPart.getWidgetModel().setLive(true);
		UIJob job = new UIJob("Temporary Connect") {
			@Override
			public IStatus runInUIThread(
					final IProgressMonitor monitor) {
				_editPart.getWidgetModel().setLive(false);
				
				// reset widget state
				_editPart.getWidgetModel().restoreState(_widgetStateMemento);
				
				return Status.OK_STATUS;
			}
		};
		job.schedule(3000);
	}

	@Override
	public void undo() {
		_editPart.getWidgetModel().setLive(false);
		
		if (_oldValue == null) {
			_editPart.getWidgetModel().removeAlias("channel");
		} else {
			_editPart.getWidgetModel().setAliasValue("channel", _oldValue);
		}
		
		// reset widget state
		_editPart.getWidgetModel().restoreState(_widgetStateMemento);
		
	}
}
