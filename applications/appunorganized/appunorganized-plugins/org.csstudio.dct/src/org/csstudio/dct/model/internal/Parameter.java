package org.csstudio.dct.model.internal;

import java.io.Serializable;

import org.csstudio.dct.util.CompareUtil;

/**
 * Represents a parameter with a default value.
 *
 * @author Sven Wende
 */
public final class Parameter implements Serializable {
    private static final long serialVersionUID = 5417684183030531306L;
    private String name;
    private String defaultValue;

    /**
     * Constructor.
     *
     * @param name
     *            the name
     * @param defaultValue
     *            the default value
     */
    public Parameter(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue
     *            the default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof Parameter) {
            Parameter parameter = (Parameter) obj;
            // .. name
            if (CompareUtil.equals(getName(), parameter.getName())) {
                // .. default value
                if (CompareUtil.equals(getDefaultValue(), parameter.getDefaultValue())) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Clones the parameter.
     */
    @Override
    public Parameter clone() {
        return new Parameter(name, defaultValue);
    }
}
