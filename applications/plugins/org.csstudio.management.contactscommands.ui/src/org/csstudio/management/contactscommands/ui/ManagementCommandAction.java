/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.management.contactscommands.ui;

import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommandService;
import org.csstudio.remote.management.IResultReceiver;
import org.csstudio.remote.management.ResultDispatcher;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * Action which executes a management command.
 * 
 * @author Joerg Rathlev
 */
public class ManagementCommandAction extends AbstractUserDependentAction {
	
	private static final String AUTHORIZATION_ID = "remoteManagement";
	private static final boolean DEFAULT_PERMISSION = true; // FIXME must be false! true for tests
	
	private final CommandDescription _command;
	private final IManagementCommandService _service;

	/**
	 * Creates a new management command action.
	 * 
	 * @param command
	 *            the command which this action will execute.
	 * @param service
	 *            the service that should be used to execute the command.
	 */
	public ManagementCommandAction(CommandDescription command,
			IManagementCommandService service) {
		super(AUTHORIZATION_ID, DEFAULT_PERMISSION);
		setText(command.getLabel());
		_command = command;
		_service = service;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doWork() {
		final ResultDispatcher dispatcher = new ResultDispatcher(new DefaultResultReceiver());
		dispatcher.addPresetReceiver(CommandResult.TYPE_VOID, new VoidResultReceiver());
		dispatcher.addPresetReceiver(CommandResult.TYPE_ERROR, new ErrorResultReceiver());
		dispatcher.addPresetReceiver(CommandResult.TYPE_ERROR_MESSAGE, new ErrorMessageResultReceiver());
		dispatcher.addPresetReceiver(CommandResult.TYPE_EXCEPTION, new ExceptionResultReceiver());
		dispatcher.addPresetReceiver(CommandResult.TYPE_MESSAGE, new MessageResultReceiver());
		
		final CommandParameters params;
		if (_command.getParameters().length != 0) {
			ManagementCommandParametersDialog dialog =
				new ManagementCommandParametersDialog(null, _command, _service);
			if (Window.OK == dialog.open()) {
				params = dialog.getCommandParameters();
			} else {
				// The user clicked "Cancel". Return without executing the
				// management command.
				return;
			}
		} else {
			params = null;
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					CommandResult result =
						_service.execute(_command.getIdentifier(), params);
					dispatcher.dispatch(result);
				} catch (final Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(null, "Management Command",
									"The command could not be executed. The " +
									"following error occured when trying to " +
									"call the remote service: " + e.toString());
						}
					});
				}
			}
		}).start();
	}
	
	/**
	 * Handles results of type {@link CommandResult#TYPE_VOID}.
	 */
	private static class VoidResultReceiver implements IResultReceiver {
		/**
		 * {@inheritDoc}
		 */
		public void processResult(CommandResult result) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(null,
							"Management Command",
							"Command executed successfully.");
				}
			});
		}
	}
	
	/**
	 * Handles results of type {@link CommandResult#TYPE_MESSAGE}.
	 */
	private static class MessageResultReceiver implements IResultReceiver {
		/**
		 * {@inheritDoc}
		 */
		public void processResult(final CommandResult result) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(null,
							"Management Command",
							(String) result.getValue());
				}
			});
		}
	}
	
	/**
	 * Handles results of type {@link CommandResult#TYPE_ERROR}.
	 */
	private static class ErrorResultReceiver implements IResultReceiver {
		/**
		 * {@inheritDoc}
		 */
		public void processResult(CommandResult result) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null,
							"Management Command",
							"An error occurred while executing the commmand. No " +
							"error message was provided by the command.");
				}
			});
		}
	}
	
	/**
	 * Handles results of type {@link CommandResult#TYPE_ERROR_MESSAGE}.
	 */
	private static class ErrorMessageResultReceiver implements IResultReceiver {
		/**
		 * {@inheritDoc}
		 */
		public void processResult(final CommandResult result) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null,
							"Management Command",
							"The following error occurred while executing the commmand:\r\n"
							+ ((String) result.getValue()));
				}
			});
		}
	}

	/**
	 * Handles results of type {@link CommandResult.TYPE_EXCEPTION}.
	 */
	private static class ExceptionResultReceiver implements IResultReceiver {
		/**
		 * {@inheritDoc}
		 */
		public void processResult(final CommandResult result) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null,
							"Management Command",
							"The following error occurred while executing the commmand:\r\n"
							+ ((Throwable) result.getValue()).toString());
				}
			});
		}
	}

	/**
	 * Handles results that have a type for which no receiver is available.
	 */
	private static class DefaultResultReceiver implements IResultReceiver {
		/**
		 * {@inheritDoc}
		 */
		public void processResult(final CommandResult result) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openInformation(null,
							"Management Command",
							"Command executed successfully. The command returned " +
							"a result but no receiver is available for the result " +
							"type \"" + result.getType() + "\".");
				}
			});
		}
	}
}
