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

import org.csstudio.diag.IOCremoteManagement.ui.RichTable.typeOfCell;
import org.eclipse.swt.widgets.Group;

/**
 * @author Albert Kagarmanov
 *
 */

public class ParametersTablePrepare {
	XMLData data;
	int lengthX=2;
	int lengthY=0;
	String[][] dataArr;
	String[] columnNameArr;
	Group propTable;
	public ParametersTablePrepare(Group propTable,XMLData data) {this.propTable=propTable; this.data=data;}
	public boolean parser() {
		final String parNameStr="Parameters";
		int length = data.data.length;
		XMLDataSingle xml;
		String[] columnDefault={"parName","parValue"};
		columnNameArr = columnDefault;
		for (int i=0;i<length;i++) {
			xml=data.data[i];
			if(xml==null)  continue;
			if(!xml.singleProperties()) continue;
			int extLen=xml.nextLevelNames.length;
			if ( parNameStr.compareTo( xml.tagName) == 0 ) {
				if (extLen <1) {
					System.out.println("ParametersTablePrepare: Error XML");
					//org.csstudio.diag.IOCremoteManagement.Activator.errorPrint ("result is OK");
					continue;
				}
				lengthY=extLen;
				dataArr=new String[lengthX][lengthY];
				for (int j=0;j<lengthY;j++) {
					dataArr[0][j]=xml.nextLevelNames[j];
					dataArr[1][j]=xml.nextLevelValues[j];
				}
			} 			
		} // end of cycle for i
		return true;
	}
}