package org.csstudio.sds.internal.eventhandling;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

public class BehaviorDescriptor implements IBehaviorDescription {
    private String _behaviorId;
    private String _widgetTypeId;
    private String _description;
    private Set<String> _shadowedProperties;
    private IConfigurationElement _configurationElement;

    public BehaviorDescriptor(String behaviorId, String widgetTypeId, String description, Set<String> shadowedProperties, IConfigurationElement configurationElement) {
        assert behaviorId!=null;
        assert widgetTypeId!=null;
        assert description!=null;
        assert shadowedProperties!=null;
        _behaviorId = behaviorId;
        _widgetTypeId = widgetTypeId;
        _description = description;
        _shadowedProperties =  Collections.unmodifiableSet(shadowedProperties);
        _configurationElement = configurationElement;
    }

    public String getBehaviorId() {
        return _behaviorId;
    }

    public String getWidgetTypeId() {
        return _widgetTypeId;
    }

    public String getDescription() {
        return _description;
    }

    public Set<String> getShadowedProperties() {
        return _shadowedProperties;
    }

    public IConfigurationElement getConfigurationElement() {
        return _configurationElement;
    }
}
