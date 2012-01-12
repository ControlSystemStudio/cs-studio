package org.csstudio.dal.spi;

/**
 * Acts as top level property factory, delegates actual property creation to
 * appropriate property implementation.
 * 
 * 
 * @author ikriznar
 *
 */
public interface PropertyFactoryBroker extends PropertyFactory {
	
	public String getDefaultPlugType();
	
	public void setDefaultPlugType(String plugType);
	
	public String[] getSupportedPlugTypes();

}
