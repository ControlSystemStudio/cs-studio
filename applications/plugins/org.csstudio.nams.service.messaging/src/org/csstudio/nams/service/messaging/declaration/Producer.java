package org.csstudio.nams.service.messaging.declaration;


public interface Producer {
	public void close();
	public boolean isClosed();
	//@Deprecated //sollte einen eigenen Message Typ geben
	public void sendMessage(NAMSMessage message);
	// TODO Irgendwann: public void sendVorgangsmappe(Vorgangsmappe vorgangsmappe);
}
