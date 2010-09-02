package org.remotercp.login.ui;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.ImageKeys;
import org.remotercp.login.LoginActivator;
import org.remotercp.login.connection.HeadlessConnection;

public class ChatLoginWizard extends Wizard {

	private ChatLoginPage loginPage;

	private final Logger logger = Logger.getLogger(ChatLoginWizard.class
			.getSimpleName());

	private DialogSettings dialogSettings = new DialogSettings("userLogin");

	private static final String USERNAME = "username";

	private static final String PASSWORD = "password";

	private static final String SERVER = "server";

	private static final String LAST_SELECTED_SERVER = "lastElement";

	public ChatLoginWizard() {
		loginPage = new ChatLoginPage();
		addPage(loginPage);
		setDialogSettings(dialogSettings);
		setDefaultPageImageDescriptor(LoginActivator
				.getImageDescriptor(ImageKeys.USER_LOGIN));
	}

	@Override
	public boolean performFinish() {

		String userName = getDialogSettings().get(USERNAME);
		String password = getDialogSettings().get(PASSWORD);
		String server = getDialogSettings().get(SERVER);

		// store user settings in preferences
		IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(LoginActivator.PLUGIN_ID);
		preferences.put(LAST_SELECTED_SERVER, server);
		preferences.put(USERNAME, userName);
		Preferences node = preferences.node(SERVER);

		/*
		 * To store the data in this way makes it possible to add several
		 * servers in the node, but only one server with the same name
		 */
		node.put(server, "");
		try {
			node.flush();
		} catch (BackingStoreException ex) {
			ex.printStackTrace();
		}

		try {
			/*
			 * Establish the server connection
			 */
			HeadlessConnection.connect(userName, password, server,
					ECFConstants.XMPP);
		} catch (IDCreateException e) {
			logger.log(Level.SEVERE, "Uable to initiate the target ID", e);
			e.printStackTrace();
			return false;
		} catch (ContainerCreateException e) {
			loginPage
					.setErrorMessage("The provided container type is not supported yet.");
			e.printStackTrace();
			return false;
		} catch (ContainerConnectException e) {
			loginPage.setErrorMessage(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			loginPage.setErrorMessage("The entered server can't be found");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private final class ChatLoginPage extends WizardPage {

		private Text username;

		private Text password;

		private Combo server;

		protected ChatLoginPage() {
			super("Login");
			setTitle("Remote RCP Login-Dialog");
			setDescription("Please enter your user name and password");
			setPageComplete(false);

		}

		public void createControl(Composite parent) {
			Composite main = new Composite(parent, SWT.None);
			main.setLayout(new GridLayout(2, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

			{
				// user name
				new Label(main, SWT.READ_ONLY).setText("user name");
				username = new Text(main, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						username);

				username.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event event) {
						getDialogSettings().put(USERNAME, username.getText());
						checkPageComplete();
					}
				});

				// password
				new Label(main, SWT.READ_ONLY).setText("password");
				password = new Text(main, SWT.BORDER | SWT.PASSWORD);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(
						password);

				password.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event event) {
						getDialogSettings().put(PASSWORD, password.getText());
						checkPageComplete();
					}
				});

				// server
				new Label(main, SWT.READ_ONLY).setText("server");
				server = new Combo(main, SWT.BORDER);
				// server.setText("127.0.0.1");
				getDialogSettings().put(SERVER, server.getText());
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(server);
				server.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event event) {
						getDialogSettings().put(SERVER, server.getText());
						checkPageComplete();
					}
				});

			}

			setControl(main);
			initValues();
		}

		/*
		 * Try to load values from preferences
		 */
		private void initValues() {
			IEclipsePreferences preferences = new ConfigurationScope()
					.getNode(LoginActivator.PLUGIN_ID);

			String userName = preferences.get(USERNAME, "");
			username.setText(userName);

			String lastSelectedServer = preferences.get(LAST_SELECTED_SERVER,
					"");
			Preferences node = preferences.node(SERVER);

			try {
				String[] keys = node.keys();
				for (int key = 0; key < keys.length; key++) {
					server.add(keys[key]);

					// determin combo selection
					if (keys[key].equals(lastSelectedServer)) {
						server.select(key);
					}
				}

			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Checks, whether all necessary data has been entered
		 */
		private void checkPageComplete() {
			boolean userNameEntered = false;
			boolean passwordEntered = false;
			boolean serverEntered = false;
			if (getDialogSettings().get(USERNAME) != null
					&& !getDialogSettings().get(USERNAME).equals("")) {
				userNameEntered = true;
			}
			if (getDialogSettings().get(PASSWORD) != null
					&& !getDialogSettings().get(PASSWORD).equals("")) {
				passwordEntered = true;
			}

			if (getDialogSettings().get(SERVER) != null
					&& !getDialogSettings().get(SERVER).equals("")) {
				serverEntered = true;
			}

			if (userNameEntered && passwordEntered && serverEntered) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		}
	}

}
