package org.csstudio.sds.internal.eventhandling;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.AbstractWidgetPropertyPostProcessor;
import org.csstudio.sds.eventhandling.EventType;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.commands.CompoundCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class WidgetPropertyPostProcessingService implements IWidgetPropertyPostProcessingService {
    private static IWidgetPropertyPostProcessingService instance;
    private List<AbstractWidgetPropertyPostProcessor> processors;
    private static final Logger LOG = LoggerFactory.getLogger(WidgetPropertyPostProcessingService.class);

    public WidgetPropertyPostProcessingService() {
        processors = lookup();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void applyForAllProperties(AbstractWidgetModel widget, EventType eventType) {
        for (WidgetProperty property : widget.getProperties()) {
            CompoundCommand chain = new CompoundCommand();
            applyForSingleProperty(widget, property, chain, EventType.ON_DISPLAY_MODEL_LOADED);

            if (!chain.isEmpty()) {
                chain.execute();
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void applyForSingleProperty(AbstractWidgetModel widget, WidgetProperty widgetProperty, CompoundCommand chain, EventType eventType) {
        for (AbstractWidgetPropertyPostProcessor processor : processors) {
            processor.applyAfterPropertyChangeCommands(widget, widgetProperty, chain, eventType);
        }
    }

    private List<AbstractWidgetPropertyPostProcessor> lookup() {
        List<AbstractWidgetPropertyPostProcessor> processors = new ArrayList<AbstractWidgetPropertyPostProcessor>();

        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(SdsPlugin.EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS);

        for (IConfigurationElement element : elements) {
            try {
                AbstractWidgetPropertyPostProcessor processor = (AbstractWidgetPropertyPostProcessor) element.createExecutableExtension("class"); //$NON-NLS-1$
                assert processor != null;
                processors.add(processor);
            } catch (CoreException e) {
                LOG.warn("Cannot instantiate extension class [" + element.getAttribute("class") + "] for extension point ["
                                + SdsPlugin.EXTPOINT_WIDGET_PROPERTY_POSTPROCESSORS + "]");
            }
        }

        return processors;
    }

}
