package de.desy.language.snl.configurationservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import de.desy.language.snl.compilerconfiguration.AbstractTargetConfigurationProvider;
import de.desy.language.snl.internal.SNLCoreActivator;

public class ConfigurationService {
	
	private static ConfigurationService _instance;
	private Map<String, AbstractTargetConfigurationProvider> _providerMap;

	public static ConfigurationService getInstance() {
		if (_instance == null) {
			_instance = new ConfigurationService();
		}
		return _instance;
	}
	
	private ConfigurationService() {
		_providerMap = new HashMap<String, AbstractTargetConfigurationProvider>();
		lookup();
	}

	private void lookup() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(SNLCoreActivator.PLUGIN_ID + ".TargetConfigurationProvider");
		for (IConfigurationElement element : elements) {
			String id = element.getAttribute("id");
			String description = element.getAttribute("description");
			String platform = element.getAttribute("platform");
			try {
				AbstractTargetConfigurationProvider provider = (AbstractTargetConfigurationProvider) element.createExecutableExtension("class");
				provider.setDescription(description);
				provider.setPlatform(platform);
				_providerMap.put(id, provider);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Set<String> getAllIDs() {
		return _providerMap.keySet();
	}
	
	public boolean hasId(String id) {
		return _providerMap.containsKey(id);
	}
	
	public AbstractTargetConfigurationProvider getProvider(String id) {
		assert id != null : "id != null";
		assert id.trim().length() != 0 : "id.trim().length() != 0";
		assert hasId(id) : "hasId(id)";
		
		return _providerMap.get(id);
	}
	
	public String getFullDescription(String id) {
		assert id != null : "id != null";
		assert id.trim().length() != 0 : "id.trim().length() != 0";
		assert hasId(id) : "hasId(id)";
		
		AbstractTargetConfigurationProvider provider = _providerMap.get(id);
		StringBuffer buffer = new StringBuffer();
		buffer.append(provider.getDescription());
		buffer.append(" (");
		buffer.append(provider.getPlatform());
		buffer.append(")");
		return buffer.toString();
	}

}
