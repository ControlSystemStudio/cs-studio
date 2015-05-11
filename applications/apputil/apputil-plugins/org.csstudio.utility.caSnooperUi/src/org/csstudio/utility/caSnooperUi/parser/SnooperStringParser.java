package org.csstudio.utility.caSnooperUi.parser;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Packs/unpacks ArrayList<DataStructure> to string<br>
 * Following format is used for each DataStructure:<BR>
 *  <statistics>statistic data</statistics>\n<br>
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
	 * Converts the string to Object[2]={String,ArrayList<ChannelStructure>}
	 * 
	 * @param String
	 * @return Object[]
	 */
	public Object[] unparse(String s){
		parsedList = new ArrayList<ChannelStructure>();
		String[] statistics = s.split("</statistics>\n");
		if(statistics.length == 2){
			String[] unpacked = statistics[1].split("<endClass>ChannelStructure</endClass>\n");
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
		}
		return new Object[]{statistics[0].replace("<statistics>","Remote snooping results"),parsedList};
	}
	
	/**
	 * Converts the ArrayList to String
	 * @param string 
	 * 
	 * @param ArrayList<ChannelStructure>
	 * @return String
	 */
	public String parse(String string, ArrayList<ChannelStructure> list){
		StringBuilder sb = new StringBuilder();
		if(string.length()>0)
			sb.append("<statistics>"+string+"</statistics>\n");
		if(list!=null){
			for (Iterator<ChannelStructure> iterator = list.iterator(); iterator.hasNext();) {
				ChannelStructure channelStructure = (ChannelStructure) iterator
						.next();
				sb.append(channelStructure.getRepeats()+"\n");
				sb.append(channelStructure.getClientAddress()+"\n");
				sb.append(channelStructure.getAliasName()+"\n");
				sb.append(channelStructure.getInterval()+"\n");
				sb.append(channelStructure.getTableId()+"\n");
				sb.append("<endClass>ChannelStructure</endClass>\n");
			}
			return sb.toString();
		}
		return "";
	}
}