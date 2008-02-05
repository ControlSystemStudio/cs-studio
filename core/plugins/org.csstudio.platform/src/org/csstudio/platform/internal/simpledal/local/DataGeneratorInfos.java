package org.csstudio.platform.internal.simpledal.local;

import java.util.regex.Pattern;

import org.csstudio.platform.logging.CentralLogger;

public enum DataGeneratorInfos {
	/**
	 * Random number generator pattern. The pattern reads the following
	 * variables in the process variable name
	 * <code> local://property RND:{from}:{to}:{period} </code>, for example
	 * <code>local://abc RND:1:100:10</code> which creates random numbers
	 * between 1 and 100 every 10 milliseconds.
	 * 
	 */
	RANDOM_NUMBER("^.* RND:([0-9]+):([0-9]+):([0-9]+)$",
			new RandomDoubleGeneratorFactory()),

	/**
	 * Class method generator pattern. The pattern reads the following variables
	 * in the process variable name
	 * <code> local://property CLM:{classname}:{methodname}:{period} </code>, for example
	 * <code>local://abc CLM:java.lang.String:toString:10</code> which creates ...
	 * 
	 */
	CLASS_METHOD("^.* CLM:(.+):(.+):([0-9]+)$",
			new ClassMethodGeneratorFactory()),

	SYSTEM_INFO("^.*SINFO:([a-zA-Z0-9]+)(:([0-9]+))?$",
			new SystemInfoGeneratorFactory());	

	private Pattern _pattern;
	private IDataGeneratorFactory _dataGeneratorFactory;

	private DataGeneratorInfos(String pattern,
			IDataGeneratorFactory dataGeneratorFactory) {
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
