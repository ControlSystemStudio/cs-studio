package org.csstudio.swt.widgets.datadefinition;

/**A wrapper for long[].
 * @author Xihui Chen
 *
 */
public class LongArrayWrapper implements IPrimaryArrayWrapper {
	
	private long[] data;
	
	

	public LongArrayWrapper(long[] data) {
		this.data = data;
	}

	public void setData(long[] data) {
		this.data = data;
	}
	
	public double get(int i) {
		return data[i];
	}

	public int getSize() {
		return data.length;
	}

}
