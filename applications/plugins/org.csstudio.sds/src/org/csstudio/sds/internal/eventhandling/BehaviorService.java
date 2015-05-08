package org.csstudio.sds.internal.eventhandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BehaviorService implements IBehaviorService {
    private static final String ALL_WIDGETS_ID = "*";

    private final Map<String, Map<String, BehaviorDescriptor>> _behaviors;

    private static final Logger LOG = LoggerFactory.getLogger(BehaviorService.class);

    public BehaviorService() {
        _behaviors = new HashMap<String, Map<String, BehaviorDescriptor>>();
        lookup();
    }

    @SuppressWarnings("unchecked")
    protected void lookup() {
        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(SdsPlugin.EXTPOINT_BEHAVIORS);

        for (IConfigurationElement e : configurationElements) {
            String behaviorId = e.getAttribute("id");
            String widgetTypeId = e.getAttribute("widgetTypeId");
            String description = e.getAttribute("description");
            AbstractBehavior<AbstractWidgetModel> behavior;


            try {
                behavior = (AbstractBehavior<AbstractWidgetModel>) e.createExecutableExtension("class");
            } catch (CoreException e1) {
                String msg = "Behavior ["+e.getAttribute("class")+"] could not be instantiated.";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }

            assert behaviorId!=null;
            assert widgetTypeId!=null;
            assert description!=null;
            assert behavior!=null;

            Set<String> shadowedProperties = new HashSet<String>();
            shadowedProperties.addAll(Arrays.asList(behavior.getInvisiblePropertyIds()));
            assert shadowedProperties!=null;

            addBehaviour(new BehaviorDescriptor(behaviorId, widgetTypeId, description, shadowedProperties, e));
        }
    }

    private void addBehaviour(BehaviorDescriptor descriptor) {
        assert descriptor != null : "behavior != null";

        String widgetId = descriptor.getWidgetTypeId();

        // .. create inner map if necessary
        Map<String, BehaviorDescriptor> widgetBehaviors = _behaviors.get(widgetId);

        if (widgetBehaviors == null) {
            widgetBehaviors = new HashMap<String, BehaviorDescriptor>();
            _behaviors.put(widgetId, widgetBehaviors);
        }

        assert widgetBehaviors != null;

        // .. add descriptor to inner map
        if (!widgetBehaviors.containsKey(descriptor.getBehaviorId())) {
            widgetBehaviors.put(descriptor.getBehaviorId(), descriptor);
        } else {
            throw new IllegalArgumentException("Only one behavior for the widget-type >>" + widgetId + "<< and id >>"
                    + descriptor.getBehaviorId() + "<< should be registered.");
        }
    }

    @SuppressWarnings("unchecked")
    public AbstractBehavior<AbstractWidgetModel> getBehavior(String behaviorId, String widgetId) {
        assert behaviorId != null : "behaviorId != null";
        assert behaviorId.trim().length() > 0 : "behaviorId.trim().length() > 0";
        assert widgetId != null : "widgetId != null";
        assert widgetId.trim().length() > 0 : "widgetId.trim().length() > 0";

        AbstractBehavior<AbstractWidgetModel> behavior = null;

        BehaviorDescriptor descriptor = getDescriptor(behaviorId, widgetId);

        if (descriptor != null) {
            IConfigurationElement e = descriptor.getConfigurationElement();

            try {
                behavior = (AbstractBehavior<AbstractWidgetModel>) e.createExecutableExtension("class");
            } catch (CoreException e1) {
                throw new IllegalArgumentException("Behavior ["+e.getAttribute("class")+"] could not be instantiated.");
            }
        }

        return behavior;
    }

    public List<BehaviorDescriptor> getBehaviors(String widgetId) {
        assert widgetId != null : "widgetId != null";
        assert widgetId.trim().length() > 0 : "widgetId.trim().length() > 0";

        List<BehaviorDescriptor> result = new ArrayList<BehaviorDescriptor>();

        // .. add behaviors for that specific widget id
        result.addAll(getDescriptorsByWidgetId(widgetId));

        // .. add global behaviors
        result.addAll(getDescriptorsByWidgetId(ALL_WIDGETS_ID));

        return result;
    }

    public String[] getInvisiblePropertyIds(String behaviorId, String widgetId) {
        assert behaviorId != null : "behaviorId != null";
        assert widgetId != null : "widgetId != null";
        assert widgetId.trim().length() > 0 : "widgetId.trim().length() > 0";

        if (behaviorId.length() > 0) {
            AbstractBehavior<AbstractWidgetModel> behavior = getBehavior(behaviorId, widgetId);
            if (behavior != null) {
                return behavior.getInvisiblePropertyIds();
            }
        }
        return new String[0];
    }

    private BehaviorDescriptor getDescriptor(String behaviorId, String widgetId) {
        assert behaviorId != null : "behaviorId != null";
        assert behaviorId.trim().length() > 0 : "behaviorId.trim().length() > 0";
        assert widgetId != null : "widgetId != null";
        assert widgetId.trim().length() > 0 : "widgetId.trim().length() > 0";

        BehaviorDescriptor descriptor = null;

        Map<String, BehaviorDescriptor> tmp = _behaviors.get(widgetId);

        if (tmp != null && tmp.containsKey(behaviorId)) {
            descriptor = tmp.get(behaviorId);
        }

        return descriptor;
    }

    private List<BehaviorDescriptor> getDescriptorsByWidgetId(String widgetId) {
        List<BehaviorDescriptor> result = Collections.emptyList();

        if (_behaviors.containsKey(widgetId)) {
            result = new ArrayList<BehaviorDescriptor>(_behaviors.get(widgetId).values());
        }

        assert result != null;
        return result;

    }

}
