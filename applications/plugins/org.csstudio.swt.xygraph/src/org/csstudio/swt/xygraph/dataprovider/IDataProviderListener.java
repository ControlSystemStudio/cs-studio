package org.csstudio.swt.xygraph.dataprovider;

/**
 * A listener on data provider data change.
 * @author Xihui Chen
 *
 */
public interface IDataProviderListener {
	
	/**
	 * This method will be notified by data provider whenever the data changed in data provider
	 */
	public void dataChanged(IDataProvider dataProvider);
	
		
}
