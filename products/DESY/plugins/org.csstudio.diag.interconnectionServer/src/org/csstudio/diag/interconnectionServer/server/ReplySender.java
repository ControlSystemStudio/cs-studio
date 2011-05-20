/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.diag.interconnectionServer.server;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;


/**
 * Sends a reply to the IOC.
 *
 * @author Joerg Rathlev
 */
class ReplySender {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(ReplySender.class);
    /*
	 * TODO
	 * For compatibility reasons the message sender on the IOC side
	 * now accepts STATUS and REPLY - but ONLY the NEW version!
	 * For the OLD version on the IOC side we still need to send STATUS!
	 * As soon ans the IOCs all get loaded with the new software version 0.4.2
	 * we can switch from STATUS to REPLY - as planned before
	 */

//	private static final String replyTagName = "REPLY";
	private static final String replyTagName = "STATUS";

	private ReplySender() {
	}

	/**
	 * Sends a reply to the IOC which signals that the message with the
	 * specified ID was processed successfully.
	 *
	 * @param id
	 *            the message ID to which a reply will be sent.
	 * @param sender
	 *            the sender which sends the message to the IOC.
	 * @throws NamingException
	 */
	public static void sendSuccess(final int id, final IIocMessageSender sender) throws NamingException {
		sender.send(prepareSuccessReply(id));
	}

	/**
	 * Sends a reply to the IOC which signals that an error occurred while
	 * processing the message with the specified ID.
	 *
	 * @param id
	 *            the message ID to which a reply will be sent.
	 * @param sender
	 *            the sender which sends the message to the IOC.
	 * @throws NamingException
	 */
	public static void sendError(final int id, final IIocMessageSender sender) throws NamingException {
		sender.send(prepareErrorReply(id));
	}

	/**
	 * Sends a reply to the IOC which signals either success or an error,
	 * depending on the value of the <code>success</code> parameter.
	 *
	 * @param id
	 *            the message ID to which a reply will be sent.
	 * @param success
	 *            whether the reply should indicate success or an error.
	 * @param sender
	 *            the sender which sends the message to the IOC.
	 * @throws NamingException
	 */
	public static void send(final int id, final boolean success, final IIocMessageSender sender) {
	    try {
	        if (success) {
	            sendSuccess(id, sender);
	        } else {
	            sendError(id, sender);
	        }
	    } catch (final NamingException e) {
	        LOG.error("Sending of " + id + " failed due to LDAP name composition.", e);
	    }
	}

	/**
	 * Creates a reply message that signals success to the IOC.
	 *
	 * @param id
	 *            the message ID to which a reply is prepared.
	 * @return the reply message.
	 */
	private static String prepareSuccessReply(final int id) {
		// TODO replace hard coded strings
		return "ID=" + id + ";" + replyTagName + "=Ok;";
	}

	/**
	 * Creates a reply message that signals an error to the IOC.
	 *
	 * @param id
	 *            the message ID to which a reply is prepared.
	 * @return the reply message.
	 */
	private static String prepareErrorReply(final int id) {
		// TODO replace hard coded strings
		return "ID=" + id + ";" + replyTagName + "=Error;";
	}
}
