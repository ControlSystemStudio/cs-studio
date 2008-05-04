package org.csstudio.nams.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

public class ExecutionServiceMock implements ExecutionService {

	private Map<Enum<?>, List<Runnable>> groups = new HashMap<Enum<?>, List<Runnable>>();

	public synchronized <GT extends Enum<?>> void executeAsynchronsly(
			GT groupId, Runnable runnable) {
		List<Runnable> runables = null;
		synchronized (groups) {
			runables = groups.get(groupId);
			if (runables == null) {
				runables = new ArrayList<Runnable>();
				groups.put(groupId, runables);
			}
		}

		runables.add(runnable);
	}

	public synchronized <GT extends Enum<?>> Throwable[] executeGroup(GT groupId) {
		List<Throwable> errors = new ArrayList<Throwable>();

		Iterable<Runnable> runnables = groups.get(groupId);
		if (runnables == null) {
			Assert.fail("Invalied groupId " + groupId.name() + " to execute.");
		}
		for (Runnable runnable : runnables) {
			try {
				runnable.run();
			} catch (Throwable t) {
				errors.add(t);
			}
		}
		return errors.toArray(new Throwable[errors.size()]);
	}

	public synchronized Set<Enum<?>> getKnownGroupIds() {
		return groups.keySet();
	}

	public synchronized Map<Enum<?>, Throwable[]> executeAll() {
		Set<Enum<?>> groupIds = getKnownGroupIds();
		Map<Enum<?>, Throwable[]> errors = new HashMap<Enum<?>, Throwable[]>();

		for (Enum<?> group : groupIds) {
			Throwable[] errorsInGroups = executeGroup(group);
			errors.put(group, errorsInGroups);
		}

		return errors;
	}
}
