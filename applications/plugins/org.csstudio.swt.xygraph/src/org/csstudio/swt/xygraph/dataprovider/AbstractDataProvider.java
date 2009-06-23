package org.csstudio.swt.xygraph.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.linearscale.Range;

/**
 * This gives the most common implementation of the {@link IDataProvider} interface.
 * 
 * @author Xihui Chen
 *
 */
public abstract class AbstractDataProvider implements IDataProvider{


	protected boolean chronological = false;
	
	protected List<IDataProviderListener> listeners; 

	protected Range xDataMinMax = null;
	protected Range yDataMinMax = null;
	
	/**
	 * @param trace the trace which the data provider will provide data to.
	 * @param chronological true if the data is sorted chronologically on xAxis, 
	 * which means the data is sorted on X Axis.
	 */
	public AbstractDataProvider(boolean chronological) {
		this.chronological = chronological;
		listeners = new ArrayList<IDataProviderListener>();
	}
	

	
	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#getSize()
	 */
	public abstract int getSize();
	
	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#getSample(int)
	 */
	public abstract ISample getSample(int index);
	
	/**
	 * update xDataMinMax and yDataMinMax whenever data changed.
	 */
	protected abstract void innerUpdate();
	
	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#getXDataMinMax()
	 */
	public Range getXDataMinMax(){
		if(getSize() <=0)
			return null;
		else
			return xDataMinMax;
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#getYDataMinMax()
	 */
	public Range getYDataMinMax(){
		if(getSize() <=0)
			return null;
		else
			return yDataMinMax;
	}
	
	/**
	 * @param chronological the chronological to set
	 */
	public void setChronological(boolean chronological) {
		this.chronological = chronological;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#isChronological()
	 */
	public boolean isChronological() {
		return chronological;
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#addDataProviderListener(org.csstudio.sns.widgets.figureparts.IDataProviderListener)
	 */
	public void addDataProviderListener(final IDataProviderListener listener){
		if(listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.sns.widgets.figureparts.IDataProvider#removeDataProviderListener(org.csstudio.sns.widgets.figureparts.IDataProviderListener)
	 */
	public boolean removeDataProviderListener(final IDataProviderListener listener){
		return listeners.remove(listener);
	}
	
	protected void fireDataChange(){
		innerUpdate();
		for(IDataProviderListener listener : listeners){
			listener.dataChanged(this);
		}
	}
}
