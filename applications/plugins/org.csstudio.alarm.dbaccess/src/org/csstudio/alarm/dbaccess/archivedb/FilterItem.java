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
package org.csstudio.alarm.dbaccess.archivedb;

/**
 * Holds the setting for one filter condition of the archive search (Property,
 * value, AND /OR relation). The data is set by the 'ExpertSearchDialog'.
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 14.05.2008
 */
public class FilterItem {

	private String _property;

	private String _value;

	private String _relation;

	public FilterItem(final FilterItem item) {
	    _property = item.getProperty();
	    _value = item.getOriginalValue();
	    _relation = item.getRelation();
	}

	public FilterItem(final String property, final String value, final String relation) {
		_property = property;
		_value = value;
		_relation = relation;
	}

	public FilterItem() {
	}

	public String getProperty() {
		return _property.toUpperCase();
	}

	public void set_property(final String property) {
		_property = property;
	}

	/**
	 * Get original (not converted) value entered by user.
	 * @return
	 */
	public String getOriginalValue() {
		return _value;
	}

	/**
	 * Get prepared value for SQL. ('*' is replaced by '%'
	 * and '?' is replaced by '_').
	 * @return
	 */
	public String getConvertedValue() {
		return _value.replace("*", "%").replace("?", "_");
	}

	public void setValue(final String value) {
		_value = value;
	}

	public String getRelation() {
		return _relation;
	}

	public void setRelation(final String relation) {
		_relation = relation;
	}

    /**
     * Check weather the THIS filter item has the same setting as the given one.
     */
    public boolean compare(final FilterItem localItem) {
        if (localItem.getOriginalValue().equals(_value) == false) {
            return false;
        }
        if (localItem.getProperty().equals(_property) == false) {
            return false;
        }
        if (localItem.getRelation().equals(_relation) == false) {
            return false;
        }
        return true;
    }
}
