package org.csstudio.utility.chat;

public interface NerdbinListener
{
	public void nerdAlert(String[] nerds);

	public void receive(String from, boolean is_self, String text);
}
