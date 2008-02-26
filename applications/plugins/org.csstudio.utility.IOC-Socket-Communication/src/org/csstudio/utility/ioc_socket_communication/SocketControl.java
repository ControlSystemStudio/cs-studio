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
 package org.csstudio.utility.ioc_socket_communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

public class SocketControl extends Thread {	
	final static boolean debug=false;
	private String hostAddress;
	private static int TIMEOUT_IN_MILISEC=600000; // 10 min
	private static int LOOP_TIME_IN_MILISEC=10000; // 10 sec
	private ArrayList<SocketInfo> _socketsInfosList;
	
	public SocketControl(ArrayList<SocketInfo> infosList) {
	//	super(); // TODO Auto-generated catch block
		_socketsInfosList = infosList;
	}
	
	public void run() {
		try {
			while(true) {
				if(debug) System.out.println("in run method: "+ hostAddress);
				sleep(LOOP_TIME_IN_MILISEC);
				checkOldConnection(_socketsInfosList);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkOldConnection(ArrayList<SocketInfo> socketInfos) {
		SocketInfo currentInfo;
		Date tmpDate;
		Socket currentSocket;
		Date now = new Date();
		long nowLong = now.getTime();
		for (int i=0;i<socketInfos.size();i++) { 
			currentInfo=socketInfos.get(i);
			tmpDate = currentInfo.getDate();
			if ( (nowLong - tmpDate.getTime()) > TIMEOUT_IN_MILISEC) {
				if(debug) System.out.println("CLOSE: in run method: "+ hostAddress);
				currentSocket=currentInfo.getSock();
				try {currentSocket.close();} catch (IOException e) {e.printStackTrace();}// TODO Auto-generated catch block
				socketInfos.remove(i);
			}
		}
		
		return true;
	}
	
	synchronized public Socket update(ArrayList<SocketInfo> socketsList, String IPname ,int port) {
		hostAddress = IPname;
		SocketInfo currentInfo;
		String currentHost;
		int currentPort;
		Date now = new Date();
		for (int i=0;i<socketsList.size();i++) { 
			currentInfo=socketsList.get(i);
			currentHost=currentInfo.getHost();
			currentPort=currentInfo.getPort();	
			if((currentPort==port)&&(currentHost.compareTo(IPname)==0)) {
				currentInfo.setDate(now);
				return currentInfo.getSock();
			}
		}
		return addSocket(socketsList,IPname,port);
	}
	
	private Socket addSocket(ArrayList<SocketInfo> socketsArray, String host, int port) {	
		SocketAddress sockaddr = new InetSocketAddress(host, port);
		Socket sock = new Socket();
		int timeoutMs = 3000;
		try {
			sock.connect(sockaddr, timeoutMs);
			sock.setSoTimeout(3000);  //TODO
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date now = new Date();
		SocketInfo newInfo = new SocketInfo(host,port,sock,now);
		socketsArray.add(newInfo);
		return sock;
	}
}
