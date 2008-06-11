package org.csstudio.nams.common;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;

/**
 * Der Standard execution service, verwendet {@link Thread}s.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
class DefaultExecutionService implements ExecutionService {

	public <GT extends Enum<?> & ThreadType> void executeAsynchronsly(
			GT groupId, StepByStepProcessor runnable) {
		// TODO ThreadGroup anlegen!
		new Thread(runnable).start();
	}

	public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds() {
		// TODO Auto-generated method stub
		return null;
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

	

//	private Map<Enum<?>, ThreadGroup> groups = new HashMap<Enum<?>, ThreadGroup>();
//	private Map<Enum<?>, List<Runnable>> runnables = new HashMap<Enum<?>, List<Runnable>>();
//
//	public synchronized <GT extends Enum<?>> void executeAsynchronsly(GT groupId,
//			Runnable runnable) {
//		ThreadGroup group = null;
//
//		synchronized (groups) {
//			group = groups.get(groupId);
//			if( group == null )
//			{
//				String groupIdAsString = groupId.name();
//				group = new ThreadGroup(groupIdAsString);
//				groups.put(groupId, group);
//			}
//			
//			List<Runnable> runs = runnables.get(groupId);
//			if( runs == null )
//			{
//				runs = new ArrayList<Runnable>();
//				runnables.put(groupId, runs);
//			}
//			runs.add(runnable);
//		}
//		
//		new Thread(group, runnable);
//	}
//
//	public Iterable<Enum<?>> getCurrentlyUsedGroupIds() {
//		return groups.keySet();
//	}
//
////	public <GT extends Enum<?>&ThreadType> Iterable<Runnable> getRunnablesOfGroupId(
////			GT groupId) {
////		List<Runnable> list = runnables.get(groupId);
////		List<Runnable> emptyList = Collections.emptyList();
////		return list == null ? emptyList : list;
////	}
//
//	public <GT extends Enum<?>> void executeAsynchronsly(GT groupId,
//			StepByStepProcessor runnable) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public <GT extends Enum<?>> boolean hasGroupRegistered(GT groupId) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	public <GT extends Enum<?>> ThreadGroup getRegisteredGroup(GT groupId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public <GT extends Enum<?>> Iterable<StepByStepProcessor> getRunnablesOfGroupId(
//			GT groupId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public <GT extends Enum<?> & ThreadType> void registerGroup(GT groupId,
//			ThreadGroup group) {
//		// TODO Auto-generated method stub
//		
//	}

}
