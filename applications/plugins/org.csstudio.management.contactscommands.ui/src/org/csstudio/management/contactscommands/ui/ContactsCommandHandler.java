package org.csstudio.management.contactscommands.ui;

import java.util.Map;

import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.IManagementCommandService;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class ContactsCommandHandler implements IHandler,
		IGenericServiceListener<ISessionService> {

	public final static String COMMAND_ID = "org.csstudio.management.contactscommands.ui.remote";

	private ISessionService sessionService;

	public ContactsCommandHandler() {
		Activator.getDefault().addSessionServiceListener(this);
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("ContactsCommandHandler.execute()");
		
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		IRosterEntry rosterEntry = (IRosterEntry) selection.getFirstElement();

		ID userId = rosterEntry.getUser().getID();
		
		IManagementCommandService managementCommandService = null;
		if (this.sessionService != null) {
			try {
				managementCommandService = this.sessionService
				.getRemoteServiceForClient(
						IManagementCommandService.class, userId, null);
			} catch (ECFException e) {
				e.printStackTrace();
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}

		
		Map parameters = event.getParameters();
		String commandName = (String) parameters
				.get("org.csstudio.management.contactscommands.ui.commandParameter1");

		CommandDescription[] supportedCommands = managementCommandService.getSupportedCommands();
		
		CommandDescription selectedCommand = null;
		
		for (CommandDescription command : supportedCommands) {
			if (command.getLabel().equals(commandName)) {
				selectedCommand = command;
				break;
			}
		}
		ManagementCommandAction a = new ManagementCommandAction(selectedCommand, managementCommandService);
		a.run();
//		service.execute(commandDescription.getIdentifier(),
//				commandDescription.getParameters());

		System.out.println(selectedCommand.getLabel());
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindService(ISessionService service) {
		sessionService = service;

	}

	@Override
	public void unbindService(ISessionService service) {
		sessionService = null;
	}

}
