package org.remotercp.chat.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.im.ChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.ecf.presence.im.IChatMessageSender;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.remotercp.chat.ChatActivator;
import org.remotercp.chat.ChatEditorInput;
import org.remotercp.ecf.session.ISessionService;

public class ChatEditor extends EditorPart {

	public static final String ID = "org.eclipsercp.hyperbola.contacts.ui.chateditor";

	private Text textAusgabe;

	private Text textEingabe;

	private ISessionService session;

	private static final Logger logger = Logger.getLogger(ChatEditor.class
			.getName());

	private IIMMessageListener messageListener;

	public ChatEditor() {
		// nothing to do yet
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// nothing to do yet

	}

	@Override
	public void doSaveAs() {
		// nothing to do yet

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);

		if (!(input instanceof ChatEditorInput))
			throw new PartInitException(
					"ChatEditor.init expects a ChatEditorInput.  Actual input: "
							+ input);
		setInput(input);
		setPartName(getChatParticipant().getName());

		// register Listener for incoming chat messages
		this.messageListener = new IIMMessageListener() {
			public void handleMessageEvent(IIMMessageEvent messageEvent) {
				if (messageEvent instanceof ChatMessageEvent) {

				}

				logger.log(Level.INFO, "chat message received : "
						+ messageEvent.getFromID());

			}
		};

		this.session = ChatActivator.getDefault().getService(
				ISessionService.class);
		assert (session != null);

		this.session.getChatManager().addMessageListener(this.messageListener);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout());

		{
			this.textAusgabe = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					this.textAusgabe);
			this.textAusgabe.setEditable(false);
			this.textAusgabe.setBackground(this.textAusgabe.getDisplay()
					.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			this.textAusgabe.setForeground(this.textAusgabe.getDisplay()
					.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		}

		{
			this.textEingabe = new Text(main, SWT.BORDER | SWT.WRAP);
			GridDataFactory.fillDefaults().grab(true, false).hint(
					this.textEingabe.getSize().x,
					this.textEingabe.getLineHeight() * 2).applyTo(
					this.textEingabe);

			this.textEingabe.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.character == SWT.CR) {
						sendMessage();

						// stop event continue performing
						e.doit = false;
					}
				}
			});
		}
	}

	@Override
	public void setFocus() {
		if (this.textEingabe != null && !this.textEingabe.isDisposed()) {
			this.textEingabe.setFocus();
		}
	}

	private void sendMessage() {
		String text = this.textEingabe.getText();
		if (text.length() == 0) {
			return;
		}

		try {
			ID toID = this.getChatParticipant();
			IChatMessageSender chatMessageSender = this.session
					.getChatManager().getChatMessageSender();

			// IChat chat = Session.getInstance().getChatManager().createChat(
			// toID, this.messageListener);
			chatMessageSender.sendChatMessage(toID, text);

			// Map<String, String> props = new HashMap<String, String>();
			//
			// IBundleExplorerService service = ChatActivator.getDefault()
			// .getService(IBundleExplorerService.class);
			//
			// // Bundle[] bundles = service.getApplicationBundles();
			// List<IFeature> installedFeatures =
			// service.getInstalledFeatures();
			// for (IFeature feature : installedFeatures) {
			// // XStream xstream = new XStream();
			// // String xml = xstream.toXML(feature);
			// String identifier = feature.getVersionedIdentifier()
			// .getIdentifier();
			// String version = feature.getVersionedIdentifier().getVersion()
			// .toString();
			//
			// props.put(identifier, version);
			//
			// }
			// // TODO: send a system message with bundle info
			// chatMessageSender.sendChatMessage(toID, null,
			//					IChatMessage.Type.SYSTEM, null, null, props);

			// chat.sendChatMessage(text);

		} catch (ECFException e) {
			e.printStackTrace();
		}

		// try {
		// // remote Weiterleitung der Nachricht
		// getChat().sendMessage(text);
		// } catch (XMPPException e) {
		// e.printStackTrace();
		// }

		// locale Ausgabe im Editor
		String from = this.session.getConnectionDetails().getUserName();
		this.textAusgabe.append("<" + from + ">" + text);
		this.textAusgabe.append("\n");
		scrollToEnd();
		this.textEingabe.setText("");

	}

	// private Chat getChat() {
	// if (chat == null)
	// chat = Session.getInstance().getChat(getChatParticipant(), true);
	// return chat;
	// }

	private void scrollToEnd() {
		int n = this.textAusgabe.getCharCount();
		this.textAusgabe.setSelection(n, n);
		this.textAusgabe.showSelection();
	}

	private ID getChatParticipant() {
		ChatEditorInput editorInput = (ChatEditorInput) getEditorInput();
		return editorInput.getParticipantID();
	}

	private void processMessage(final IChatMessage message) {
		if (textAusgabe.isDisposed())
			return;
		textAusgabe.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (textAusgabe.isDisposed())
					return;
				textAusgabe.append("<" + message.getFromID().getName() + ">"
						+ message.getBody());
				textAusgabe.append("\n");
				scrollToEnd();
			}
		});
	}

	public void postUserArrivedOrLeftStatus(String message) {
		this.textAusgabe.append(message);
		this.scrollToEnd();
	}

	/**
	 * Processes the first message, which triggered the opening of this chat
	 * editor.
	 * 
	 * @param message
	 *            the message
	 */
	public void processFirstMessage(IChatMessage message) {
		// if (message.getType() == IChatMessage.Type.SYSTEM) {
		// Map properties = message.getProperties();
		// // Object object = properties.get("bundlecontext");
		//		}

		// XStream xstream = new XStream();
		if (message.getProperties() != null) {
			// try to read feature list
			for (Object key : message.getProperties().keySet()) {
				String featureIdentifier = (String) key;
				String featureVersion = (String) message.getProperties().get(
						key);
				logger.log(Level.INFO, "Feature: " + featureIdentifier
						+ "  -- version: " + featureVersion);
				// IFeature feature = (IFeature) xstream.fromXML((String)
				// message
				// .getProperties().get(key));
			}
		}
		processMessage(message);
	}

	@Override
	public void dispose() {

	}
}
