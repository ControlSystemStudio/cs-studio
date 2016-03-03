package org.csstudio.dct.nameresolution.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.ExtensionPointUtil;
import org.csstudio.dct.ISensorIdService;
import org.csstudio.dct.PreferenceSettings;
import org.csstudio.dct.ServiceExtension;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.domain.common.strings.StringUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Implementation for the sensorid() function.
 *
 * @author Sven Wende
 *
 */
public final class SensorIdFieldFunction implements IFieldFunction {

    private Map<String, ServiceExtension<ISensorIdService>> services;

    /**
     * Constructor.
     */
    public SensorIdFieldFunction() {
        services = ExtensionPointUtil.lookupNamingServiceExtensions(DctActivator.EXTPOINT_SENSOR_ID_SERVICES);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
        ISensorIdService service = null;

        if (services.isEmpty()) {
            throw new IllegalArgumentException("no service registered");
        } else if (services.size() == 1) {
            service = services.values().iterator().next().getService();
        } else {
            String id = Platform.getPreferencesService().getString(DctActivator.PLUGIN_ID, PreferenceSettings.SENSOR_ID_SERVICE_ID.name(), "",
                    null);

            if (!StringUtil.hasLength(id) || !services.containsKey(id)) {
                throw new IllegalArgumentException("multiple services registered, please choose one in preferences");
            } else {
                service = services.get(id).getService();
            }
        }

        assert service != null;

        return service.getSensorId(parameters[0], fieldName);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knownParameters, IRecord record) {
        return Collections.EMPTY_LIST;
    }
}
