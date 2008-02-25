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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
public class Parsing {
	final static boolean debug=false;
	final static boolean debugFull=false;
	final static boolean debugStatus=false;
	final static boolean debugEnum=false;
	final static String debugBrunch = "SNLDebugger";
	final static String rootString = "Result";	
	final static String rootPropertyResult = "Result";
	final static String infoName = "NAME";
	final static String infoStatus = "STATUS";
	final static String SNLDebuggerStatus = "status";
	final static String okString = "OK";
	private String wholeXML;
	public Parsing(String text) {
		wholeXML=text;
	}
	public XMLData Parse() {
		if (debug)  System.out.println("!!!! start");
		XMLData data=parserAnswer(); //TODO errCode
		if (debug)  System.out.println("!!!! end");
		return data;
	}

	private XMLData parserAnswer() {
		if(wholeXML.length() < 5) {
			org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("SNLdebugger Error:  bad parserAnswer wholeXML is small");
			System.out.println("SNLdebugger Error:  bad parserAnswer wholeXML is small ='" + wholeXML+"'");
			return null;		
		}
		if (debug) System.out.println("SNLdebugger:parserAnswer(): wholeXML is ='" + wholeXML+"'");

		List<String> input = new ArrayList<String>();
		final SAXBuilder saxb = new SAXBuilder(false);
		Document xmlDoc;
		XMLData data = null;
		XMLattrValue[] atr =null;
		try {
			xmlDoc = saxb.build(new StringReader(wholeXML));
			Element root = xmlDoc.getRootElement();
			final List rootList = root.getChildren(rootString);
			if(rootList.size()==0) {
				System.out.println("SNLdebugger Error:");//TODO
				return null;
			}
			final Iterator rootIterator = rootList.iterator();
			String str;
			
			final Element levelOne = (Element) rootIterator.next(); // <Result>
			if ((atr= findAllAtributes(levelOne))==null) return null;
			data = new XMLData();
			data.infoResult="UNDEF";
			data.infoName="UNDEF";
			data.infoStatus="UNDEF";
			data.internalStatus=okString;
			data.isMaster =false;
			if (debugStatus) System.out.println("parserAnswer init");
			if (atr.length <3) {
				System.out.println("SNLdebugger:parserAnswer(): strange Result attribute");
			}else {
				for (int i=0;i<atr.length;i++)  { //result="SUCCESS" status="MASTER" name="RMT"
					if ( atr[i].attribute.compareToIgnoreCase("result") == 0) data.infoResult=atr[i].value;
					if ( atr[i].attribute.compareToIgnoreCase("internalstatus") == 0) data.internalStatus=atr[i].value;
					if ( atr[i].attribute.compareToIgnoreCase("name") == 0)   data.infoName=atr[i].value;
					if ( atr[i].attribute.compareToIgnoreCase("status") == 0) {
						data.infoStatus=atr[i].value;
						if ((data.infoStatus.compareToIgnoreCase("master") == 0)||(data.infoStatus.compareToIgnoreCase("active") == 0))  {
							data.isMaster=true;
							if (debugStatus) System.out.println("parserAnswer isMaster=true");
					} else {
						if (debugStatus) System.out.println("parserAnswer isMaster=false");
					}
				  }				
				}
			}
			
			final List levelOneList = levelOne.getChildren();
			
			int len = levelOneList.size();
			if(len==0) {
				System.out.println("SNLdebugger Error:levelOneList:size"); //TODO
				return null;
			}
			XMLDataSingle[] dataIterator = new  XMLDataSingle[len] ;
			data.data=dataIterator;
			final Iterator levelOneIterator = levelOneList.iterator();
			int count =0;
			while(levelOneIterator.hasNext()){
				final Element elm = (Element) levelOneIterator.next();
				//
				// Resolve Name and Value:
				//
				dataIterator[count] = new XMLDataSingle();
				dataIterator[count].isMaster=data.isMaster;
				dataIterator[count].tagName=elm.getName();
				dataIterator[count].tagValue=elm.getTextNormalize();
				if (debug) System.out.println("tagName ="+dataIterator[count].tagName);
				if (debug) System.out.println("tagVAlue="+dataIterator[count].tagValue);
				//
				// Resolve Attribute List of Name and Value:
				//
				final List par = elm.getAttributes();
				if(par.size()==0) {
					if (debug)  System.out.println("SNLdebugger no param: all is default");//TODO
					//return null;
					dataIterator[count].atrName  = new String[1];
					dataIterator[count].atrValue = new String[1];
					dataIterator[count].atrName[0]="nextLevel"; //TODO
					dataIterator[count].atrValue[0]="no";
				} else {
					dataIterator[count].atrName  = new String[par.size()];
					dataIterator[count].atrValue = new String[par.size()];
					if (debug) System.out.println("par.size="+par.size()+" elm.getAt="+par.toString()+"\n");
					for (int i=0;i<par.size();i++){
						Attribute e = (Attribute) par.get(i);
						dataIterator[count].atrName [i]=e.getName();
						dataIterator[count].atrValue[i]=e.getValue();
					}
				}
				//
				// Resolve Next level Name and Value and ENUM:
				//
				final List next = elm.getChildren();
				dataIterator[count].nextLevelNames  = new String[next.size()];
				dataIterator[count].nextLevelValues = new String[next.size()];
				dataIterator[count].nextValueEnum=null;
				for (int i=0;i<next.size();i++){
					Element e = (Element) next.get(i);
					dataIterator[count].nextLevelNames [i]=e.getName();
					dataIterator[count].nextLevelValues[i]=e.getTextNormalize();
					if (debugFull) System.out.println(" nextLevel="+dataIterator[count].nextLevelNames [i]+":"+dataIterator[count].nextLevelValues[i]);
					final List nextEnum = e.getChildren();
					dataIterator[count].nextValueEnum  = new String[nextEnum.size()];
					for (int j=0;j<nextEnum.size();j++){
						Element enumEl = (Element) nextEnum.get(j);
						dataIterator[count].nextValueEnum[j]=enumEl.getTextNormalize();
						if (debugEnum) System.out.println(" nextEnum="+dataIterator[count].nextValueEnum[j]+":"+enumEl.getName());
					}
				}
				count++;
			}
	 
		} catch (final JDOMException e1) {
			org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("SNLdebugger: View.ExceptionJDOM",e1.getMessage());
			return null;
		} catch (final IOException e1) {
			org.csstudio.diag.IOCremoteManagement.Activator.errorPrint("View.ExceptionIO", e1.getMessage()); 
			return null;
		}
		
		if (debugFull) {
			System.out.println("data.infoName="+data.infoName+ ";");
			System.out.println("data.infoResult="+data.infoResult+ ";");
			System.out.println("data.infoStatus="+data.infoStatus+ ";");
			System.out.println("data.infointernalStatus="+data.internalStatus+ ";");
			System.out.println("data.length="+data.data.length+ ";");
			for (int i=0;i<data.data.length;i++) {
				System.out.println("\tdata.data.tagName="+data.data[i].tagName+ ";");
				System.out.println("\tdata.data.tagValue="+data.data[i].tagValue+ ";");
				System.out.println("\tdata.data.atrNameLen="+data.data[i].atrName.length+ ";");
				for (int j=0;j<data.data[i].atrName.length;j++) {
					System.out.println("\t\tdata.data[i].atrName="+data.data[i].atrName[j]+ ";");
					System.out.println("\t\tdata.data[i].atrValue="+data.data[i].atrValue[j]+ ";");
				}
				System.out.println("\tdata.data.atrNextLevelValLen="+data.data[i].nextLevelValues.length+ ";");
				for (int j=0;j<data.data[i].nextLevelNames.length;j++) {
					System.out.println("\t\tdata.data[i].nextLevelNames="+data.data[i].nextLevelNames[j]+ ";");
					System.out.println("\t\tdata.data[i].nextLevelValues="+data.data[i].nextLevelValues[j]+ ";");
				}
			}
			System.out.println("OK");
		}
		
		return data;
	}
	XMLattrValue[] findAllAtributes(Element element) {
		final List list = element.getAttributes();
		int len;
		if ( (len=list.size())==0) {
			System.out.println("SNLdebugger Error:findAllAtributes:size"); //TODO
			return null;
		}  
		XMLattrValue[] ret = new XMLattrValue[len];
		Attribute att;
		for(int i=0;i<len;i++) {
			if ((att= (Attribute) list.get(i)) == null) {
				System.out.println("SNLdebugger Error:findAllAtributes:index"); //TODO
				return null;
			}
			ret[i] = new XMLattrValue();
			ret[i].value=att.getValue();
			ret[i].attribute=att.getName();			
			if ((ret[i].value==null)||(ret[i].attribute==null)) {
				System.out.println("SNLdebugger Error:findAllAtributes:nullString"); //TODO
				return null;
			}	
		}
		return ret;
	}
	XMLattrValue[] findAllAtributesChild(Element root,String childName) {
		final List child = root.getChildren(childName);
		if(child.size()==0) {
			System.out.println("SNLdebugger Error:findAllAtributesChild:size"); //TODO
			return null;
		}
		final Iterator childI = child.iterator();		
		final Element element = (Element) childI.next();
		return findAllAtributes(element);
	}
	
	String findTextChild(Element root,String childName) {
		final List child = root.getChildren(childName);
		if(child.size()==0) {
			System.out.println("SNLdebugger Error:findTextChild:size"); //TODO
			return null;
		}
		final Iterator childI = child.iterator();		
		final Element element = (Element) childI.next();
		String str = element.getTextNormalize();
		return str;
	}
	
	
}

class XMLattrValue {
	public String attribute;
	public String value;
}