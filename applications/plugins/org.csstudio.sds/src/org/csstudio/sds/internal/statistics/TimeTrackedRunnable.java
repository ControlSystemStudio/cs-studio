package org.csstudio.sds.internal.statistics;

/**
 * A Runnable implementation which tracks its execution time and reports it to a
 * statistical service.
 * 
 * @author Sven Wende
 * 
 */
public abstract class TimeTrackedRunnable implements Runnable {
	
	/**
	 * A category identifier.
	 */
	private MeasureCategoriesEnum _category;

	private long _timeStamp;
	/**
	 * Constructs a timed tracked runnable of the specified catefory. Time and execution statistics will
	 * be grouped by categories.
	 * 
	 * @param category the category
	 */
	public TimeTrackedRunnable(final MeasureCategoriesEnum category) {
		_category = category;
		_timeStamp = System.currentTimeMillis();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final void run() {
		long startTime = System.currentTimeMillis();
		doRun();
		long endTime = System.currentTimeMillis();
		long timeNeeded = endTime - startTime;
		
		StatisticUtil.getInstance().trackExecution(_category, timeNeeded);
	}

	public long getTimeStamp() {
		return _timeStamp;
	}
	
	/**
	 * Do the real work.  
	 */
	protected abstract void doRun();

}
