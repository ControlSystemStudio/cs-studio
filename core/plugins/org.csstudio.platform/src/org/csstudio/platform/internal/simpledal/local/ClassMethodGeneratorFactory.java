package org.csstudio.platform.internal.simpledal.local;

public class ClassMethodGeneratorFactory implements IDataGeneratorFactory {

	public AbstractDataGenerator createGenerator(LocalChannel channel,
			int defaultPeriod, String[] options) {
		return new ClassMethodGenerator(channel, defaultPeriod, options);
	}

}
