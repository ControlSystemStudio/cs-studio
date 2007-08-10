package de.desy.css.dal.tine;

import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class LongPropertyProxyImpl extends PropertyProxyImpl<Long>{
	
	private long[] value;
	
	/**
	 * Constructs a new LongPropertyProxy.
	 * @param name
	 */
	public LongPropertyProxyImpl(String name) {
		super(name);
		value = new long[(Integer)getCharacteristic("sequenceLength")];
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
	 */
	@Override
	protected Long extractData(TDataType out) {
		out.getData(value);
		if (value != null && value.length > 0)
			return value[0];
		else 
			return new Long(0);
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getDataObject()
	 */
	@Override
	protected Object getDataObject() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#setDataToObject(java.lang.Object)
	 */
	@Override
	protected Object setDataToObject(Long data) {
		return new Long[]{data};
	}
	/* (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
	 */
	@Override
	protected Class getNumericType() {
		return Long.class;
	}

}
