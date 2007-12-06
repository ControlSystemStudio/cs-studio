package org.csstudio.platform.internal.simpledal.local;

import java.util.regex.Pattern;

import org.csstudio.platform.logging.CentralLogger;

public enum GeneratedData {
	/**
	 * Pattern for ..
	 */
	RANDOM_NUMBER("^.* RND:([0-9]+):([0-9]+):([0-9]+)$", new RandomDoubleGeneratorFactory());
	
	private Pattern _pattern;
	private IDataGeneratorFactory _dataGeneratorFactory;
	
	private GeneratedData(String pattern, IDataGeneratorFactory dataGeneratorFactory) {
		assert pattern != null;
		assert dataGeneratorFactory != null;
		_pattern = Pattern.compile(pattern);
		_dataGeneratorFactory = dataGeneratorFactory;
	}
	
	
	public Pattern getPattern() {
		return _pattern;
	}
	
	public IDataGeneratorFactory getDataGeneratorFactory() {
		return _dataGeneratorFactory;
	}
	
}
