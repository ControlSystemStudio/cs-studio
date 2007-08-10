package de.desy.css.dal.tine;

import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class DoubleSeqPropertyProxyImpl extends PropertyProxyImpl<double[]>{
	
	private double[] value;
	private int length;
	
	/**
	 * Constructs a new DoubleSeqPropertyProxy.
	 * @param name
	 */
	public DoubleSeqPropertyProxyImpl(String name) {
		super(name);
		length = (Integer)getCharacteristic("sequenceLength");
		value = new double[length];
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
	 */
	@Override
	protected double[] extractData(TDataType out) {
		out.getData(value);
		if (value != null)
			return value;
		else 
			return new double[length];
		
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
	protected Object setDataToObject(double[] data) {
		return data;
	}
	/* (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
	 */
	@Override
	protected Class getNumericType() {
		return Double.class;
	}

}
