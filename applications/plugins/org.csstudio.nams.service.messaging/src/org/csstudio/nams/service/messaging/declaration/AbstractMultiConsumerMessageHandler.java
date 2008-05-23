package org.csstudio.nams.service.messaging.declaration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;

public abstract class AbstractMultiConsumerMessageHandler implements MessageHandler{

	// FIXME WICHTIG!!!!!!!!
	enum TODO implements ThreadType {TODO};
	
	private final BlockingQueue<NAMSMessage> queue = new ArrayBlockingQueue<NAMSMessage>(1);
	
	public AbstractMultiConsumerMessageHandler(Consumer[] consumerArray, ExecutionService executionService) {
		for (final Consumer consumer: consumerArray) {
			executionService.executeAsynchronsly(TODO.TODO, new StepByStepProcessor() {
				@Override
				protected void doRunOneSingleStep() throws Throwable {
					queue.add(consumer.receiveMessage());
				}
			});
		}
		executionService.executeAsynchronsly(TODO.TODO, new StepByStepProcessor() {
			@Override
			protected void doRunOneSingleStep() throws Throwable {
				handleMessage(queue.take());
			}
		});
	}
}
