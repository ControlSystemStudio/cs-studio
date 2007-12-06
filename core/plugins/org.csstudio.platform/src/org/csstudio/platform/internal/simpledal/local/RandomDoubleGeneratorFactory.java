package org.csstudio.platform.internal.simpledal.local;

public class RandomDoubleGeneratorFactory implements IDataGeneratorFactory {

	public AbstractDataGenerator createGenerator(LocalChannel channel,
			int defaultPeriod, String[] options) {
		return new RandomDoubleGenerator(channel, defaultPeriod, options);
	}

}
