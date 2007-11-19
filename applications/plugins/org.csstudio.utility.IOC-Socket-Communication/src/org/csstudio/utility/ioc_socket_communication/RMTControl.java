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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;


public class RMTControl {

	private static RMTControl _instance;

	private Socket sock = null;
	private int port = 9003;
	private IOCAnswer iocanswer;


	private RMTControl() {
	}

	public static RMTControl getInstance() {
	        if (_instance == null)
	            _instance = new RMTControl();
	        return _instance;
	    }

	  public void send(String address, String message, int port, IOCAnswer iocanswer) {
		  this.port = port;
		  send(address, message, iocanswer);
	  }

	  public void send(String address, String message, IOCAnswer ioca) {
		  try {
			  this.iocanswer = ioca;
//			sock = new Socket(address, port);
//			InputStream in = sock.getInputStream();
////			OutputStream out = sock.getOutputStream();
//			//set timeout
//			sock.setSoTimeout(3000);
			Receiver rec = new Receiver(address, port, iocanswer);
//			rec.addJobChangeListener(new JobChangeAdapter() {
//		        public void done(IJobChangeEvent event) {
//		        if (event.getResult().isOK()) 
//		        	RMTControl.this.iocanswer.notifyView();
//		        }
//		     });
			rec.schedule();
			PrintStream os = new PrintStream(sock.getOutputStream());
			os.println(message);

		}  catch (UnknownHostException e) {
			Activator.logException("UnknownHostException", e);
		} catch (IOException e) {
			Activator.logException("IOException", e);
		}
	  }
}
