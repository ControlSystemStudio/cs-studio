package org.csstudio.nams.service.messaging.declaration;

import junit.framework.Assert;

import org.csstudio.nams.service.messaging.exceptions.MessagingException;

public class ConsumerMock implements Consumer {

	private boolean isClosed;
	private boolean throwInterruptedOnNextCall = false;
	private NAMSMessage nextToBeDeliviered = null;

	public void close() {
		this.isClosed = true;
	}

	public boolean isClosed() {
		return this.isClosed;
	}

	public void mockSetNextToBeDelivered(final NAMSMessage message) {
		this.nextToBeDeliviered = message;
		this.throwInterruptedOnNextCall = false;
	}

	public void mockThrowInterruptedExceptionOnNextCall() {
		this.throwInterruptedOnNextCall = true;
		this.nextToBeDeliviered = null;
	}

	public NAMSMessage receiveMessage() throws MessagingException,
			InterruptedException {
		Assert.assertFalse(this.isClosed());

		if (this.throwInterruptedOnNextCall) {
			throw new InterruptedException("requested by test");
		}

		Assert.assertNotNull("Eine Nachricht steht zu Auslieferung bereit.",
				this.nextToBeDeliviered);
		return this.nextToBeDeliviered;
	}

}
