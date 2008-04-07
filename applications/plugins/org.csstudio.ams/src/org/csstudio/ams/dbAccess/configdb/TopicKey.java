/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.ams.dbAccess.configdb;

import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.Key;

/**
 * Abstract topic representation. Holds the topic name, the group ordered to and
 * corresponding topic id.
 * 
 * @author Kai Meyer, Matthias Zeimer
 */
public class TopicKey extends Key {

	private static final long serialVersionUID = -4929977981892342829L;

	final private int _topicId;
	final private String _humandReadableName;
	final private int _groupRef;

	/**
	 * Creates a new instance with -1 as ID and groupRef and an empty human readable name. 
	 */
	public TopicKey() {
		this(-1,"",-1);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param topicId
	 *            The DB-Id of the topic.
	 * @param humandReadableName
	 *            The human readable name.
	 * @param groupRef
	 *            The group this topics belongs to.
	 */
	public TopicKey(final int topicId, final String humandReadableName,
			final int groupRef) {
		super(Key.TOPIC_KEY);

		this._topicId = topicId;
		this._humandReadableName = humandReadableName;
		this._groupRef = groupRef;
	}

	/**
	 * The group-id this topic belongs to.
	 */
	public int getGroupRef() {
		return _groupRef;
	}

	/**
	 * @see ItemInterface#getID()
	 */
	public int getID() {
		return getTopicId();
	}

	/**
	 * The DB-Id of the topic.
	 */
	public int getTopicId() {
		return _topicId;
	}

	/**
	 * The JMS queue name.
	 */
	public String getHumanReadableName() {
		return _humandReadableName;
	}

	/**
	 * Compares the topic id of this and another topic.
	 */
	@Override
	public boolean equals(Object obj) {
		boolean equals = false;

		if (obj instanceof TopicKey) {
			TopicKey otherKey = (TopicKey) obj;
			equals = this.getTopicId() == otherKey.getTopicId();
		}

		return equals;
	}

	/**
	 * The hashcode based on topic id.
	 */
	@Override
	public int hashCode() {
		return this.getTopicId();
	}

	/**
	 * Returns the TopicName or an empty-string if topicName is null.
	 */
	@Override
	public String toString() {
		String topicName = this.getHumanReadableName();

		return topicName == null ? "" : topicName;
	}

}
