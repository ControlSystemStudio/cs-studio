
package org.csstudio.nams.common;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;

/**
 * Der Standard execution service, verwendet {@link Thread}s.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is incomplete on handling groups.
 */
public class DefaultExecutionService implements ExecutionService {

	@Override
    public <GT extends Enum<?> & ThreadType> void executeAsynchronsly(
			final GT groupId, final StepByStepProcessor runnable) {
		// TODO ThreadGroup anlegen!
		new Thread(runnable, groupId.name()).start();
	}

	@Override
    public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds() {
		// TODO registrierte ThreadGroup liefern!
		return null;
	}

	@Override
    public <GT extends Enum<?> & ThreadType> ThreadGroup getRegisteredGroup(
			final GT groupId) {
		// TODO registrierte ThreadGroup liefern!
		return null;
	}

	@Override
    public <GT extends Enum<?> & ThreadType> Iterable<StepByStepProcessor> getRunnablesOfGroupId(
			final GT groupId) {
		// TODO runnables of ThreadGroups liefern!
		return null;
	}

	@Override
    public <GT extends Enum<?> & ThreadType> boolean hasGroupRegistered(
			final GT groupId) {
		// TODO Check for registred group
		return false;
	}

	@Override
    public <GT extends Enum<?> & ThreadType> void registerGroup(
			final GT groupId, final ThreadGroup group) {
		// TODO register group
	}
}
