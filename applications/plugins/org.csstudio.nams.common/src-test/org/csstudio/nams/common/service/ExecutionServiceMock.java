package org.csstudio.nams.common.service;


/**
 * Ein Mock-Execution service, das ausführen erfolgt synchron und manuell.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
public class ExecutionServiceMock implements ExecutionService {

//	private Map<Enum<?> & ThreadType, List<Runnable>> allRunnables = new HashMap<Enum<?> & ThreadType, List<Runnable>>();
//
//	public synchronized <GT extends Enum<?>> void executeAsynchronsly(
//			GT groupId, Runnable runnable) {
//		List<Runnable> runables = null;
//		synchronized (allRunnables) {
//			runables = allRunnables.get(groupId);
//			if (runables == null) {
//				runables = new ArrayList<Runnable>();
//				allRunnables.put(groupId, runables);
//			}
//		}
//
//		runables.add(runnable);
//	}
//
//	public synchronized <GT extends Enum<?>> Throwable[] executeGroup(GT groupId) {
//		List<Throwable> errors = new ArrayList<Throwable>();
//
//		Iterable<Runnable> runnables = allRunnables.get(groupId);
//		if (runnables == null) {
//			Assert.fail("Invalied groupId " + groupId.name() + " to execute.");
//		}
//		for (Runnable runnable : runnables) {
//			try {
//				runnable.run();
//			} catch (Throwable t) {
//				errors.add(t);
//			}
//		}
//		return errors.toArray(new Throwable[errors.size()]);
//	}
//
//	public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds() {
//		return allRunnables.keySet();
//	}
//
//	public synchronized Map<Enum<?>, Throwable[]> executeAll() {
//		Set<Enum<?>> groupIds = getCurrentlyUsedGroupIds();
//		Map<Enum<?>, Throwable[]> errors = new HashMap<Enum<?>, Throwable[]>();
//
//		for (Enum<?> group : groupIds) {
//			Throwable[] errorsInGroups = executeGroup(group);
//			errors.put(group, errorsInGroups);
//		}
//
//		return errors;
//	}

	public <GT extends Enum<?> & ThreadType> void executeAsynchronsly(
			GT groupId, StepByStepProcessor runnable) {
		// TODO Auto-generated method stub
		
	}

	public <GT extends Enum<?> & ThreadType> ThreadGroup getRegisteredGroup(
			GT groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public <GT extends Enum<?> & ThreadType> Iterable<StepByStepProcessor> getRunnablesOfGroupId(
			GT groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public <GT extends Enum<?> & ThreadType> boolean hasGroupRegistered(
			GT groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	public <GT extends Enum<?> & ThreadType> void registerGroup(GT groupId,
			ThreadGroup group) {
		// TODO Auto-generated method stub
		
	}

	public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds() {
		// TODO Auto-generated method stub
		return null;
	}

	

	// public <GT extends Enum<?>> Iterable<Runnable> getRunnablesOfGroupId(
	// GT groupId) {
	// List<Runnable> list = allRunnables.get(groupId);
	// List<Runnable> emptyList = Collections.emptyList();
	// return list == null ? emptyList : list;
	// }
}
