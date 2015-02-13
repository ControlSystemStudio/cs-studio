package org.csstudio.askap.chat;

import java.util.Collection;

public interface ChatMessageHandler {
	
	public void changeUserName(String newName);
	public void startChat() throws Exception;
	public void stopChat() throws Exception;
	public void sendChatMessage(String message) throws Exception;
	public Collection<String> getParticipants() throws Exception;

}
