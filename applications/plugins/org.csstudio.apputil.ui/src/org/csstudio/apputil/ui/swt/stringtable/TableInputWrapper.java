package org.csstudio.apputil.ui.swt.stringtable;

import java.util.List;

/**
 * The wrapper wraps the StringTableEditor's input. 
 * When the input changed, the wrapper won't change, which provides
 * the possibility that make every consumer know the changing of input. 
 * @author Xihui Chen
 *
 */
public class TableInputWrapper {

	private List<?> items;

	public TableInputWrapper() {
	}
	
	public TableInputWrapper(List<?> items) {
		this.items = items;
	}
	
		
	/**
	 * @return the items
	 */
	public List<?> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<?> items) {
		this.items = items;
	}
}
