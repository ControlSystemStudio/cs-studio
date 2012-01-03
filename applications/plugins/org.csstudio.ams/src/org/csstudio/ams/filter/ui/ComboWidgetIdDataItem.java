
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
 /**
 * 
 */

package org.csstudio.ams.filter.ui;

import org.csstudio.ams.dbAccess.ItemInterface;
import org.eclipse.swt.widgets.Combo;

/**
 * Represents a id marked member of a data-list of a combo widget.
 * 
 * Use instances of this type as content of the data list added to combo widget 
 * ({@link Combo#setData(Object)}) to add ids to items.
 */
@SuppressWarnings("hiding")
public class ComboWidgetIdDataItem implements ItemInterface {
	private final int id;
	private final String text;

	/**
	 * Creates an instance with given id and String-representation.
	 * 
	 * @param id A id value -- duplicates possible.
	 * @param text The String representation.
	 */
    public ComboWidgetIdDataItem(int id, String text) {
		this.id = id;
		this.text = text;
	}

	/**
	 * Gets the id associated with this instance.
	 */
	@Override
    public int getID() {
		return id;
	}

	/**
	 * Gets the String associated with this instance.
	 */
	@Override
    public String toString() {
		return text == null ? "" : text;
	}
}