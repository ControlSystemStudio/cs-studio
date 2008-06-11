package org.csstudio.nams.service.messaging.declaration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;

/**
 * Ein abstakter {@link MessageHandler} der auf mehreren {@link Consumer}n ließt.
 */
public abstract class AbstractMultiConsumerMessageHandler implements MessageHandler{

	public static enum MultiConsumerMessageThreads implements ThreadType {CONSUMER_THREAD, HANDLER_THREAD};
	
	/**
	 * Queue zum zwischen speichern empfangener Nachrichten.
	 * BlockingQueue mit max groeße 1 damit keine Nachrichten auf Vorrat geholt werden.
	 */
	private final BlockingQueue<NAMSMessage> queue = new ArrayBlockingQueue<NAMSMessage>(1);
	private List<StepByStepProcessor> processors;
	private StepByStepProcessor masterProcessor;
	
	public AbstractMultiConsumerMessageHandler(Consumer[] consumerArray, ExecutionService executionService) {
		processors = new LinkedList<StepByStepProcessor>();
		
		for (final Consumer consumer: consumerArray) {
			StepByStepProcessor stepByStepProcessor = new StepByStepProcessor() {
				@Override
				protected void doRunOneSingleStep() throws Throwable {
					NAMSMessage receivedMessage = consumer.receiveMessage();
					if (receivedMessage != null) {
						queue.add(receivedMessage);
					}
				}
			};
			executionService.executeAsynchronsly(MultiConsumerMessageThreads.CONSUMER_THREAD, stepByStepProcessor);
			processors.add(stepByStepProcessor);
		}
		
		masterProcessor = new StepByStepProcessor() {
			@Override
			protected void doRunOneSingleStep() throws Throwable {
				handleMessage(queue.take());
			}
		};
		executionService.executeAsynchronsly(MultiConsumerMessageThreads.HANDLER_THREAD, masterProcessor);
		processors.add(masterProcessor);
	}

	/**
	 * Joined mit dem Thread des Master-Processor, wartet also auf die interne Queue "to be interrupted."
	 * @throws InterruptedException 
	 */
	public void joinMasterProcessor() throws InterruptedException {
		masterProcessor.joinThread();
	}
	
	public void beendeArbeit() {
		for (StepByStepProcessor processor : processors) {
			processor.stopWorking();
		}
	}
}
