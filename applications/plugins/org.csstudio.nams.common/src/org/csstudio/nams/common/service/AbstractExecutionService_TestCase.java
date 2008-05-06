package org.csstudio.nams.common.service;

import static org.junit.Assert.*;

import org.junit.Test;

public abstract class AbstractExecutionService_TestCase {

	private static enum TestIds implements ThreadType{
		A, B, C;
	}
	
	protected abstract ExecutionService getNewInstance();
	
	@Test
	public void testRegisterGroup() {
		ExecutionService executionService = getNewInstance();
		
		ThreadGroup threadGroupA = new ThreadGroup("A");
		executionService.registerGroup(TestIds.A, threadGroupA);
		
		ThreadGroup threadGroupB = new ThreadGroup("B");
		executionService.registerGroup(TestIds.B, threadGroupB);
		
		assertTrue(executionService.hasGroupRegistered(TestIds.A));
		assertTrue(executionService.hasGroupRegistered(TestIds.B));
		assertFalse(executionService.hasGroupRegistered(TestIds.C));
		
		ThreadGroup threadGroupC = new ThreadGroup("C");
		executionService.registerGroup(TestIds.C, threadGroupC);

		assertTrue(executionService.hasGroupRegistered(TestIds.A));
		assertTrue(executionService.hasGroupRegistered(TestIds.B));
		assertTrue(executionService.hasGroupRegistered(TestIds.C));
		
		ThreadGroup containedGroupA = executionService.getRegisteredGroup(TestIds.A);
		ThreadGroup containedGroupB = executionService.getRegisteredGroup(TestIds.B);
		ThreadGroup containedGroupC = executionService.getRegisteredGroup(TestIds.C);
		
		assertSame(threadGroupA, containedGroupA);
		assertSame(threadGroupB, containedGroupB);
		assertSame(threadGroupC, containedGroupC);
	}

	@Test
	public void testExecuteAsynchronsly() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentlyUsedGroupIds() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRunnablesOfGroupId() {
		fail("Not yet implemented");
	}

}
