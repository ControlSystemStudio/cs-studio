/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ioc_socket_communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class Receiver extends Job {

	private Socket sock = null;
	private InputStream in = null;
	private IOCAnswer iocAnswer;
	private String address;
	private int port;
	private boolean transmitted = false;
	private String message = null;
	
	public Receiver(Socket sock, String message, String address, int port,
			IOCAnswer iocAnswer) {
		super("IOCReader");
		this.message = message;
		this.iocAnswer = iocAnswer;
		this.address = address;
		this.port = port;
		this.sock = sock;
	}

/**
 * 
 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
		if (in == null) {
			SocketAddress sockaddr = new InetSocketAddress(address, port);
			sock = new Socket();
			int timeoutMs = 3000;
			sock.connect(sockaddr, timeoutMs);
			in = sock.getInputStream();
			sock.setSoTimeout(3000);
		} else {
		
		
//			if (in == null) {
//				SocketAddress sockaddr = new InetSocketAddress(address, port);
//				sock = new Socket();
				int timeoutMs = 3000;
//				sock.connect(sockaddr, timeoutMs);
				in = sock.getInputStream();
				sock.setSoTimeout(3000);
		}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return Status.CANCEL_STATUS;
		} catch (IOException e1) {
			e1.printStackTrace();
			return Status.CANCEL_STATUS;
		}

		
		if (transmitted == false) {
			PrintStream os;
			try {
				os = new PrintStream(sock.getOutputStream());
				os.println(message);
			} catch (IOException e) {
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
			transmitted = true;
		}

		boolean finished = false;
		StringBuffer answer = new StringBuffer();
		if (in != null) {

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in));
			try {
				if (bufferedReader.ready()) {
					finished = true;
					while (bufferedReader.ready()) {
						answer.append(bufferedReader.readLine());
					}
				}
			} catch (IOException e) {
				Activator.logException("IOException", e);
				return Status.CANCEL_STATUS;
			}
		}
		if (finished == false) {
			schedule(1000);
		} else {
			iocAnswer.setAnswer(answer.toString());
		}
		return Status.OK_STATUS;
	}
}
