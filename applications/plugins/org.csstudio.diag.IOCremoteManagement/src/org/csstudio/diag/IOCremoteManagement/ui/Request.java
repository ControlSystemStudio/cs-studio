/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.diag.IOCremoteManagement.ui;
/**
 * @author Albert Kagarmanov
 *
 */
public class Request {
	private final static boolean debug = false;
	private String DocumentStart;
	private String DocumentEnd=firstEnd;
	private String newParameterName=null;
	private String newParameterValue=null;
	private Request parent=null;
	//public boolean find =false;
//	private final static String firstStartSNL="<Root version=\"1.0.0\" invokeid=\"0\"><Command command=\"snlDebugger\" destination=\"SNLEXEC\"";
	private final static String firstStart="<Root version=\"1.0.0\" invokeid=\"0\"><Command";
	private final static String firstEnd="/></Root>";
	private final static String attrNext = "nextAttrToAdd";
    //private final static String closeDelimiter = "/>";
	private final static String closeDelimiter = "";
    private final static String blank = " ";
    private final static String equality = "=";
    private final static String apostrof = "\"";
    
    private final static String nextCommandAtr="nextCommand";
    private final static String nextCommandReq="command";
    private final static String nextDestinationAtr="nextDestination";
    private final static String nextDestinationReq="destination";
	
	public String  getDocumentStart()    {return DocumentStart;}
	public String  getDocumentEnd()      {return DocumentEnd;}
	public String  getNewParameterName() {return newParameterName;}
	public String  getNewParameterValue(){return newParameterValue;}
	public Request getParent()           {return parent;}  
	
	public void setDocumentStart(String s)    {DocumentStart=s;}
	public void setDocumentEnd(String s)      {DocumentEnd=s;}
	public void setNewParameterName(String s) {newParameterName=s;}
	public void setNewParameterValue(String s){newParameterValue=s;}
	public void setParent(Request r)          {parent=r;}  
	
	public String getDocument() {return DocumentStart+closeDelimiter+ DocumentEnd;}
	private String createStartDocument() {
		if (parent == null) return firstStart;
		String result=parent.getDocumentStart();
		result += (blank+ newParameterName + equality + apostrof  + newParameterValue +  apostrof); 
		return result;	
	}
	private String createStartDocument(String par) {
		String result=par;
		result += (blank+ newParameterName + equality + apostrof  + newParameterValue +  apostrof); 
		return result;	
	}
	
	private void RequestHelper(Request parent,String parameterName,String parameterValue,boolean replace,String labelNextCommand,String valueNextCommand) {
		this.parent=parent;
		newParameterName =parameterName;
		newParameterValue=parameterValue;
		DocumentStart=createStartDocumentWithReplace(labelNextCommand,valueNextCommand);
		
	}
	private void RequestHelper(Request parent,String parameterName,String parameterValue) {
		this.parent=parent;
		newParameterName =parameterName;
		newParameterValue=parameterValue;
		DocumentStart=createStartDocument();
	}
	
