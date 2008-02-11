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
public class XMLDataSingle {
	String tagName;
	String tagValue;
	boolean isMaster;
	String[] atrName;
	String[] atrValue;
	String[] nextLevelNames;
	String[] nextLevelValues;
	private final static String attrNextAttr = "nextAttrToAdd";	
	private final static String treeAttrName = "tree";	
	public String searchAtr (String name) {
		for(int i=0;i<atrName.length;i++) if(name.compareTo(atrName[i])==0) return atrValue[i];
		return null;
	}
	public boolean singleProperties() {
		if(searchAtr(treeAttrName) != null) return false;
		//if(searchAtr(attrNextAttr) != null) return false;
		return true;
	}
	public boolean prepare() {
		return true;
	}


public String toString() {
	String ret;
		ret="";
			ret+="tagName="+tagName+ ";\n";
			ret+="tagValue="+tagValue+ ";\n";
			ret+="atrNameLen="+atrName.length+ ";\n";
			for (int j=0;j<atrName.length;j++) {
				ret+="\tatrName="+atrName[j]+ ";\n";
				ret+="\tatrValue="+atrValue[j]+ ";\n";
			}
			ret+="atrNextLevelValLen="+nextLevelValues.length+ ";\n";
			for (int j=0;j<nextLevelNames.length;j++) {
				ret+="\tnextLevelNames="+nextLevelNames[j]+ ";\n";
				ret+="\tnextLevelValues="+nextLevelValues[j]+ ";\n";
			}
	return ret;
	}
}
/*   Example 
   		<ResultRoot version="1.0.0" invokeid="1">
		<Result result="SUCCESS" status="inactive" name="SNLEXEC">
		<spName tree="branch" nextAttrToAdd="spName">sncExample</spName>
		<spName tree="branch" nextAttrToAdd="spName">sncGliu</spName>
		<spName tree="branch" nextAttrToAdd="spName">sncDemo</spName>
		</Result></ResultRoot>
		
tagName = spName
tagValues = {sncExample, sncGliu, sncDemo}
atrName = {tree,nextAttrToAdd}
atrValue= {branch,nextAttrToAdd}
nextLevelNames=nextLevelValues=null .

PS: nextLevelNames and nextLevelValues is not null for next more complicated request:
Here nextLevelNames=null or ENUM
nextLevelValues={null or {suspend,resume,singleStep} or {START,RAMP_UP,RAMP_DOWN}} :

		<ResultRoot version="1.0.0" invokeid="1">
		<Result result="SUCCESS" status="inactive" name="SNLEXEC">
		<threadName>sncDemo_1</threadName>
		<ssRunControl nextAttrToAdd="ssRunControl" valueChangeable="yes" isENUM="yes">
			<ENUM>suspend</ENUM>
			<ENUM>resume</ENUM>
			<ENUM>singleStep</ENUM>
		</ssRunControl>
		<dbgRunState>RUNNING</dbgRunState>
		<firstState>START</firstState>
		<currentState nextAttrToAdd="newStateName" valueChangeable="yes" isENUM="yes">
			RAMP_DOWN
			<ENUM>START</ENUM>
			<ENUM>RAMP_UP</ENUM>
			<ENUM>RAMP_DOWN</ENUM>
		</currentState>
		<previousState>RAMP_DOWN</previousState>
		<nextState>RAMP_DOWN</nextState>
		<Msg>Elapsed time since state was entered = 4.5 secondsQueued time delays:	delay[ 0]=5.000000</Msg>
		</Result></ResultRoot>
		
		And last most  complicated structure:
		
 */