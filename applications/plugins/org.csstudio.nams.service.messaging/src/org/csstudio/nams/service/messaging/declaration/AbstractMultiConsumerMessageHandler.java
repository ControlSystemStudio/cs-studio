package org.csstudio.nams.service.messaging.declaration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.service.StepByStepProcessor;
import org.csstudio.nams.common.service.ThreadType;
import org.csstudio.nams.service.messaging.exceptions.MessagingException;

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
					try {
					NAMSMessage receivedMessage = consumer.receiveMessage();
					if (receivedMessage != null) {
						queue.put(receivedMessage);
					}
					} catch( MessagingException me ) {
						if( me.getCause() instanceof InterruptedException ) {
							// Ok, soll beendet werden....
						} else {
							throw me.fillInStackTrace();
						}
					}
				}
			};
			executionService.executeAsynchronsly(MultiConsumerMessageThreads.CONSUMER_THREAD, stepByStepProcessor);
			processors.add(stepByStepProcessor);
		}
		
		masterProcessor = new StepByStepProcessor() {
			@Override
			protected void doRunOneSingleStep() throws Throwable {
				try {
					handleMessage(queue.take());
				} catch(InterruptedException ie) {
					// Ok... alles gut. Gewolltest Verhalten.
				}
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
		Thread.yield();
		masterProcessor.joinThread();
	}
	
	public void beendeArbeit() {
		for (StepByStepProcessor processor : processors) {
			processor.stopWorking();
		}
	}
}
