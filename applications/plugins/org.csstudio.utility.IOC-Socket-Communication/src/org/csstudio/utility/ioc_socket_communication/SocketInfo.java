package org.csstudio.utility.ioc_socket_communication;

import java.net.Socket;
import java.util.Date;

public class SocketInfo {
	private String host;
	private int port;
	private Socket sock;
	private Date date;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Socket getSock() {
		return sock;
	}
	public void setSock(Socket sock) {
		this.sock = sock;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public SocketInfo(String host, int port, Socket sock, Date date) {
		this.host = host;
		this.port = port;
		this.sock = sock;
		this.date = date;
	}	
}

