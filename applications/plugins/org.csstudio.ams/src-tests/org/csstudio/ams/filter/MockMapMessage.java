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
 /**
 * 
 */
package org.csstudio.ams.filter;

import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import junit.framework.Assert;

/**
 * A mock of {@link MapMessage}. Extend this type and override selected methods
 * to enable specific behavior, by default all methods will cause the test to
 * fail.
 * 
 * @author C1 WPS / KM, MZ
 */
public abstract class MockMapMessage implements MapMessage {
	public boolean getBoolean(String arg0) throws JMSException {
		Assert.fail();
		return false;
	}

	public byte getByte(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public byte[] getBytes(String arg0) throws JMSException {
		Assert.fail();
		return null;
	}

	public char getChar(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public double getDouble(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public float getFloat(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public int getInt(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public long getLong(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public Enumeration getMapNames() throws JMSException {
		Assert.fail();
		return null;
	}

	public Object getObject(String arg0) throws JMSException {
		Assert.fail();
		return null;
	}

	public short getShort(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public String getString(String arg0) throws JMSException {
		Assert.fail();
		return null;
	}

	public boolean itemExists(String arg0) throws JMSException {
		Assert.fail();
		return false;
	}

	public void setBoolean(String arg0, boolean arg1) throws JMSException {
		Assert.fail();
	}

	public void setByte(String arg0, byte arg1) throws JMSException {
		Assert.fail();
	}

	public void setBytes(String arg0, byte[] arg1) throws JMSException {
		Assert.fail();
	}

	public void setBytes(String arg0, byte[] arg1, int arg2, int arg3)
			throws JMSException {
		Assert.fail();
	}

	public void setChar(String arg0, char arg1) throws JMSException {
		Assert.fail();
	}

	public void setDouble(String arg0, double arg1) throws JMSException {
		Assert.fail();
	}

	public void setFloat(String arg0, float arg1) throws JMSException {
		Assert.fail();
	}

	public void setInt(String arg0, int arg1) throws JMSException {
		Assert.fail();
	}

	public void setLong(String arg0, long arg1) throws JMSException {
		Assert.fail();
	}

	public void setObject(String arg0, Object arg1) throws JMSException {
		Assert.fail();
	}

	public void setShort(String arg0, short arg1) throws JMSException {
		Assert.fail();
	}

	public void setString(String arg0, String arg1) throws JMSException {
		Assert.fail();
	}

	public void acknowledge() throws JMSException {
		Assert.fail();
	}

	public void clearBody() throws JMSException {
		Assert.fail();
	}

	public void clearProperties() throws JMSException {
		Assert.fail();
	}

	public boolean getBooleanProperty(String arg0) throws JMSException {
		Assert.fail();
		return false;
	}

	public byte getByteProperty(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public double getDoubleProperty(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public float getFloatProperty(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public int getIntProperty(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public String getJMSCorrelationID() throws JMSException {
		Assert.fail();
		return null;
	}

	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		Assert.fail();
		return null;
	}

	public int getJMSDeliveryMode() throws JMSException {
		Assert.fail();
		return 0;
	}

	public Destination getJMSDestination() throws JMSException {
		Assert.fail();
		return null;
	}

	public long getJMSExpiration() throws JMSException {
		Assert.fail();
		return 0;
	}

	public String getJMSMessageID() throws JMSException {
		Assert.fail();
		return null;
	}

	public int getJMSPriority() throws JMSException {
		Assert.fail();
		return 0;
	}

	public boolean getJMSRedelivered() throws JMSException {
		Assert.fail();
		return false;
	}

	public Destination getJMSReplyTo() throws JMSException {
		Assert.fail();
		return null;
	}

	public long getJMSTimestamp() throws JMSException {
		Assert.fail();
		return 0;
	}

	public String getJMSType() throws JMSException {
		Assert.fail();
		return null;
	}

	public long getLongProperty(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public Object getObjectProperty(String arg0) throws JMSException {
		Assert.fail();
		return null;
	}

	public Enumeration getPropertyNames() throws JMSException {
		Assert.fail();
		return null;
	}

	public short getShortProperty(String arg0) throws JMSException {
		Assert.fail();
		return 0;
	}

	public String getStringProperty(String arg0) throws JMSException {
		Assert.fail();
		return null;
	}

	public boolean propertyExists(String arg0) throws JMSException {
		Assert.fail();
		return false;
	}

	public void setBooleanProperty(String arg0, boolean arg1)
			throws JMSException {
		Assert.fail();
	}

	public void setByteProperty(String arg0, byte arg1) throws JMSException {
		Assert.fail();
	}

	public void setDoubleProperty(String arg0, double arg1) throws JMSException {
		Assert.fail();
	}

	public void setFloatProperty(String arg0, float arg1) throws JMSException {
		Assert.fail();
	}

	public void setIntProperty(String arg0, int arg1) throws JMSException {
		Assert.fail();
	}

	public void setJMSCorrelationID(String arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSCorrelationIDAsBytes(byte[] arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSDeliveryMode(int arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSDestination(Destination arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSExpiration(long arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSMessageID(String arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSPriority(int arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSRedelivered(boolean arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSReplyTo(Destination arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSTimestamp(long arg0) throws JMSException {
		Assert.fail();
	}

	public void setJMSType(String arg0) throws JMSException {
		Assert.fail();
	}

	public void setLongProperty(String arg0, long arg1) throws JMSException {
		Assert.fail();
	}

	public void setObjectProperty(String arg0, Object arg1) throws JMSException {
		Assert.fail();
	}

	public void setShortProperty(String arg0, short arg1) throws JMSException {
		Assert.fail();
	}

	public void setStringProperty(String arg0, String arg1) throws JMSException {
		Assert.fail();
	}
}