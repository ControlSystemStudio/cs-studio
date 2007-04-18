package org.csstudio.alarm.treeView.cacher;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LDAPTreeParser implements ITreeParser{

	public List<String> getAncestors(String name)
	{
		List<String> dum = new ArrayList<String>();
		//ok we parse the name and get ancestors
		StringTokenizer stoken = new StringTokenizer(name,",");
		String token;
		int pos;
		//name is probably something after first=
		while (stoken.hasMoreTokens()){
			token = stoken.nextToken();
			pos = token.indexOf("=");
			dum.add(token.substring(pos+1));
		}
		return dum;
	}
	
	public String specialClean(String toClean){
		StringBuffer tc = new StringBuffer(toClean);
		String grr = ""+'"';
		int pos = tc.indexOf(grr);
		while (pos>-1){
			tc.deleteCharAt(pos);
			pos = tc.indexOf(grr);
		}
		return tc.toString();
	}
	
	public String getParent(String name){
		String dum = name;
		int pos=dum.indexOf(",");
		if (pos==-1) return null;
		return getMyName(dum.substring(pos+1));		
	}
	
	public String getMyName(String name){
		int pos1 = name.indexOf("=");
		int pos2= name.indexOf(",");
		if (pos2 ==-1 ) {pos2=name.length();} //if comma is not present, we must take last character
		return name.substring(pos1+1,pos2);
	}
	
	public String getParentRName(String name){
		String dum = name;
		if (dum ==null) {dum ="";}
		int pos=dum.indexOf(",");
		if (pos==-1) return null;
		return dum.substring(pos+1);
	}
}