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
 * <p>The classes in this package are responsible for receiving alarm messages
 * via JMS and applying the necessary updates to the tree view.</p>
 * 
 * <p>Some notes on how threading and queueing is used in the alarm tree: the
 * alarm tree must connect to the JMS topic before reading the entries from the
 * LDAP directory to ensure that it will not miss any updates. This is
 * implemented in this package by adding pending updates into a queue which is
 * processed as soon as a tree to apply the updates to becomes available.</p>
 * 
 * <p>JMS messages are received by an {@link AlarmMessageListener} in a thread
 * owned by the JMS implementation. The listener interprets them as alarm
 * messages, creates a {@link PendingUpdate}, and writes this update into a
 * queue.</p>
 * 
 * <p>The updates must eventually be applied in the UI thread, but the UI thead
 * cannot block on the queue because that would block the UI. Therefore, the
 * listener uses an internal queue worker thread which waits for pending updates
 * by a blocking call on the queue. Pending updates are then applied to the tree
 * using an {@link AlarmTreeUpdater}, which performs the necessary changes to
 * the model and triggers the UI update in the UI thread.</p>
 */
package org.csstudio.alarm.treeView.jms;
