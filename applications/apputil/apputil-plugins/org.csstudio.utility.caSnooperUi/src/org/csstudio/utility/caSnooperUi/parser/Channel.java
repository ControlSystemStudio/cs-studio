package org.csstudio.utility.caSnooperUi.parser;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Structure for received broadcast message
 * 
 * @author rkosir
 *
 */
public class Channel{
	
	private ChannelStructure tmpStructure;
	/**
	 * Data storage for messages
	 */
	private HashMap<String, ChannelStructure> map;
	
	/**
	 * Returns list of all hostnames and PVs sorted by highest frequency
	 * @return
	 */
	public ArrayList<ChannelStructure> getData(){
		ArrayList<ChannelStructure> list = new ArrayList<ChannelStructure>();
		if(map != null){
			Set<String> keys = map.keySet();
			Iterator<String> keyIter = keys.iterator();
			while (keyIter.hasNext()) {
		         list.add(map.get(keyIter.next()));
		    }
			Collections.sort(list,new Comparer());
			for(int i=0;i<list.size();i++)
				list.get(i).setId(i+1);
			return list;
		}
		return null;
	}

	/**
	 * Adds a new channel to the list or increases the frequency of the channel
	 * 
	 * @param aliasName
	 * @param clientAddress
	 * @param interval
	 */
	public void addChannel(String aliasName, InetSocketAddress clientAddress,int interval){
		String key = clientAddress.getHostName()+":"+clientAddress.getPort()+aliasName;
		//create new map if this is first received channel
		if(map == null){
			map = new HashMap<String,ChannelStructure>();
			tmpStructure = new ChannelStructure(clientAddress,aliasName,interval);
			map.put(key,tmpStructure);
		}else{
			//check if channel already exits,if not create it
			//if it does, update repeats
			tmpStructure = map.get(key);
			if (tmpStructure == null){
				tmpStructure = new ChannelStructure(clientAddress,aliasName,interval);
				map.put(key,tmpStructure);
			}else{
				tmpStructure.updateRepeats();
			}
		}
	}
}