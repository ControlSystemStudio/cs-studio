package org.csstudio.nams.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.nams.common.service.ExecutionService;

/**
 * Der Standard execution service, verwendet {@link Thread}s.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
class DefaultExecutionService implements ExecutionService {

	private Map<Enum<?>, ThreadGroup> groups = new HashMap<Enum<?>, ThreadGroup>();
	private Map<Enum<?>, List<Runnable>> runnables = new HashMap<Enum<?>, List<Runnable>>();

	public synchronized <GT extends Enum<?>> void executeAsynchronsly(GT groupId,
			Runnable runnable) {
		ThreadGroup group = null;

		synchronized (groups) {
			group = groups.get(groupId);
			if( group == null )
			{
				String groupIdAsString = groupId.name();
				group = new ThreadGroup(groupIdAsString);
				groups.put(groupId, group);
			}
			
			List<Runnable> runs = runnables.get(groupId);
			if( runs == null )
			{
				runs = new ArrayList<Runnable>();
				runnables.put(groupId, runs);
			}
			runs.add(runnable);
		}
		
		new Thread(group, runnable);
	}

	public Iterable<Enum<?>> getCurrentlyUsedGroupIds() {
		return groups.keySet();
	}

	public <GT extends Enum<?>> Iterable<Runnable> getRunnablesOfGroupId(
			GT groupId) {
		List<Runnable> list = runnables.get(groupId);
		List<Runnable> emptyList = Collections.emptyList();
		return list == null ? emptyList : list;
	}

}
