package org.csstudio.nams.service.messaging.declaration;

import junit.framework.Assert;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class ConsumerMock implements Consumer {

	private boolean isClosed;
	private boolean throwInterruptedOnNextCall = false;
	private NAMSMessage nextToBeDeliviered = null;

	public void mockSetNextToBeDelivered(NAMSMessage message) {
		nextToBeDeliviered = message;
		throwInterruptedOnNextCall = false;
	}
	
	public void mockThrowInterruptedExceptionOnNextCall() {
		throwInterruptedOnNextCall = true;
		nextToBeDeliviered = null;
	}
	
	public void close() {
		isClosed = true;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public NAMSMessage receiveMessage() throws MessagingException,
			InterruptedException {
		Assert.assertFalse(isClosed());

		if( throwInterruptedOnNextCall ) {
			throw new InterruptedException("requested by test");
		}
		
		Assert.assertNotNull("Eine Nachricht steht zu Auslieferung bereit.", nextToBeDeliviered);
		return nextToBeDeliviered;
	}

}
