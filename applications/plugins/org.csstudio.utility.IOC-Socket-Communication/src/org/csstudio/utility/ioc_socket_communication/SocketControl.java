package org.csstudio.utility.ioc_socket_communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

public class SocketControl extends Thread {	
	final static boolean debug=true;
	private String hostAddress;
	private static int TIMEOUT_IN_MILISEC=8000;
	private static int LOOP_TIME_IN_MILISEC=2000;
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
		for (int i=0;i<socketsList.size();i++) { 
			currentInfo=socketsList.get(i);
			currentHost=currentInfo.getHost();
			currentPort=currentInfo.getPort();	
			if((currentPort==port)&&(currentHost.compareTo(IPname)==0)) return currentInfo.getSock();
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
