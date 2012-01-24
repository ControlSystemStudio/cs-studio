package org.csstudio.ams.configReplicator;

public class ReplicationException extends Exception {

	private static final long serialVersionUID = 1596565125049661641L;

	public ReplicationException(Exception causingException) {
		super(causingException);
	}
	
}
