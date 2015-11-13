package org.csstudio.saverestore;

/**
 *
 * <code>DataProviderWrapper</code> is a wrapper that contains the {@link DataProvider} and its meta information.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DataProviderWrapper {

    public final DataProvider provider;
    public final String name;
    public final String id;
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
     * @return the presentation name of this wrapper (name: description)
     */
    public String getPresentationName() {
        return name + ": " + description;
    }

    @Override
    public String toString() {
        return getPresentationName();
    }
}
