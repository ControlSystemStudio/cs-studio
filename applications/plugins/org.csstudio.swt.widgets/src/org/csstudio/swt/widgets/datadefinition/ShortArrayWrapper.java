package org.csstudio.swt.widgets.datadefinition;

/**A wrapper for short[].
 * @author Xihui Chen
 *
 */
public class ShortArrayWrapper implements IPrimaryArrayWrapper {
	
	private short[] data;
	
	

	public ShortArrayWrapper(short[] data) {
		this.data = data;
	}

	public void setData(short[] data) {
		this.data = data;
	}
	
	public double get(int i) {
		return data[i];
	}

	public int getSize() {
		return data.length;
	}

}
