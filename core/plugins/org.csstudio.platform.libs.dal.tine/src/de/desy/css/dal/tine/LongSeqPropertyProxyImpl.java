package de.desy.css.dal.tine;

import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class LongSeqPropertyProxyImpl extends PropertyProxyImpl<long[]>{
	
	private long[] value;
	private int length;
	
	/**
	 * Constructs a new LongSeqPropertyProxy.
	 * @param name
	 */
	public LongSeqPropertyProxyImpl(String name) {
		super(name);
		length = (Integer)getCharacteristic("sequenceLength");
		value = new long[length];
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
	 */
	@Override
	protected long[] extractData(TDataType out) {
		out.getData(value);
		if (value != null)
			return value;
		else 
			return new long[length];
		
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
	protected Object setDataToObject(long[] data) {
		return data;
	}
	/* (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
	 */
	@Override
	protected Class getNumericType() {
		return Long.class;
	}

}
