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

import java.util.Vector;

import org.csstudio.diag.IOCremoteManagement.ui.RichTable.typeOfCell;
import org.eclipse.swt.widgets.Group;

/**
 * @author Albert Kagarmanov
 *
 */

public class RichTablePrepare {
	XMLData data;
	int lengthX;
	int lengthY;
		
	String[][] stringDataArr;
	typeOfCell[][] cellsTypeArr;
	Object[][] extraDataArr ;
	String[] columnNameArr;
	
	String[][] realAttrName;
	String[][] realAttrVar;
	int[] realAttrDim;
	
	boolean isWarning;	
	Group propTable;
	final static String textChangeable="valueChangeable"; //nextAttrToAdd valueChangeable isENUM
	final static String dbNameStr="dbName";
	final static String msgNameStr="Msg";
	final static String parNameStr="Parameters";
	final static String nextAttrStr="nextAttrToAdd";
	final static String isEnumStr="isENUM";
	final static String isTree="tree";
	final static String isNextLevel="nextLevel";
	final static String[] columnDefault={"variable","value"};
	
	public RichTablePrepare(Group propTable,XMLData data) {this.propTable=propTable; this.data=data;}
	public boolean parser() {
		boolean isItLongTable = isItLongTableCalx();
		if (isItLongTable) prepareLongTable();
		else prepareRichTable();
		return true;	
	}
	private boolean isItLongTableCalx(){
		// <Index_0000 timestamp="2007-12-12 15:53:49.692" instance="RMT"  trace="rmt is -245 ticks left / cycle time = 20 ticks" />
		XMLDataSingle xml;
		if ((data.internalStatus.compareToIgnoreCase("locked") == 0)||(data.infoResult.compareToIgnoreCase("failed") == 0)||(data.internalStatus.compareToIgnoreCase("error") == 0)) {
			isWarning=true;
		}		
		for (int i=0;i<data.data.length;i++) {
			xml=data.data[i];
			
			if(xml==null)  return false;
			if(!xml.singleProperties()) return false;
			String str=xml.tagValue;
			if ((str != null)&&(str.length() > 0)  ){
				return false;
			}	
			for (int j=0;j<xml.atrValue.length;j++) {
				if((xml.atrName[j]).compareTo(textChangeable)==0) return false;
				if((xml.atrName[j]).compareTo(nextAttrStr)==0) return false;
				if((xml.atrName[j]).compareTo(isEnumStr)==0) return false;
				if((xml.atrName[j]).compareTo(isTree)==0) return false;
				if((xml.atrName[j]).compareTo(isNextLevel)==0) return false;
			}
		}
		return true;	
	}
	public boolean prepareLongTable() {
		// <Index_0000 timestamp="2007-12-12 15:53:49.692" instance="RMT"  trace="rmt is -245 ticks left / cycle time = 20 ticks" />
		int length = data.data.length;
		XMLDataSingle xml;
		lengthX=data.data[0].atrName.length+1;		
		lengthY=length;
		
		String[][] dataArr=new String[lengthX][lengthY];
		Object[][] extraData = new Object[lengthX][lengthY];
		typeOfCell[][] cellsType=new typeOfCell[lengthX][lengthY];
		String[] columnName=new String[lengthX];
		for (int i=0;i<columnDefault.length -1;i++) columnName[i]=columnDefault[i];
		int count=0;
		for (int i=0;i<length;i++) {
			xml=data.data[i];
			if(xml==null)  continue;
			if(!xml.singleProperties()) continue;
			for (int j=0;j<lengthX;j++) {
				cellsType[j][count]=typeOfCell.String;
				extraData[j][count]=null;
			}
			dataArr[0][count]=xml.tagName;
			for (int j=0;j<xml.atrValue.length;j++) {
				dataArr[1+j][count]=xml.atrValue[j];
				if(count==0)columnName[1+j]=xml.atrName[j];
			}
		count++;	
		}
		
		if (count != length) {
			lengthY=count;
		}

		stringDataArr=new String[lengthX][lengthY];
		extraDataArr = new Object[lengthX][lengthY];
		cellsTypeArr=new typeOfCell[lengthX][lengthY];
		columnNameArr=new String[lengthX];
		
		for(int i=0;i<lengthX;i++) {
			for(int j=0;j<lengthY;j++) {
				stringDataArr[i][j]=dataArr[i][j];
				extraDataArr[i][j]=extraData[i][j];
				cellsTypeArr[i][j]=cellsType[i][j];
			}
			columnNameArr[i]=columnName[i];
		}
		
		return true;
	}
	
	public boolean prepareRichTable() {
		//<type>int</type><value nextAttrToAdd="newVarValue" valueChangeable="yes">99</value>
		//<cur nextAttrToAdd="newStateName" valueChangeable="yes" isENUM="yes">RAMP_DOWN<ENUM>START</ENUM><ENUM>RAMP_UP</ENUM></cur>

		int length = data.data.length;
		XMLDataSingle xml;
		lengthX=2;		
		lengthY=length;		
		String[][] dataArr=new String[lengthX][lengthY];
		Object[][] extraData = new Object[lengthX][lengthY];
		typeOfCell[][] cellsType=new typeOfCell[lengthX][lengthY];
		String[] columnName=new String[lengthX];
		for (int i=0;i<columnDefault.length;i++) columnName[i]=columnDefault[i];
		int count=0;
		
		for (int i=0;i<length;i++) {
			xml=data.data[i];
			if(xml==null)  continue;
			if(!xml.singleProperties()) continue;
			if ( parNameStr.compareTo( xml.tagName) == 0 ) continue;
			for (int j=0;j<lengthX;j++) {
				cellsType[j][count]=typeOfCell.String;
				extraData[j][count]=null;
			}
			dataArr[0][count]=xml.tagName;
			dataArr[1][count]=xml.tagValue;
			
			int extLen=xml.nextLevelNames.length;

			if (extLen>0) {
				cellsType[1][count]=typeOfCell.Combo;
				String[] comboStr = new String[extLen];
				for (int j=0;j<extLen;j++) comboStr[j] = xml.nextLevelValues[j];
				extraData[1][count]= comboStr;
			} else if( dbNameStr.compareTo( xml.tagName) == 0 ) {
				cellsType[1][count]=typeOfCell.MB3_member; 
			} else if( msgNameStr.compareTo( xml.tagName) == 0 ) {
				cellsType[1][count]=typeOfCell.Message; 
			} else if (xml.atrValue.length > 0){
			for (int j=0;j<xml.atrValue.length;j++) {
				if( textChangeable.compareTo( xml.atrName[j]) == 0 ) {
					if (xml.atrValue[j].compareToIgnoreCase("yes") == 0) {
						cellsType[1][count]=typeOfCell.EditableText; 
						break;
						}
					}
				}
			} else {
				System.out.println("RichTablePrepare: brocken XML-schema");
			}
			count++;
		} // end of cycle for i
		if (count != length) {
			lengthY=count;
		}

		stringDataArr=new String[lengthX][lengthY];
		extraDataArr = new Object[lengthX][lengthY];
		cellsTypeArr=new typeOfCell[lengthX][lengthY];
		columnNameArr=new String[lengthX];
		
		for(int i=0;i<lengthX;i++) {
			for(int j=0;j<lengthY;j++) {
				stringDataArr[i][j]=dataArr[i][j];
				extraDataArr[i][j]=extraData[i][j];
				cellsTypeArr[i][j]=cellsType[i][j];
			}
			columnNameArr[i]=columnName[i];
		}
		return true;
	}

}