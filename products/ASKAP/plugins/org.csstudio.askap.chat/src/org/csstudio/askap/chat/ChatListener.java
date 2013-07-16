package org.csstudio.askap.chat;

import java.util.Date;

public interface ChatListener {
	
	public void receive(String from, String text, long timeStamp, boolean isSelf);
	public void addParticipant(String name);
	public void removeParticiparnt(String name);
}
