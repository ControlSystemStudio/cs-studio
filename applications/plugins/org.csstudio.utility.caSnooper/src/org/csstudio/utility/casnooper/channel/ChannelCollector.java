package org.csstudio.utility.casnooper.channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * @author rkosir
 *
 */
public class ChannelCollector {
	
	private boolean first;
	private Channel firstList,secondList;
	private int delay;
	private static ChannelCollector instance;
	private ChannelCollector(){
	}

	public static synchronized ChannelCollector getInstance(){
		if(instance == null){
			instance = new ChannelCollector();
		}
		System.out.println("Somebody stole instance!");
		return instance;
	}
	/**
	 * Adds new message to the map
	 *  
	 * @param aliasName
	 * @param clientAddress
	 */
	public void addBMessage(String aliasName, InetSocketAddress clientAddress) {
		if(first){
			firstList.addChannel(aliasName, clientAddress, delay);
		}
		else{
			secondList.addChannel(aliasName, clientAddress, delay);
		}
	}
	
	/**
	 * Returns snoops received as ArrayList<DataStructure>
	 * 
	 * @return Arraylist of received snoops
	 */
	public ArrayList<ChannelStructure> getSnoops(){
		if(first){
			if(secondList != null)
				return secondList.getData();
		}
		else{ 
			if(firstList !=null)
				return firstList.getData();
		}
		System.out.println("no data? wtf");
		return null;
	}

	/**
	 * Creates the statistics and returns them as String
	 * 
	 * @param entries as ArrayList<DataStructure>
	 * @return String
	 */
	public String getStatistics(ArrayList<ChannelStructure> entries) {
		double maximumFrequency = 0,avarageFrequency = 0;
		int numberOfRequests = 0;
		String statistics;
		ChannelStructure tmp;
		Set<String> listOfAllChannels = new HashSet<String>();
		for(int i=0;i<entries.size();i++){
			tmp=entries.get(i);
			if(tmp.getFrequency()>maximumFrequency)
				maximumFrequency = tmp.getFrequency();
			numberOfRequests = numberOfRequests + tmp.getRepeats();
			avarageFrequency = avarageFrequency + tmp.getFrequency();
			listOfAllChannels.add(tmp.getAliasName());
		}
		
		avarageFrequency = avarageFrequency/entries.size();
		avarageFrequency = ((int)(avarageFrequency*1000))/1000.0;
		
		statistics = "There were "+numberOfRequests+" requests for "+listOfAllChannels.size()+" different PVs.\n";
		statistics = statistics+"Max frequency: "+maximumFrequency+"Hz\n";
		statistics = statistics+"Avarage frequency: "+avarageFrequency+"Hz\n";
		return statistics;
	}

	public void setMapToUse(boolean firstMap) {
		first = firstMap;
		if(firstMap)
			firstList = new Channel();
		else
			secondList = new Channel();
	}
}