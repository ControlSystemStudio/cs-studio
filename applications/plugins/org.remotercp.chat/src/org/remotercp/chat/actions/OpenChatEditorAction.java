package org.remotercp.chat.actions;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.presence.IIMMessageEvent;
import org.eclipse.ecf.presence.im.ChatMessageEvent;
import org.eclipse.ecf.presence.im.IChatMessage;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.remotercp.chat.ChatEditorInput;
import org.remotercp.chat.ui.ChatEditor;

/**
 * This action will open an Editor for a user to chat.
 * 
 * @author eugrei
 * 
 */
public class OpenChatEditorAction extends Action {

	private IIMMessageEvent messageEvent;

	public OpenChatEditorAction(IIMMessageEvent messageEvent) {
		this.messageEvent = messageEvent;
	}

	@Override
	public void run() {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (page != null && messageEvent instanceof ChatMessageEvent) {
					final IChatMessage message = ((ChatMessageEvent) messageEvent)
							.getChatMessage();

					ID fromID = messageEvent.getFromID();
					ChatEditorInput editorInput = new ChatEditorInput(fromID);
					try {
						IEditorPart editor = page.openEditor(editorInput,
								ChatEditor.ID);
						if (editor instanceof ChatEditor) {
							ChatEditor chatEditor = (ChatEditor) editor;

							chatEditor.processFirstMessage(message);
						}
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}
}
