package org.csstudio.platform.internal.simpledal.local;

public interface IDataGeneratorFactory {
	AbstractDataGenerator createGenerator(LocalChannel channel, int defaultPeriod, String options[]);
}
