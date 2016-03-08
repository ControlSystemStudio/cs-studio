package org.csstudio.sds.eventhandling;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetProperty;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Base class for widget property post processors. This class can be extended, to create a new
 * PostProcessor for one property of a widget.
 *
 * @author Sven Wende, Kai Meyer (C1 WPS)
 * @param <W> the widget type
 */
public abstract class AbstractWidgetPropertyPostProcessor<W extends AbstractWidgetModel> implements
        IExecutableExtension {
    private String _propertyId;
    private String _workingId = null;
    private String[] _widgetIds;
    private boolean _skipOnLoad;

    /**
     *{@inheritDoc}
     */
    @Override
    public final void setInitializationData(final IConfigurationElement config,
                                            final String propertyName,
                                            final Object data) throws CoreException {
        _widgetIds = config.getAttribute("widgetId").split(",");
        _propertyId = config.getAttribute("propertyId");
        _skipOnLoad = Boolean.parseBoolean(config.getAttribute("skipOnLoad"));
    }

    /**
     * Is only defined during execution of applyAfterPropertyChangeCommands. May be used in template
     * method doCreateCommand.
     *
     * @return the current working Property
     */
    protected final String getWorkingPropertyId() {
        String result = _workingId;
        // TODO hr use annotations
        assert result != null : "Working ID may not be undefined";
        return result;
    }

    /**
     * Called, when the specified property changes in the specified widget. Checks whether this post
     * processor has to be applied (widget and property identifiers match) and adds additional
     * commands to the specified command chain.
     *
     * @param widget the widget model
     * @param property the property that
     * @param chain the command chain
     * @param eventType the type of event
     */
    public final void applyAfterPropertyChangeCommands(final W widget,
                                                       final WidgetProperty property,
                                                       final CompoundCommand chain,
                                                       final EventType eventType) {
        boolean apply = false;
        _workingId = property.getId();
        for (String id : _widgetIds) {
            id = id.trim();
            apply |= id.equals("*");
            apply |= id.equals(widget.getTypeID());
        }
        if (_propertyId.endsWith("*")) {
            String replace = _propertyId.replace("*", "");
            apply &= property.getId().startsWith(replace);
        } else {
            apply &= _propertyId.equals(property.getId());

        }

        // .. skip certain processors when display model is loaded (and not manipulated manually)
        if (_skipOnLoad && (eventType == EventType.ON_DISPLAY_MODEL_LOADED)) {
            apply = false;
        }

        if (apply) {
            Command command = doCreateCommand(widget);

            if (command != null) {
                chain.add(command);
            }
        }
        _workingId = null;
    }

    /**
     * Template method. Subclasses have to implement depending property changes using the command
     * pattern.
     *
     * @param widget the widget
     * @return a command that changes dependent properties or null
     */
    protected abstract Command doCreateCommand(W widget);
}
