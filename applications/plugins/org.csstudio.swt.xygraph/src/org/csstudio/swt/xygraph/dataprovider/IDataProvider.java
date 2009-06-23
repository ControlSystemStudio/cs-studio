package org.csstudio.swt.xygraph.dataprovider;

import org.csstudio.swt.xygraph.linearscale.Range;

/**
 * Interface for the data provider of trace. This gives the possibilities to implement 
 * different data provider, which could have different data source or data storage structure.
 * For example: the data source could be from user input, database, files, etc,. 
 * The storage structure could be array, queue, circular buffer, bucket buffer, etc,. 
 * @author Xihui Chen
 *
 */
public interface IDataProvider {

	/**Total number of samples.
	 * @return the size.
	 */
	public int getSize();

	/**Get sample by index;
	 * @param index
	 * @return the sample. null if the index is out of range.
	 */
	public ISample getSample(int index);

	/**Get the minimum and maximum xdata.
	 * @return a range includes the min and max as lower and upper. 
	 * return null if there is no data.
	 */
	public Range getXDataMinMax();

	/**Get the minimum and maximum ydata.
	 * @return a range includes the min and max as lower and upper.
	 * return null if there is no data.
	 */
	public Range getYDataMinMax();

	/**
	 * @return true if data is ascending sorted on X axis; false otherwise 
	 */
	public boolean isChronological();

	public void addDataProviderListener(
			final IDataProviderListener listener);

	public boolean removeDataProviderListener(
			final IDataProviderListener listener);	

}