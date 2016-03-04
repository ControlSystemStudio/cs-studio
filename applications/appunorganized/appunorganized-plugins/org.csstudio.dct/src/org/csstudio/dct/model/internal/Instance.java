package org.csstudio.dct.model.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.util.CompareUtil;

/**
 * Standard implementation of {@link IInstance}.
 *
 * @author Sven Wende
 */
public final class Instance extends AbstractContainer implements IInstance {

    private static final long serialVersionUID = -7749937096138079752L;

    private Map<String, String> parameterValues;

    public Instance() {
    }

    /**
     * Constructor.
     * @param parent the parent container
     * @param id the id
     */
    public Instance(IContainer parent, UUID id) {
        super(null, parent, id);
        this.parameterValues = new HashMap<String, String>();
    }

    /**
     * Constructor.
     * @param name the name
     * @param prototype the prototype
     * @param id the id
     */
    public Instance(String name, IPrototype prototype, UUID id) {
        this(prototype, id);
        setName(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getParameterValues() {
        return parameterValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameterValue(String key, String value) {
        if (value != null && value.length() > 0) {
            parameterValues.put(key, value);
        } else {
            parameterValues.remove(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValue(String key) {
        return parameterValues.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasParameterValue(String key) {
        return parameterValues.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPrototype getPrototype() {
        IContainer parent = getParent();

        if (parent instanceof IPrototype) {
            return (IPrototype) parent;
        } else {
            if (parent instanceof IInstance) {
                return ((IInstance) parent).getPrototype();
            } else {
                return null;
            }
        }
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isInherited() {
        return getParent() instanceof IInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(IVisitor visitor) {
        visitor.visit(this);

        for(IInstance instance : getInstances()) {
            instance.accept(visitor);
        }

        for(IRecord record : getRecords()) {
            record.accept(visitor);
        }
    }


    /**
     *{@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof Instance) {
            Instance instance = (Instance) obj;

            if (super.equals(obj)) {
                // .. parameter values
                if (getParameterValues().equals(instance.getParameterValues())) {
                    // .. container
                    if (CompareUtil.idsEqual(getContainer(), instance.getContainer())) {
                        // .. folder
                        if (CompareUtil.idsEqual(getParentFolder(), instance.getParentFolder())) {
                            result = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;

    }
}
