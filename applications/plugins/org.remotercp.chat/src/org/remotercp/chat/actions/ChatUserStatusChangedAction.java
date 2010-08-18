package org.remotercp.chat.actions;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.remotercp.chat.ui.ChatEditor;

public class ChatUserStatusChangedAction extends Action {

	private ID fromID;

	private IPresence presence;

	public ChatUserStatusChangedAction(ID fromID, IPresence presence) {
		this.fromID = fromID;
		this.presence = presence;
	}

	public void run() {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (page != null) {

					IEditorPart activeEditor = page.getActiveEditor();

					if (activeEditor != null
							&& activeEditor instanceof ChatEditor) {
						ChatEditor chatEditor = (ChatEditor) activeEditor;

						String presenceText = null;
						if (presence.getType() == IPresence.Type.UNAVAILABLE) {
							presenceText = "LEFT the room";
						}

						if (presence.getType() == IPresence.Type.AVAILABLE) {
							presenceText = "ARRIVED";
						}

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"dd/MM HH:mm");

						final String message = "*** ["
								+ simpleDateFormat.format(new Date()) + "] "
								+ fromID.getName() + " " + presenceText;
						// post the arrive or left message to active editor
						chatEditor.postUserArrivedOrLeftStatus(message);

					}
				}
			}
		});

	}

}
