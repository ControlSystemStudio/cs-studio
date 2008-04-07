/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.ams.dbAccess.configdb;

import org.csstudio.ams.dbAccess.TObject;

/**
	iFilterRef			NUMBER(11) NOT NULL,
	iFilterActionRef	NUMBER(11) NOT NULL,
	iPos				NUMBER(11) NOT NULL,
*/
public class FilterFilterActionTObject extends TObject
{
	private static final long serialVersionUID = 8955192537972815624L;
	
	private int filterRef;
	private int filterActionRef;
	private int pos;
	
	public FilterFilterActionTObject()
	{
		this.filterRef = -1;
		this.filterActionRef = -1;
		this.pos = -1;
	}
	
	public FilterFilterActionTObject(int filterRef, int filterActionRef, int pos)
	{
		this.filterRef = filterRef;
		this.filterActionRef = filterActionRef;
		this.pos = pos;
	}

	////////// Getter- and Setter-Methods //////////

	public int getFilterActionRef() {
		return filterActionRef;
	}
	public void setFilterActionRef(int filterActionRef) {
		this.filterActionRef = filterActionRef;
	}

	public int getFilterRef() {
		return filterRef;
	}
	public void setFilterRef(int filterRef) {
		this.filterRef = filterRef;
	}

	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
}
