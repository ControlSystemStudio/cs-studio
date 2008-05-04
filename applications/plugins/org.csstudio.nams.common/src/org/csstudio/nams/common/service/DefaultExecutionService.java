package org.csstudio.nams.common.service;

import java.util.HashMap;
import java.util.Map;

public class DefaultExecutionService implements ExecutionService {

	private Map<Enum<?>, ThreadGroup> groups = new HashMap<Enum<?>, ThreadGroup>();

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
		}
		
		new Thread(group, runnable);
	}

}
