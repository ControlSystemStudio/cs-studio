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
public class XMLData {
	String infoResult;
	String infoName;
	String infoStatus;
	String internalStatus;
	boolean isMaster;
	XMLDataSingle[] data;
public String searchAtr (String name,int index) {
	for(int i=0;i<data[index].atrName.length;i++) if(name.compareTo(data[index].atrName[i])==0) return data[index].atrValue[i];
	return null;
	}
public String toString() {
	String ret;
		ret="infoName="+infoName+ ";\n";
		ret+="infoResult="+infoResult+ ";\n";
		ret+="infoStatus="+infoStatus+ ";\n";
		ret+="infointernalStatus="+internalStatus+ ";\n";
		ret+="length="+data.length+ ";\n";
		for (int i=0;i<data.length;i++) {
			ret+="\ttagName="+data[i].tagName+ ";\n";
			ret+="\ttagValue="+data[i].tagValue+ ";\n";
			ret+="\tatrNameLen="+data[i].atrName.length+ ";\n";
			for (int j=0;j<data[i].atrName.length;j++) {
				ret+="\t\tdata[i].atrName="+data[i].atrName[j]+ ";\n";
				ret+="\t\tdata[i].atrValue="+data[i].atrValue[j]+ ";\n";
			}
			ret+="\tatrNextLevelValLen="+data[i].nextLevelValues.length+ ";\n";
			for (int j=0;j<data[i].nextLevelNames.length;j++) {
				ret+="\t\tdata[i].nextLevelNames="+data[i].nextLevelNames[j]+ ";\n";
				ret+="\t\tdata[i].nextLevelValues="+data[i].nextLevelValues[j]+ ";\n";
			}
		}
	return ret;
	}
}
