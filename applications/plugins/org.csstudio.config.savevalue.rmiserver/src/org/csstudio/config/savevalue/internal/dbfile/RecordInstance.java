/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.config.savevalue.internal.dbfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A record instance definition in an EPICS database file.
 * 
 * @author Joerg Rathlev
 */
public class RecordInstance {

	/**
	 * The record type.
	 */
	private String _type;
	
	/**
	 * The record name.
	 */
	private String _name;
	
	/**
	 * The fields of this record instance.
	 */
	private List<Field> _fields;

	/**
	 * Creates a new record instance definition.
	 * 
	 * @param type
	 *            the record type.
	 * @param name
	 *            the record name.
	 */
	public RecordInstance(final String type, final String name) {
		_type = type;
		_name = name;
		_fields = new ArrayList<Field>();
	}
	
	/**
	 * Returns the record type.
	 * 
	 * @return the record type.
	 */
	public final String getType() {
		return _type;
	}
	
	/**
	 * Returns the record name.
	 * 
	 * @return the record name.
	 */
	public final String getName() {
		return _name;
	}

	/**
	 * Adds a field to this record instance.
	 * 
	 * @param field
	 *            the field.
	 */
	public final void addField(final Field field) {
		_fields.add(field);
	}
	
	/**
	 * Returns an unmodifiable list containing the fields of this record.
	 * 
	 * @return an unmodifiable list containing the fields of this record.
	 */
	public final List<Field> getFields() {
		return Collections.unmodifiableList(_fields);
	}
}
