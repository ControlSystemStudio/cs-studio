package org.csstudio.diag.interconnectionServer.internal.iocmessage;



/**
 * <p>
 * Detects duplicate messages. Duplicates are detected by their message ID. When
 * a duplicate message is detected, the detector notifies an
 * {@link IDuplicateMessageHandler}.
 * </p>
 * 
 * <p>
 * The detector will not detect all duplicates because it does not store an
 * unlimited number of old messages. If a duplicate message occurs after more
 * than the number of internally stored old messages, the detector does not
 * recognize it as a duplicate message.
 * </p>
 * 
 * @author Joerg Rathlev
 */
public class DuplicateMessageDetector {
	
	/**
	 * The maximum number of old messages that will be stored in this detector.
	 */
	private static final int NUMBER_OF_OLD_MESSAGES = 50;
	
	/**
	 * The old messages.
	 */
	private final IocMessage[] _oldMessages;
	
	/**
	 * The index in the array of old messages to which the next message will be
	 * written.
	 */
	private int _nextWriteIndex;

	/**
	 * The handler that this detector will notify when detecting a duplicate
	 * message.
	 */
	private final IDuplicateMessageHandler _handler;

	/**
	 * Creates a new duplicate message detector.
	 * 
	 * @param handler
	 *            the handler that this detector will notify when detecting a
	 *            duplicate message.
	 */
	public DuplicateMessageDetector(IDuplicateMessageHandler handler) {
		_oldMessages = new IocMessage[NUMBER_OF_OLD_MESSAGES];
		_nextWriteIndex = 0;
		_handler = handler;
	}
	
	/**
	 * Checks whether a message is a duplicate. The checked message will be
	 * stored by this detector. If the message does not have a message ID, it
	 * will be ignored.
	 * 
	 * @param message
	 *            the message.
	 * 
	 * @return <code>true</code> if the message is a duplicate message,
	 *         <code>false</code> if this detector could not recognize it as a
	 *         duplicate message.
	 */
	public synchronized void checkAndRemember(IocMessage message) {
		// The duplicate message detector works based on message IDs. If the
		// message doesn't have an ID, the detector simply ignores it.
		if (!message.hasMessageId()) {
			return;
		}
		
		// See if there was an earlier message with the same ID. This is done
		// in a synchronized region so that concurrent checks don't conflict.
		IocMessage firstMessageWithSameId;
		synchronized (this) {
			firstMessageWithSameId = check(message);
			remember(message);
		}

		// If an earlier message with the same ID was found, notify the
		// duplicate message handler. This is outside the synchronized block
		// so that the lock isn't kept longer than necessary and the risk of
		// deadlocks is reduced.
		if (firstMessageWithSameId != null) {
			_handler.duplicateMessageDetected(firstMessageWithSameId, message);
		}
	}

	/**
	 * Stores the given message.
	 * 
	 * @param message
	 *            the message.
	 */
	private void remember(IocMessage message) {
		_oldMessages[_nextWriteIndex++] = message;
		if (_nextWriteIndex >= _oldMessages.length) {
			_nextWriteIndex = 0;
		}
	}

	/**
	 * Checks if the given message is a duplicate and returns the original
	 * message if it is a duplicate.
	 * 
	 * @param message
	 *            the message to check.
	 * @return the original message if the checked message is a duplicate,
	 *         <code>null</code> otherwise.
	 */
	private IocMessage check(IocMessage message) {
		for (IocMessage oldMessage : _oldMessages) {
			if (oldMessage != null) {
				if (message.getMessageId() == oldMessage.getMessageId()) {
					return oldMessage;
				}
			}
		}
		return null;
	}
}
