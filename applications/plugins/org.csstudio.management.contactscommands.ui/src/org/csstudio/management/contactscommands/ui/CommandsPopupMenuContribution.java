package org.csstudio.management.contactscommands.ui;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.IManagementCommandService;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class CommandsPopupMenuContribution extends ExtensionContributionFactory
		implements IGenericServiceListener<ISessionService> {

	private ISessionService sessionService;

	public CommandsPopupMenuContribution() {
		Activator.getDefault().addSessionServiceListener(this);

	}

	@Override
	public void createContributionItems(IServiceLocator serviceLocator,
			IContributionRoot additions) {

		System.out.println("------------ create contribution items");
		ID userId = getSelectedUserId(serviceLocator);

		// 1. add dummy context menu
//		 addDummyContextItem(serviceLocator, additions);

		// 2. add real context menu
		addContextItems(serviceLocator, additions, userId);

	}

	private ID getSelectedUserId(IServiceLocator serviceLocator) {
		ISelectionService service = (ISelectionService) serviceLocator
				.getService(ISelectionService.class);
		IStructuredSelection selection = (IStructuredSelection) service
				.getSelection();

		IRosterEntry rosterEntry = (IRosterEntry) selection.getFirstElement();
		IUser user = rosterEntry.getUser();
		ID userId = user.getID();
		return userId;
	}

	private void addContextItems(IServiceLocator serviceLocator,
			IContributionRoot additions, ID userId) {
		System.out
				.println("CommandsPopupMenuContribution.createContributionItems()");
		if (this.sessionService != null) {
			try {
				IManagementCommandService managementCommandService = this.sessionService
						.getRemoteServiceForClient(
								IManagementCommandService.class, userId, null);

				if (managementCommandService != null) {
					createContextItems(serviceLocator, additions,
							managementCommandService);
				}

			} catch (ECFException e) {
				e.printStackTrace();
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}

		}
	}

	private void createContextItems(IServiceLocator serviceLocator,
			IContributionRoot additions,
			IManagementCommandService managementCommandService) {
		CommandDescription[] supportedCommands = managementCommandService
				.getSupportedCommands();

//		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("service", managementCommandService);

		for (CommandDescription commandDescription : supportedCommands) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("org.csstudio.management.contactscommands.ui.commandParameter1", commandDescription.toString());

			CommandContributionItemParameter param = new CommandContributionItemParameter(
					serviceLocator, null, ContactsCommandHandler.COMMAND_ID, SWT.PUSH);

			param.label = commandDescription.getLabel();
			param.parameters = parameters;
			CommandContributionItem item = new CommandContributionItem(param);
			item.setVisible(true);

			additions.addContributionItem(item, null);
		}
	}

	 private void addDummyContextItem(IServiceLocator serviceLocator,
	 IContributionRoot additions) {
	 CommandContributionItemParameter param = new
	 CommandContributionItemParameter(
	 serviceLocator, null, ContactsCommandHandler.COMMAND_ID, SWT.PUSH);
	
	 param.label = "Dummy context contribution";
	
	 CommandContributionItem item = new CommandContributionItem(param);
	 item.setVisible(true);
	
	 additions.addContributionItem(item, null);
	 }

	@Override
	public void bindService(ISessionService service) {
		this.sessionService = service;

	}

	@Override
	public void unbindService(ISessionService service) {
		this.sessionService = null;
	}

}
