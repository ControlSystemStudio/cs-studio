
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
 * Ein abstakter {@link MessageHandler} der auf mehreren {@link Consumer}n
 * ließt.
 */
public abstract class AbstractMultiConsumerMessageHandler implements
		MessageHandler {

	public static enum MultiConsumerMessageThreads implements ThreadType {
		CONSUMER_THREAD, HANDLER_THREAD
	};

	/**
	 * Queue zum zwischen speichern empfangener Nachrichten. BlockingQueue mit
	 * max groeße 1 damit keine Nachrichten auf Vorrat geholt werden.
	 */
	private final BlockingQueue<NAMSMessage> queue = new ArrayBlockingQueue<NAMSMessage>(
			1);
	private final List<StepByStepProcessor> processors;
	private final StepByStepProcessor masterProcessor;

	public AbstractMultiConsumerMessageHandler(final Consumer[] consumerArray,
			final ExecutionService executionService) {
		this.processors = new LinkedList<StepByStepProcessor>();

		for (final Consumer consumer : consumerArray) {
			final StepByStepProcessor stepByStepProcessor = new StepByStepProcessor() {
				@Override
				protected void doRunOneSingleStep() throws Throwable {
					try {
						NAMSMessage receivedMessage = consumer.receiveMessage();
						if (receivedMessage != null) {
							AbstractMultiConsumerMessageHandler.this.queue
									.put(receivedMessage);
						}
					} catch (MessagingException me) {
						if (me.getCause() instanceof InterruptedException) {
							// Ok, soll beendet werden....
						} else {
							throw me.fillInStackTrace();
						}
					}
				}
			};
			executionService.executeAsynchronsly(
					MultiConsumerMessageThreads.CONSUMER_THREAD,
					stepByStepProcessor);
			this.processors.add(stepByStepProcessor);
		}

		this.masterProcessor = new StepByStepProcessor() {
			@Override
			protected void doRunOneSingleStep() throws Throwable {
				try {
					AbstractMultiConsumerMessageHandler.this
							.handleMessage(AbstractMultiConsumerMessageHandler.this.queue
									.take());
				} catch (final InterruptedException ie) {
					// Ok... alles gut. Gewolltest Verhalten.
				}
			}
		};
		executionService.executeAsynchronsly(
				MultiConsumerMessageThreads.HANDLER_THREAD,
				this.masterProcessor);
		this.processors.add(this.masterProcessor);
	}

	public void beendeArbeit() {
		for (final StepByStepProcessor processor : this.processors) {
			processor.stopWorking();
		}
	}

	/**
	 * Joined mit dem Thread des Master-Processor, wartet also auf die interne
	 * Queue "to be interrupted."
	 * 
	 * @throws InterruptedException
	 */
	public void joinMasterProcessor() throws InterruptedException {
		Thread.yield();
		this.masterProcessor.joinThread();
	}
}
