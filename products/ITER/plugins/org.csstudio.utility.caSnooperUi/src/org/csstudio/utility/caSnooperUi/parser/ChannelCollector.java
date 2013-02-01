package org.csstudio.utility.caSnooperUi.parser;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author rkosir
 *
 */
public class ChannelCollector implements Runnable{
	
	private SnooperServer server;
	private boolean started,first;
	private Timer timer = new Timer();
	private Channel firstList,secondList;
	private boolean dataReady;
	private int delay;
	
	public ChannelCollector(){
		server = new SnooperServer();
		Thread t = new Thread(new Runnable(){
			public void run() {
				server.execute();
			}
		});
		t.start();
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
		else
			if(secondList!=null){
				secondList.addChannel(aliasName, clientAddress, delay);
			}
		
	}
	
	/**
	 * Returns snoops received as ArrayList<DataStructure>
	 * 
	 * @return Arraylist of received snoops
	 */
	public ArrayList<ChannelStructure> getSnoops(){
		dataReady = false;
		if(first)
			return secondList.getData();
		else 
			return firstList.getData();
	}

	public boolean isReady() {
		return dataReady ;
	}

	/**
	 * Start the snooping with given interval
	 * 
	 * @param time the time interval
	 */
	public void start(int time){
		this.delay = time;
		started = false;
		first = false;
		dataReady=false;
		server.addListener(this);
		timer = new Timer();
		timer.scheduleAtFixedRate(new captureData(), 0, time * 1000);
	}
	
	/**
	 * Stops the snooping
	 */
	public void stop() {
		timer.cancel();
		server.resetListener();
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

	public void run() {
		server.execute();
	}
	
	/**
	 * Timer class for changing between maps
	 *
	 */
	private class captureData extends TimerTask {
		
		@Override
		public void run() {
			if(!started){
				if(first){
					first = false;
					secondList = new Channel();
					dataReady = true;
				}
				else{
					firstList = new Channel();
					first = true;
					started = true;
					firstList = new Channel();
				}
			}else
				if(first){
					first = false;
					secondList = new Channel();
					if(firstList!=null)
						dataReady = true;
				}
				else{
					first = true;
					firstList = new Channel();
					if(secondList!=null)
						dataReady = true;
				}
		}
	}
}