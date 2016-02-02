package org.csstudio.saverestore;

import java.util.Objects;

/**
 *
 * <code>DataProviderWrapper</code> is a wrapper that contains the {@link DataProvider} and its meta information.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DataProviderWrapper {

    /** Implementation of the DataProvider interface provided by an extension point */
    public final DataProvider provider;
    /** The name of the data provider */
    public final String name;
    /** Unique id of the data provider */
    public final String id;
    /** Description of the data provider */
    public final String description;

    /**
     * Constructs a new wrapper.
     *
     * @param id the id of the data provider
     * @param name the name of the data provider
     * @param description the description of the data provider
     * @param provider the data provider
     */
    public DataProviderWrapper(String id, String name, String description, DataProvider provider) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Data provider id not defined.");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Data provider implementatio unknown for " + id);
        }
        this.id = id;
        this.name = name == null ? id : name;
        this.description = description == null ? "" : description;
        this.provider = provider;
    }

    /**
     * Returns the presentation name of this wrapper, which is equal to name: description.
     *
     * @return the presentation name (name: description)
     */
    public String getPresentationName() {
        return name + ": " + description;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getPresentationName();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(id, ((DataProviderWrapper) obj).id);
    }
}
