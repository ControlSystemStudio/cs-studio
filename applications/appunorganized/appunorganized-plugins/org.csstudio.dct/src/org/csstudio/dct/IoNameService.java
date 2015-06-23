package org.csstudio.dct;

import java.util.List;

/**
 * Interface for the IO name service.
 *
 * @author Sven Wende
 */
public interface IoNameService {
    /**
     * Returns the Epics Address for the specified IO name.
     *
     * @param ioName
     *            the key (mandatory)
     * @param field
     *            a field name (optional)
     * @return the Epics address for the specified IO name or null if no Epics address exists
     *         for that IO name
     */
    String getEpicsAddress(String ioName, String field);

    /**
     * Returns a list off all configured IO names.
     *
     * @return a list of IO names.
     */
    List<String> getAllIoNames();

}
