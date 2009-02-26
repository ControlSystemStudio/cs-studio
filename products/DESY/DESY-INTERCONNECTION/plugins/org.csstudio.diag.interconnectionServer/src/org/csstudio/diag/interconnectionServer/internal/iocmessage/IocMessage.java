/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.diag.interconnectionServer.internal.iocmessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A message used in communication with the IOC. A message is a list of items,
 * which are tag value pairs.
 * 
 * @author Joerg Rathlev
 */
public class IocMessage {
	
	private Map<String, TagValuePair> _items;
	private int _messageId;
	private String _messageTypeString;

	/**
	 * Creates a new IOC message.
	 */
	public IocMessage() {
		_items = new HashMap<String, TagValuePair>();
		_messageId = -1;
	}

	/**
	 * Returns whether this message is valid. A message is valid if it has at
	 * least a TYPE and an ID tag.
	 * 
	 * @return <code>true</code> if this message is valid, <code>false</code>
	 *         otherwise.
	 */
	public boolean isValid() {
		// TODO Refactor. This should be doable without hard coded strings.
		return contains("ID") && contains("TYPE");
	}

	/**
	 * Adds a new item to this message.
	 * 
	 * @param item
	 *            the item.
	 */
	// TODO: prevent duplicate tags?
	public void addItem(TagValuePair item) {
		_items.put(item.getTag(), item);
		
		// If the item that was added specifies the message ID, store the
		// message ID in the respective field.
		if (TagList.getTagType(item.getTag()) == TagList.TAG_TYPE_ID) {
			try {
				_messageId = Integer.parseInt(item.getValue());
			} catch (NumberFormatException e) {
				// Message ID is not an integer number. Simply ignore it.
			}
		} else if (TagList.getTagType(item.getTag()) == TagList.TAG_TYPE_TYPE) {
			_messageTypeString = item.getValue();
		}
	}

	/**
	 * Returns whether this message contains an item with the specified tag.
	 * 
	 * @param tag
	 *            the tag.
	 * @return <code>true</code> if this message contains an item with the
	 *         specified tag, <code>false</code> otherwise.
	 */
	public boolean contains(String tag) {
		return _items.containsKey(tag);
	}

	/**
	 * Returns the item with the specified tag.
	 * 
	 * @param tag
	 *            the tag.
	 * @return the item with the specified tag, or <code>null</code> if such an
	 *         item does not exist in this message.
	 */
	// TODO: throw something like NoSuchElementException instead?
	public TagValuePair getItem(String tag) {
		return _items.get(tag);
	}

	/**
	 * Returns the list of items contained in this message.
	 * 
	 * @return the list of items.
	 */
	public Collection<TagValuePair> getItems() {
		return _items.values();
	}

	/**
	 * Returns the message ID of this message. If this message does not have an
	 * ID, returns -1.
	 * 
	 * @return the message ID of this message.
	 */
	public int getMessageId() {
		return _messageId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		// TODO: This currently returns the items in an undefined order. For
		// debugging purposes, it might be more useful to return the items in
		// exactly the order in which they were received from the IOC.
		for (TagValuePair tvp : _items.values()) {
			stringBuilder.append(tvp.toString());
		}
		return stringBuilder.toString();
	}

	/**
	 * Returns whether this message has a message ID.
	 * 
	 * @return <code>true</code> if this message has a message ID,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasMessageId() {
		return getMessageId() != -1;
	}

	/**
	 * Returns the message type as a string. If no tag value pair with TYPE tag
	 * was added to this message, returns <code>null</code>.
	 * 
	 * @return the message type, or <code>null</code> if this message has no
	 *         type.
	 */
	public String getMessageTypeString() {
		return _messageTypeString;
	}
}
