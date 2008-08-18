package org.csstudio.utility.casnooper.channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Implementation of channel collection with 2 distinct Channel structures for holding 
 * informations about broadcasts 
 * 
 * @author rkosir
 */
public class ChannelCollector {
	
	/**
	 * Variable to indicate which list should be used, if true,it will fill firstList
	 */
	private boolean first;
	
	/**
	 * Structures for meessage colelctions
	 */
	private Channel firstList,secondList;
	
	/**
	 * Timer delay for caluclation of frequency
	 * 
	 * TODO if headless will use dynamic delay,need to implement this
	 */
	private int delay = 30;
	
	/**
	 * instance of this class for server and timerTask
	 */
	private static ChannelCollector instance;
	
	
	private ChannelCollector(){
	}

	public static synchronized ChannelCollector getInstance(){
		if(instance == null){
			instance = new ChannelCollector();
		}
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

	/**
	 * Change the list to which broadcasts will be stored
	 * @param boolean list
	 */
	public void setMapToUse(boolean list) {
		first = list;
		if(list)
			firstList = new Channel();
		else
			secondList = new Channel();
	}
}