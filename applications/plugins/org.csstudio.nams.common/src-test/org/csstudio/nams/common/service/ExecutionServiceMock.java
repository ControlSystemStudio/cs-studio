package org.csstudio.nams.common.service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.csstudio.nams.common.service.ThreadType;


/**
 * Ein Mock-Execution service, das ausführen erfolgt synchron und manuell.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
public class ExecutionServiceMock implements ExecutionService {

//		return errors; Marek: -2602

	public <GT extends Enum<?> & ThreadType> void mockExecuteOneStepOf(GT groupId) throws Throwable {
		List<StepByStepProcessor> list = allStepByStepProcessors.get(groupId);
		if( list == null )
		{ 
			Assert.fail("group not registered.");
		}
		for (StepByStepProcessor stepByStepProcessor : list) {
			stepByStepProcessor.doRunOneSingleStep();
		}
	}
	
	private Map<Enum<?>, List<StepByStepProcessor>> allStepByStepProcessors = new HashMap<Enum<?>, List<StepByStepProcessor>>();
	
	public <GT extends Enum<?> & ThreadType> void executeAsynchronsly(
			GT groupId, StepByStepProcessor runnable) {
		System.out.println("ExecutionServiceMock.executeAsynchronsly(): "+groupId+ ", time: "+System.nanoTime()+", all: "+allStepByStepProcessors.toString());
		
		for (Enum<?> enuum :allStepByStepProcessors.keySet()) {
			System.out.println(enuum.toString() + " = " + groupId.toString() + " is " + (enuum == groupId));
		}
		
		List<StepByStepProcessor> list = allStepByStepProcessors.get(groupId);
		if( list == null )
		{ 
			System.out.println("ExecutionServiceMock.executeAsynchronsly(): "+groupId+ ", time: "+System.nanoTime()+", all: "+allStepByStepProcessors.toString());
			Assert.fail("group not registered: "+groupId);
		}
		list.add(runnable);
		allStepByStepProcessors.put(groupId, list);
	}

	public <GT extends Enum<?> & ThreadType> ThreadGroup getRegisteredGroup(
			GT groupId) {
		Assert.fail("unexpected method call");
		return null;
	}

	public <GT extends Enum<?> & ThreadType> Iterable<StepByStepProcessor> getRunnablesOfGroupId(
			GT groupId) {
		List<StepByStepProcessor> list = allStepByStepProcessors.get(groupId);
		if( list == null )
		{ 
			list = Collections.emptyList();
		}
		return list;
	}

	public <GT extends Enum<?> & ThreadType> boolean hasGroupRegistered(
			GT groupId) {
		return allStepByStepProcessors.keySet().contains(groupId);
	}

	public <GT extends Enum<?> & ThreadType> void registerGroup(GT groupId,
			ThreadGroup group) {
		allStepByStepProcessors.put(groupId, new LinkedList<StepByStepProcessor>());
		System.out.println("ExecutionServiceMock.registerGroup(): "+groupId+ ", time: "+System.nanoTime()+", all: "+allStepByStepProcessors.toString());
	}

	public <GT extends Enum<?> & ThreadType> Iterable<GT> getCurrentlyUsedGroupIds() {
		Assert.fail("unexpected method call");
		return null;
	}
}
