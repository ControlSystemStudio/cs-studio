package de.desy.language.snl.compilerconfiguration;

import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractCompilerConfiguration {
	
	private final ICompilerOptionsService _service;

	public AbstractCompilerConfiguration(ICompilerOptionsService service) {
		_service = service;
	}

	public abstract List<String> getCompilerParameter(String sourceFile, String targetFile);

	public abstract Pattern getErrorPattern();

	public abstract String getCompilerPath();

	public abstract String getSourceFolder();

	public abstract String getTargetFolder();

	public abstract String getSourceFileExtension();

	public abstract String getTargetFileExtension();
	
	protected ICompilerOptionsService getCompilerOptionService() {
		return _service;
	}

}