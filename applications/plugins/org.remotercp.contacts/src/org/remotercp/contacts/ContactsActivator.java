package org.remotercp.contacts;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.IIMMessageListener;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.IPresenceListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.remotercp.chat.actions.ChatUserStatusChangedAction;
import org.remotercp.chat.actions.OpenChatEditorAction;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class ContactsActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.contacts";

	// The shared instance
	private static ContactsActivator INSTANCE;

	private static BundleContext _bundleContext;

	/**
     * Don't instantiate.
     * Called by framework.
	 */
	public ContactsActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(final BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;

		_bundleContext = context;

		this.registerListener();
	}

	/*
	 * Register listener for incoming chat events. Check if this is the
	 * appropriate place to register a listener. The listener has to be
	 * registered on start up, otherwise the chat editor will never be opened.
	 *
	 * FIXME: I commented out the code below because it is not the appropriate
	 * place to register a listener. If the SessionService is not available yet
	 * by the time this method is called, it would cause the activiation of this
	 * whole plug-in to fail. (jr,20090611)
	 */
	private void registerListener() {
//		ISessionService session = OsgiServiceLocatorUtil.getOSGiService(
//				bundlecontext, ISessionService.class);
//
//		if (session != null) {
//
//			// nachrichten
//			session.getChatManager().addMessageListener(
//					new IIMMessageListener() {
//
//						public void handleMessageEvent(
//								IIMMessageEvent messageEvent) {
//							Logger.getAnonymousLogger().log(
//									Level.INFO,
//									"Message received: "
//											+ messageEvent.getFromID());
//
//							new OpenChatEditorAction(messageEvent).run();
//						}
//					});
//
//			// inform chat user about arriving and leaving of other chat user
//			session.getRosterManager().addPresenceListener(
//					new IPresenceListener() {
//						public void handlePresence(ID fromID, IPresence presence) {
//							new ChatUserStatusChangedAction(fromID, presence)
//									.run();
//
//						}
//					});
//		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(final BundleContext context) throws Exception {
		INSTANCE = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ContactsActivator getDefault() {
		return INSTANCE;
	}

	public static BundleContext getBundleContext() {
		return _bundleContext;
	}

}
