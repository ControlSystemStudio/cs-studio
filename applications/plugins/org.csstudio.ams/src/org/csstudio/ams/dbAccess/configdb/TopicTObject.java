
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
import org.csstudio.ams.dbAccess.TObject;

/**
 * The representation of a DB-data-set of Topics.
 * 
 * SQL-Statement:
 * 
 * <pre>
 * create table AMS_Topic
 * (
 *  iTopicId 		INT NOT NULL,
 * 	iGroupRef		INT default -1 NOT NULL,
 * 	cTopicName 		VARCHAR(128),
 * 	cName	 		VARCHAR(128),
 * 	cDescription	VARCHAR(256),
 * 	PRIMARY KEY (iTopicId)						
 * );
 * </pre>
 */
@SuppressWarnings("hiding")
public class TopicTObject extends TObject implements ItemInterface {
	
    /**
	 * generated Serial version id.
	 */
	private static final long serialVersionUID = 1336106394298251516L;

	public static final int INITIAL_NON_DB_KEY = -1;

	/**
	 * The id of the topic (used like user-id), db-field: iTopicId.
	 */
	private int topicID;// PRIMARY KEY

	/**
	 * The name of the Topic at the JMS-Server, db-field: cTopicName.
	 */
	private String topicName;

	/**
	 * Human-readable name of the topic, db-field: cName.
	 */
	private String humanReadableName;

	/**
	 * The id of corresponding group, db-field: iGroupRef.
	 */
	private int groupRef;

	/**
	 * An optional description of this topic, db-field: cDescription.
	 */
	private String description;

	/**
	 * Creates an empty {@link TopicTObject} with an id of -1.
	 */
	public TopicTObject() {
		this.topicID = INITIAL_NON_DB_KEY;
		this.groupRef = GroupKey.NO_GROUP;
	}

	/**
	 * Creates a {@link TopicTObject} with given configuration.
	 * 
	 * @param topicID
	 *            The id of this topic, should be greater than 0.
	 * @param topicName
	 *            The name of this topic, not null.
	 * @param humanReadableName
	 *            The human readable name of this topic, not null.
	 * @param groupRef
	 *            The id of the corresponding group
	 * @param description
	 *            The optional description of this topic, may be null.
	 */
	public TopicTObject(int topicID, String topicName,
			String humanReadableName, int groupRef, String description) {
		super();
		this.topicID = topicID;
		this.topicName = topicName;
		this.humanReadableName = humanReadableName;
		this.groupRef = groupRef;
		this.description = description;
	}

	@Deprecated
	public TopicTObject(int topicID, String topicName, String url, String port,
			String protocol) {
		this.topicID = topicID;
		this.topicName = topicName;
	}

	/**
	 * The id of this topic.
	 */
	@Override
    public int getID() {
		return topicID;
	}

	/**
	 * Returns a {@link TopicKey}-representation of this {@link TopicTObject}.
	 */
	public TopicKey getKey() {
		return new TopicKey(this.getID(), this.getHumanReadableName(), this.getGroupRef());
	}
	
	/**
	 * The TopicName of this topic at the JMS-Server.
	 */
	public String getTopicName() {
		return topicName;
	}

	/**
	 * Sets the topicName of this topic at the JMS-Server.
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	/**
	 * The Id of this Topic. Equivalent to {@link #getID()}.
	 */
	public int getTopicID() {
		return getID();
	}

	/**
	 * Sets this topic-id - formally the id - of this topic.
	 * 
	 * @param topicID
	 *            The topicId, should be grater than 0.
	 */
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}

	/**
	 * The human readable name of this topic.
	 */
	public String getHumanReadableName() {
		return this.humanReadableName;
	}

	/**
	 * Sets the human readable name of this topic.
	 */
	public void setHumanReadableName(String humanReadableName) {
		this.humanReadableName = humanReadableName;
	}

	/**
	 * The id of the corresponding group of this topic.
	 */
	public int getGroupRef() {
		return groupRef;
	}

	/**
	 * Sets the id of the corresponding group of this topic.
	 */
	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * The description of this topic, may be null.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this topic, may be null.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + groupRef;
		result = prime
				* result
				+ ((humanReadableName == null) ? 0 : humanReadableName
						.hashCode());
		result = prime * result + topicID;
		result = prime * result
				+ ((topicName == null) ? 0 : topicName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TopicTObject))
			return false;
		final TopicTObject other = (TopicTObject) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (groupRef != other.groupRef)
			return false;
		if (humanReadableName == null) {
			if (other.humanReadableName != null)
				return false;
		} else if (!humanReadableName.equals(other.humanReadableName))
			return false;
		if (topicID != other.topicID)
			return false;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		return true;
	}
}
