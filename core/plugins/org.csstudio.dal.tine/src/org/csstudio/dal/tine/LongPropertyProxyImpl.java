/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.tine;

import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class LongPropertyProxyImpl extends PropertyProxyImpl<Long>{
	
	private int[] value;
	
	/**
	 * Constructs a new LongPropertyProxy.
	 * @param name
	 */
	public LongPropertyProxyImpl(String name, TINEPlug plug) {
		super(name, plug);
//		value = new long[(Integer)getCharacteristic("sequenceLength")];
		this.value = new int[1];
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
	 */
	@Override
	protected Long extractData(TDataType out) {
		out.getData(this.value);
		if (this.value != null && this.value.length > 0) {
			return new Long(this.value[0]);
		} else {
			return new Long(0);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getDataObject()
	 */
	@Override
	protected Object getDataObject() {
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#setDataToObject(java.lang.Object)
	 */
	@Override
	protected Object convertDataToObject(Long data) {
		return new int[]{data.intValue()};
	}
	/* (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
	 */
	@Override
	protected Class getNumericType() {
		return Long.class;
	}

}
