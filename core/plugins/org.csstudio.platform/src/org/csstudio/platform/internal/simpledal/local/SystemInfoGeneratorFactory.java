package org.csstudio.platform.internal.simpledal.local;

public class SystemInfoGeneratorFactory implements IDataGeneratorFactory {

	public AbstractDataGenerator createGenerator(LocalChannel channel,
			int defaultPeriod, String[] options) {
		return new SystemInfoGenerator(channel, defaultPeriod, options);
	}

}
