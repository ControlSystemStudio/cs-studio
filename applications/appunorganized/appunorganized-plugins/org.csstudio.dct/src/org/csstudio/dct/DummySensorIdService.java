package org.csstudio.dct;

import java.util.UUID;

/**
 * Dummy implementation of a sensor id service.
 *
 * @author Sven Wende
 *
 */
public final class DummySensorIdService implements ISensorIdService {

    /**
     *{@inheritDoc}
     */
    @Override
    public String getSensorId(String id, String field) {
        return "sensor_" + UUID.randomUUID() + id + field;
    }

}
