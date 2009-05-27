package de.desy.language.snl.compilerconfiguration;

import java.util.List;

public abstract class AbstractTargetConfigurationProvider {
	
	private String _description;
	private String _platform;

	public void setDescription(String description) {
		_description = description;
	}
	
	public void setPlatform(String platform) {
		_platform = platform;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public String getPlatform() {
		return _platform;
	}
	
	public abstract List<AbstractCompilerConfiguration> getConfigurations(ICompilerOptionsService service);

}
