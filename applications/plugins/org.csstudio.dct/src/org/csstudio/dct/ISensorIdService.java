package org.csstudio.dct;

/**
 * Interface for the sensor id service.
 *
 * @author Sven Wende
 */
public interface ISensorIdService {
    /**
     * Returns the sensor id.
     *
     * @param sensorName
     *            the key (mandatory)
     * @param field
     *            a field name (optional)
     * @return the sensor id for the sensor name or null if no sensor id exists
     *         for that name
     */
    String getSensorId(String sensorName, String field);
}
