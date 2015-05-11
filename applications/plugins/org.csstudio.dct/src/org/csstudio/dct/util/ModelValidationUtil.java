package org.csstudio.dct.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;

/**
 * Collection of utility method used for model validation.
 *
 * @author Sven Wende
 *
 */
public class ModelValidationUtil {
    private ModelValidationUtil() {
    }

    /**
     * Returns all instances that dependend on the specified instance.
     *
     * @param instance the instance
     *
     * @return all instances depending on the specified instance
     */
    public static List<IInstance> recursivelyGetDependentInstances(IInstance instance) {
        List<IInstance> result = new ArrayList<IInstance>();

        for (IContainer c : instance.getDependentContainers()) {
            assert c instanceof IInstance;
            IInstance di = (IInstance) c;
            List<IInstance> dependent = recursivelyGetDependentInstances(di);

            for (IInstance i : dependent) {
                if (result.contains(i)) {
                    throw new IllegalArgumentException("TODO");
                }
            }

            result.addAll(dependent);
            result.add(di);
        }

        return result;

    }

    /**
     * Checks for a transitive loop between the specified target container and a
     * prototype.
     *
     * @param container
     *            the target container
     * @param prototype
     *            the prototype
     *
     * @return true, if the specified prototype is already used in the
     *         transitive closure of the specified container
     */
    public static boolean causesTransitiveLoop(IContainer container, IPrototype prototype) {
        IPrototype forbiddenPrototype;

        if (container instanceof IPrototype) {
            forbiddenPrototype = (IPrototype) container;
        } else {
            forbiddenPrototype = ((IInstance) container).getPrototype();
        }

        boolean result = prototype == forbiddenPrototype;

        for (IInstance i : prototype.getInstances()) {
            result |= isInTransitiveClosure(i, forbiddenPrototype);
        }

        return result;
    }

    private static boolean isInTransitiveClosure(IInstance instance, IPrototype forbiddenPrototype) {
        boolean result = false;

        result |= instance.getPrototype() == forbiddenPrototype;

        for (IInstance i : instance.getInstances()) {
            result |= i.getPrototype() == forbiddenPrototype;
            result |= isInTransitiveClosure(i, forbiddenPrototype);
        }

        return result;
    }


}
