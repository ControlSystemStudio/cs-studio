package org.csstudio.platform.internal.simpledal.local;


public abstract class AbstractDataGenerator<E> implements Runnable {
	private LocalChannel _localChannel;
	private int _period;

	public AbstractDataGenerator(LocalChannel localChannel, int defaultPeriod,
			String[] options) {
		assert localChannel != null;
		assert defaultPeriod > 0 : "defaultPeriod>0";
		assert options != null;
		_localChannel = localChannel;
		_period = defaultPeriod;

		init(options);
	}

	protected abstract void init(String[] options);

	protected abstract E generateNextValue();

	public void setPeriod(int period) {
		_period = period;
	}
	
	public int getPeriod() {
		return _period;
	}

	public void run() {
		Object nextValue = generateNextValue();
		_localChannel.setValue(nextValue);
	}

}
