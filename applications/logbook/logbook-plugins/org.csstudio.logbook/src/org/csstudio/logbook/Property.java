package org.csstudio.logbook;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An interface that represents properties attached to a LogEntry It has a name
 * and a group of attribute, value pairs.
 *
 * @author shroffk
 *
 */
public interface Property {

    /**
     * The unique name to identify the property
     *
     * @return String - property name
     */
    public String getName();

    /**
     * A set of all the attributes defined for this property
     *
     * @return Collection<String> - the list of attributes for this property
     */
    public Collection<String> getAttributeNames();

    /**
     *
     * @return Collection<String> - the attribute values
     */
    public Collection<String> getAttributeValues();

    /**
     * Provides the value for the attribute _attributeName_
     *
     * @param attributeName
     * @return String - the attribute value for the attribute identified buy _attributeName_
     */
    public String getAttributeValue(String attributeName);

    /**
     * @return All the attributes for this property
     */
    public Set<Entry<String, String>> getAttributes();

}
