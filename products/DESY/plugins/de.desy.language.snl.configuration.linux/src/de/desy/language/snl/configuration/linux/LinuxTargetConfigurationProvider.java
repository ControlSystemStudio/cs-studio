package de.desy.language.snl.configuration.linux;

import java.util.ArrayList;
import java.util.List;

import de.desy.language.snl.compilerconfiguration.AbstractCompilerConfiguration;
import de.desy.language.snl.compilerconfiguration.AbstractTargetConfigurationProvider;
import de.desy.language.snl.compilerconfiguration.ICompilerOptionsService;
import de.desy.language.snl.configuration.linux.configurations.ApplicationCompilerConfiguration;
import de.desy.language.snl.configuration.linux.configurations.CCompilerConfiguration;
import de.desy.language.snl.configuration.linux.configurations.PreCompilerConfiguration;
import de.desy.language.snl.configuration.linux.configurations.SNCompilerConfiguration;

public class LinuxTargetConfigurationProvider extends
		AbstractTargetConfigurationProvider {

	public LinuxTargetConfigurationProvider() {
	}

	@Override
	public List<AbstractCompilerConfiguration> getConfigurations(ICompilerOptionsService service) {
		List<AbstractCompilerConfiguration> configurations = new ArrayList<AbstractCompilerConfiguration>();
		
		configurations.add(new PreCompilerConfiguration(service));
		configurations.add(new SNCompilerConfiguration(service));
		configurations.add(new CCompilerConfiguration(service));
		configurations.add(new ApplicationCompilerConfiguration(service));
		return configurations;
	}

}
