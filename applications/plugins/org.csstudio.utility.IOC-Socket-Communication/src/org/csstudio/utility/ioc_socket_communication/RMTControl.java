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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//class URL {String host; int port;}

public class RMTControl {
	final static boolean debug=false;
	private static RMTControl _instance;
	private SocketControl sockControl = null;//SocketControl.getInstance();
	private int port = 9003;
	private IOCAnswer iocanswer;
	private ArrayList<SocketInfo> socketsInfosList = new ArrayList<SocketInfo>();
	private RMTControl() {}
	public static RMTControl getInstance() {
		if (_instance == null)
			_instance = new RMTControl();
		return _instance;
	}
	public ArrayList<SocketInfo> getSocketsInfosList() {
		return socketsInfosList;
	}
	synchronized	public void send(String address, String message, int port,IOCAnswer iocanswer) {
		this.port = port;
		send(address, message, iocanswer);
	}
	synchronized public void send(String address, String message, IOCAnswer ioca) {
		this.iocanswer = ioca;
		//is tread already started??
		//no -> start it
		if (sockControl == null) {
			sockControl = new SocketControl(socketsInfosList);
		}
		if(!sockControl.isAlive()) {
			sockControl.start();
		}
		Socket currentSocket=sockControl.update(socketsInfosList, address,this.port);
		System.out.println("SocketID="+currentSocket);

		Receiver rec = new Receiver(currentSocket, message, address, port, iocanswer);
		rec.schedule();
	}
	
	
	public void closeAll() {
		for (int i=0;i<socketsInfosList.size();i++) { 
			try {
				socketsInfosList.get(i).getSock().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
