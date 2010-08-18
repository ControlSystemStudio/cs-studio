package org.remotercp.chat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class ChatEditorInput implements IEditorInput {

	private ID participantID;

	public ChatEditorInput(ID participantID) {
		super();
		Assert.isNotNull(participantID);
		this.participantID = participantID;
	}

	public boolean exists() {
		return false;
	}

	public String getToolTipText() {
		return participantID.getName();
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return getParticipantID().getName();
	}

	public ID getParticipantID() {
		return participantID;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (!(obj instanceof ChatEditorInput))
			return false;
		ChatEditorInput other = (ChatEditorInput) obj;
		return participantID.equals(other.participantID);
	}

	public int hashCode() {
		return participantID.hashCode();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}