	private String createStartDocumentWithReplace(String parameterName,String parameterValue) {
		String parDoc=parent.getDocumentStart();
		String answer=replace(parDoc,nextCommandReq,parameterValue);
		if(debug) System.out.println("createStartDocumentWithReplace parName="+parameterName+" parVal="+parameterValue);
		if(debug) System.out.println("createStartDocumentWithReplace answer="+answer);
		return  createStartDocument(answer);
	}
	
	
	private void RequestHelperReplace(Request parent,String nextCommand,String nextDestination){
		this.parent=parent;
		if (parent == null) {
				System.out.println("!!!RequestHelperReplace parent is null");
				DocumentStart=firstStart;
				DocumentStart+=(blank+nextCommandReq+equality+apostrof+nextCommand+apostrof+blank+nextDestinationReq+equality+apostrof+nextDestination+apostrof);
				return ;
		}
		String doc=parent.getDocument();
		String firstDoc=firstStart+closeDelimiter+ DocumentEnd;
		if (debug){ 
			System.out.println("firstStart="+firstStart);
			System.out.println("parent.getDocument()="+doc);
			System.out.println("firstDoc="+firstDoc);
		}
		if (doc.compareTo(firstDoc)==0 ) {
			if (debug) System.out.println("case 0");
			DocumentStart=firstStart;
			DocumentStart+=(blank+nextCommandReq+equality+apostrof+nextCommand+apostrof+blank+nextDestinationReq+equality+apostrof+nextDestination+apostrof);
			if (debug)System.out.println("case 0 DocumentStart"+DocumentStart);
		} else {
			String answer=null;
			if (debug) System.out.println("case 1");	
			DocumentStart=parent.getDocumentStart();
			if(nextCommand != null)answer=replace(DocumentStart,nextCommandReq,nextCommand);
			if(nextDestination != null)answer=replace(answer,nextDestinationReq,nextDestination);
			if(answer==null) {
				System.out.println("!!!Request:RequestHelperReplace() answer is null");//TODO
				answer=firstStart+closeDelimiter+ DocumentEnd;
			}
			DocumentStart=answer;
		}
	}
	
	private String replace(String text,String par,String val) {
		int pos;
		if ( (pos=text.indexOf(par)) < 0) {
			System.out.println("!!!Request:replace() pos is negative");//TODO
			return text;
		}
		
		String part = text.substring(pos+par.length()+2);
		int apostrofPos;
		if (debug) System.out.println("part="+part+";");
		if ( (apostrofPos=part.indexOf(apostrof))<0 ) {
			System.out.println("!!!Request:replace() apostrofPos=part is negative");//TODO
			return text;
		}
		if (debug) System.out.println("pos="+apostrofPos);
		String regexStr=part.substring(0,apostrofPos);
		if (debug) System.out.println("regexStr="+regexStr);
		String answer=text.replaceFirst(regexStr, val);
		if (debug) System.out.println("answer="+answer);
	return answer;
	}
	
	
	public Request() {
		DocumentStart=firstStart;
	}
	public Request(Request parent,String parameterName,String parameterValue) {
		RequestHelper(parent,parameterName,parameterValue);
	}
	
	public Request(Request parent,XMLDataSingle data) {
		boolean findAdd =false;
		boolean findReplace =false;
		this.parent=parent;
		String  nameForAttrNext=null;
		String valueForAttrNext=null;
		
		String  valueNextCommand=null;
		String  labelNextCommand=null;
		String valueNextDestination=null;
		
		for (int i =0;i<data.atrName.length;i++) {
			if (attrNext.compareTo(data.atrName[i]) == 0) {
				findAdd=true;
				nameForAttrNext=data.atrValue[i];
				valueForAttrNext=data.tagValue;
				if(debug) System.out.println("nameForAttrNext="+nameForAttrNext+" valueForAttrNext="+valueForAttrNext );
			} else 	if (nextCommandAtr.compareTo(data.atrName[i]) == 0) {
					findReplace=true;
					valueNextCommand=data.atrValue[i];
					labelNextCommand=data.atrName[i];
					if(debug) System.out.println("valueNextCommand="+valueNextCommand );
			} else if (nextDestinationAtr.compareTo(data.atrName[i]) == 0) {
				findReplace=true;
				valueNextDestination=data.atrValue[i];
				if(debug) System.out.println("valueNextDestination="+valueNextDestination );
			}	//if-else 
		} // for
		
		if (findAdd) {
			if (!findReplace) RequestHelper(parent,nameForAttrNext,valueForAttrNext);
			else {
				RequestHelper(parent,nameForAttrNext,valueForAttrNext,findReplace,labelNextCommand,valueNextCommand);
			}
		}else if (findReplace) {
			RequestHelperReplace(parent,valueNextCommand,valueNextDestination);
		} else {
			System.out.println("!!! bad XML request ");
		}
	}
}