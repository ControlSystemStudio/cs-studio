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
 package org.csstudio.alarm.treeView.preferences;

/**
 * Constant definitions for plug-in preferences.
 */
public final class PreferenceConstants {
	
	/**
	 * Private constructor.
	 */
	private PreferenceConstants() {
	}
	
	/**
	 * A preference that stores the URL for the primary JMS server.
	 */
	public static final String JMS_URL_PRIMARY = "jmsurl";
	
	/**
	 * A preference that stores the URL for the secondary JMS server.
	 */
	public static final String JMS_URL_SECONDARY = "jms.url.2";
	
	/**
	 * A preference that stores the class name of the context factory to use
	 * for the primary JMS server.
	 */
	public static final String JMS_CONTEXT_FACTORY_PRIMARY = "jms.contextfactory.1";

	/**
	 * A preference that stores the class name of the context factory to use
	 * for the secondary JMS server.
	 */
	public static final String JMS_CONTEXT_FACTORY_SECONDARY = "jms.contextfactory.2";
	
	/**
	 * A preference that stores the name or names of the JMS queue to connect
	 * to.
	 */
	public static final String JMS_QUEUE = "jms.queue";
	
	/**
	 * A preference that stores the facility names that should be displayed
	 * in the tree.
	 */
	public static final String FACILITIES = "NODE";

}
