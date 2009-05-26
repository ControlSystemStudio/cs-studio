package de.desy.language.snl.configuration.linux;

import java.util.ArrayList;
import java.util.List;

import de.desy.language.snl.SNLCoreActivator;
import de.desy.language.snl.compilerconfiguration.AbstractCompilerConfiguration;
import de.desy.language.snl.compilerconfiguration.AbstractTargetConfigurationProvider;
import de.desy.language.snl.compilerconfiguration.ICompilerOptionsService;
import de.desy.language.snl.configuration.linux.configurations.ApplicationCompilerConfiguration;
import de.desy.language.snl.configuration.linux.configurations.CCompilerConfiguration;
import de.desy.language.snl.configuration.linux.configurations.PreCompilerConfiguration;
import de.desy.language.snl.configuration.linux.configurations.SNCompilerConfiguration;
import de.desy.language.snl.configurationservice.CompilerOptionsService;

public class LinuxTargetConfigurationProvider extends
		AbstractTargetConfigurationProvider {

	public LinuxTargetConfigurationProvider() {
	}

	@Override
	public List<AbstractCompilerConfiguration> getConfigurations() {
		List<AbstractCompilerConfiguration> configurations = new ArrayList<AbstractCompilerConfiguration>();
		
		ICompilerOptionsService service = new CompilerOptionsService(SNLCoreActivator.getDefault().getPreferenceStore());
		
		configurations.add(new PreCompilerConfiguration(service));
		configurations.add(new SNCompilerConfiguration(service));
		configurations.add(new CCompilerConfiguration(service));
		configurations.add(new ApplicationCompilerConfiguration(service));
		return configurations;
	}

}
