package org.csstudio.utility.caSnooperUi.parser;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.eclipse.core.runtime.PlatformObject;
//TODO jhatje: implement new datatypes

public class ChannelStructure extends PlatformObject implements Serializable { //IProcessVariable,Serializable	{
	private int repeats;
	private String clientAddress;
	private String aliasName;
	private int interval;
	private int tableId;
	
	public ChannelStructure(InetSocketAddress clientAddress, String aliasName, int interval) {
		this.aliasName = aliasName;
		this.clientAddress = clientAddress.getHostName()+":"+clientAddress.getPort();
		repeats = 1;
		this.interval = interval;
	}
	
	public ChannelStructure(int repeats, String clientAddress, String channelName, int interval, int tableId){
		setRepeats(repeats);
		setClientAddress(clientAddress);
		setAliasName(channelName);
		setInterval(interval);
		setTableId(tableId);
	}
	
	/**
	 * Returns the hostname and port of a source
	 * @return String
	 */
	public String getClientAddress(){
		return clientAddress;
	}
	
	/**
	 * Returns the PV name
	 * @return String
	 */
	public String getAliasName(){
		return aliasName;
	}
	
	/**
	 * Returns the number of PV repeats
	 * @return
	 */
	public int getRepeats(){
		return repeats;
	}
	
	public void setRepeats(int repeats){
		this.repeats = repeats;
	}

	/**
	 * Increases the repeats
	 */
	public void updateRepeats() {
		repeats++;
	}
	
	/**
	 * Returns the frequency of a channel
	 * @return double
	 */
	public double getFrequency(){
		return ((int)((getRepeats()*1000)/interval))/1000.0;
	}
	
	/**
	 * Sets the current ID in table  
	 */
	public void setId(int id){
		tableId = id;
	}
	
	/**
	 * Returns the current ID in table
	 * @return int
	 */
	public int getId(){
		return tableId;
	}
	
	public String getName() {
		return aliasName;
	}

	//TODO jhatje: implement new datatypes
	public String getTypeId() {
//		return IProcessVariable.TYPE_ID;
		return null;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
}
