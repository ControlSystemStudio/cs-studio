package org.csstudio.nams.common.service;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractExecutionService_TestCase {

	private static enum TestIds implements ThreadType {
		A, B, C;
	}

	@Test
	public void testExecuteAsynchronsly() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentlyUsedGroupIds() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testGetRunnablesOfGroupId() {
		Assert.fail("Not yet implemented");
	}

	@Test
	public void testRegisterGroup() {
		final ExecutionService executionService = this.getNewInstance();

		final ThreadGroup threadGroupA = new ThreadGroup("A");
		executionService.registerGroup(TestIds.A, threadGroupA);

		final ThreadGroup threadGroupB = new ThreadGroup("B");
		executionService.registerGroup(TestIds.B, threadGroupB);

		Assert.assertTrue(executionService.hasGroupRegistered(TestIds.A));
		Assert.assertTrue(executionService.hasGroupRegistered(TestIds.B));
		Assert.assertFalse(executionService.hasGroupRegistered(TestIds.C));

		final ThreadGroup threadGroupC = new ThreadGroup("C");
		executionService.registerGroup(TestIds.C, threadGroupC);

		Assert.assertTrue(executionService.hasGroupRegistered(TestIds.A));
		Assert.assertTrue(executionService.hasGroupRegistered(TestIds.B));
		Assert.assertTrue(executionService.hasGroupRegistered(TestIds.C));

		final ThreadGroup containedGroupA = executionService
				.getRegisteredGroup(TestIds.A);
		final ThreadGroup containedGroupB = executionService
				.getRegisteredGroup(TestIds.B);
		final ThreadGroup containedGroupC = executionService
				.getRegisteredGroup(TestIds.C);

		Assert.assertSame(threadGroupA, containedGroupA);
		Assert.assertSame(threadGroupB, containedGroupB);
		Assert.assertSame(threadGroupC, containedGroupC);
	}

	protected abstract ExecutionService getNewInstance();

}
