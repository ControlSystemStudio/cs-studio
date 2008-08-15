package org.csstudio.utility.caSnooper.ui.ChangeView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.csstudio.utility.caSnooper.parser.ChannelStructure;
/**
 * Packs/unpacks ArrayList<DataStructure> to string
 * Following format is used for each DataStructure:
 *  repeats\n
 * 	clientAddres\n";
 *  channelName\n";
 * 	interval\n";
 *  tableIt\n
 * 	<endClass>ChannelStructure</endClass>
 *  
 * @author rkosir
 *
 */
public class SnooperXmlParser {

	private ArrayList<ChannelStructure> parsedList = new ArrayList<ChannelStructure>();
	private ChannelStructure tmpStruct;
	private int repeats;
	private String clientAddress;
	private String channelName;
	private int interval;
	private int tableId;
	
	/**
	 * unparse the string to ArrayList<ChannelStructure>
	 * @param s
	 * @return
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
	
	public String parse(ArrayList<ChannelStructure> list){
		String tmpString = "";
		if(list!=null){
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ChannelStructure channelStructure = (ChannelStructure) iterator
						.next();
				tmpString = tmpString+channelStructure.getRepeats()+"\n";
			}
		}
		
		return "";
	}
}
