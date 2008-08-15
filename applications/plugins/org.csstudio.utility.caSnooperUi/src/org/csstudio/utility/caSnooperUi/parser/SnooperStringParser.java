package org.csstudio.utility.caSnooperUi.parser;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Packs/unpacks ArrayList<DataStructure> to string<br>
 * Following format is used for each DataStructure:<BR>
 *  repeats\n<BR>
 * 	clientAddres\n<BR>
 *  channelName\n<BR>
 * 	interval\n<BR>
 *  tableIt\n<BR>
 * 	<endClass>ChannelStructure</endClass><BR>
 *  
 * @author rkosir
 *
 */
public class SnooperStringParser {

	private ArrayList<ChannelStructure> parsedList = new ArrayList<ChannelStructure>();
	private ChannelStructure tmpStruct;
	private int repeats;
	private String clientAddress;
	private String channelName;
	private int interval;
	private int tableId;
	
	/**
	 * Converts the string to ArrayList<ChannelStructure>
	 * 
	 * @param String
	 * @return ArrayList<ChannelStructure>
	 */
	public ArrayList<ChannelStructure> unparse(String s){
		parsedList = new ArrayList<ChannelStructure>();
		String[] unpacked = s.split("<endClass>ChannelStructure</endClass>");
		for(int i=0;i<unpacked.length;i++){
			String[] object = unpacked[i].split("\n");
			if(object.length == 5){
				repeats = Integer.parseInt(object[0]);
				clientAddress = object[1];
				channelName = object[2];
				interval = Integer.parseInt(object[3]);
				tableId = Integer.parseInt(object[4]);
				tmpStruct = new ChannelStructure(repeats,clientAddress,channelName,interval,tableId);
				parsedList.add(tmpStruct);
			}
		}
		return parsedList;
	}
	
	/**
	 * Converts the ArrayList to String
	 * 
	 * @param ArrayList<ChannelStructure>
	 * @return String
	 */
	public String parse(ArrayList<ChannelStructure> list){
		StringBuilder sb = new StringBuilder();
		if(list!=null){
			for (Iterator<ChannelStructure> iterator = list.iterator(); iterator.hasNext();) {
				ChannelStructure channelStructure = (ChannelStructure) iterator
						.next();
				sb.append(channelStructure.getRepeats()+"\n");
				sb.append(channelStructure.getClientAddress()+"\n");
				sb.append(channelStructure.getAliasName()+"\n");
				sb.append(channelStructure.getInterval()+"\n");
				sb.append(channelStructure.getTableId()+"\n");
				sb.append("<endClass>ChannelStructure</endClass>");
			}
			return sb.toString();
		}
		return "";
	}
}
