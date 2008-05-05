package org.csstudio.nams.common.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;

/**
 * Ein Mock-Execution service, das ausführen erfolgt synchron und manuell.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
public class ExecutionServiceMock implements ExecutionService {

	private Map<Enum<?>, List<Runnable>> allRunnables = new HashMap<Enum<?>, List<Runnable>>();

	public synchronized <GT extends Enum<?>> void executeAsynchronsly(
			GT groupId, Runnable runnable) {
		List<Runnable> runables = null;
		synchronized (allRunnables) {
			runables = allRunnables.get(groupId);
			if (runables == null) {
				runables = new ArrayList<Runnable>();
				allRunnables.put(groupId, runables);
			}
		}

		runables.add(runnable);
	}

	public synchronized <GT extends Enum<?>> Throwable[] executeGroup(GT groupId) {
		List<Throwable> errors = new ArrayList<Throwable>();

		Iterable<Runnable> runnables = allRunnables.get(groupId);
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

	public synchronized Set<Enum<?>> getCurrentlyUsedGroupIds() {
		return allRunnables.keySet();
	}

	public synchronized Map<Enum<?>, Throwable[]> executeAll() {
		Set<Enum<?>> groupIds = getCurrentlyUsedGroupIds();
		Map<Enum<?>, Throwable[]> errors = new HashMap<Enum<?>, Throwable[]>();

		for (Enum<?> group : groupIds) {
			Throwable[] errorsInGroups = executeGroup(group);
			errors.put(group, errorsInGroups);
		}

		return errors;
	}

	public <GT extends Enum<?>> Iterable<Runnable> getRunnablesOfGroupId(
			GT groupId) {
		List<Runnable> list = allRunnables.get(groupId);
		List<Runnable> emptyList = Collections.emptyList();
		return list == null ? emptyList : list;
	}
}
