package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.SystemNachricht;
import org.junit.Assert;

public class ProducerMock implements Producer {

	public boolean isClosed() {
		Assert.fail("unexpected call of method.");
		return false;
	}

	public void sendeSystemnachricht(final SystemNachricht vorgangsmappe) {
		Assert.fail("unexpected call of method.");
	}

	public void sendeVorgangsmappe(final Vorgangsmappe vorgangsmappe) {
		Assert.fail("unexpected call of method.");
	}

	public void tryToClose() {
		Assert.fail("unexpected call of method.");
	}

}
