package de.desy.css.dal.tine;

import de.desy.tine.dataUtils.TDataType;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class DoublePropertyProxyImpl extends PropertyProxyImpl<Double>{
	
	private double[] value;
	
	/**
	 * Constructs a new DoublePropertyProxy.
	 * @param name
	 */
	public DoublePropertyProxyImpl(String name) {
		super(name);
		value = new double[(Integer)getCharacteristic("sequenceLength")];
	}

	/*
	 * (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#extractData(de.desy.tine.dataUtils.TDataType)
	 */
	@Override
	protected Double extractData(TDataType out) {
		out.getData(value);
		if (value != null && value.length > 0)
			return value[0];
		else 
			return Double.NaN;
		
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
	protected Object setDataToObject(Double data) {
		return new double[]{data};
	}
	
	/* (non-Javadoc)
	 * @see de.desy.css.dal.tine.PropertyProxyImpl#getNumericType()
	 */
	@Override
	protected Class getNumericType() {
		return Double.class;
	}

}
