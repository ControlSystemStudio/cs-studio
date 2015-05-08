package org.csstudio.dct.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.beanutils.PropertyUtils;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that provides methods to access information that arise from the
 * hierarchical relationships between model elements.
 *
 * @author Sven Wende
 *
 */
public final class AliasResolutionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AliasResolutionUtil.class);

    private AliasResolutionUtil() {
        // Constructor
    }

    /**
     * Returns the name for an element that arises from its hierarchy. The
     * delivered name can be defined on the element directly or is inherited
     * from one of its parents.
     *
     * @param element
     *            the element
     *
     * @return the element name as it is inherited from the hierarchy
     */
    public static String getNameFromHierarchy(IElement element) {
        return getPropertyViaHierarchy(element, "name");
    }

    /**
     * Returns the EPICS name for a record that arises from its hierarchy. The
     * delivered name can be defined on the record directly or is inherited from
     * one of its parent records.
     *
     * @param record
     *            the record
     *
     * @return the records EPICS name as it is inherited from the hierarchy
     */
    public static String getEpicsNameFromHierarchy(IRecord record) {
        return getPropertyViaHierarchy(record, "epicsName");
    }

    @SuppressWarnings("unchecked")
    public static <E> E getPropertyViaHierarchy(IElement element, String propertyName) {
        E result = null;

        Stack<IElement> stack = getElementStack(element);

        while (!stack.isEmpty()) {
            IElement top = stack.pop();

            E p;
            try {
                p = (E) PropertyUtils.getProperty(top, propertyName);

                if (p != null) {
                    result = p;
                }
            } catch (Exception e) {
                LOG.warn("", e);
            }

        }

        return result;
    }

    /**
     * Returns the value for a named parameter or an instance. The delivered
     * value can be defined on the instance directly or is inherited from one of
     * its parent instance or the initial prototype.
     *
     * @param instance
     *            the instance
     * @param parameter
     *            the parameter name
     *
     * @return a parameter value as it is inherited from the hierarchy
     */
    public static String getParameterValueFromHierarchy(IInstance instance, String parameter) {
        String result = null;

        // 1. step: derive parameters from parents
        Stack<IContainer> parentStack = getParentStack(instance);

        while (!parentStack.isEmpty()) {
            IContainer top = parentStack.pop();
            if (top.hasParameterValue(parameter)) {
                result = top.getParameterValue(parameter);
            }
        }

        return result;
    }

    /**
     * Returns the final aliases for a container. The aliases are inherited
     * from hierarchical parents (classical inheritance) and from container´s.
     *
     * @param container
     *            the container
     * @return the final aliases for this container
     */
    public static Map<String, String> getFinalAliases(IContainer container) {
        Map<String, String> result = new HashMap<String, String>();

        // 1. step: derive parameters from parents
        Stack<IContainer> parentStack = getParentStack(container);

        while (!parentStack.isEmpty()) {
            IContainer top = parentStack.pop();
            result.putAll(top.getParameterValues());
        }

        // 2. step: derive from containers
        Stack<IContainer> containerStack = getContainerStack(container);

        while (!containerStack.isEmpty()) {
            IContainer top = containerStack.pop();
            Map<String, String> finalAliases = getFinalAliases(top);
            result.putAll(finalAliases);
        }

        return result;
    }

    /**
     * Collect all parent containers in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     *
     * @param container
     *            the container
     * @return all parent containers, including this
     */
    private static Stack<IContainer> getContainerStack(IContainer container) {
        Stack<IContainer> stack = new Stack<IContainer>();

        IContainer c = container.getContainer();

        while (c != null) {
            stack.add(c);
            c = c.getContainer();
        }
        return stack;
    }

    /**
     * Collect all parent containers in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     *
     * @param instance
     *            the instance
     * @return all parent containers, including this
     */
    private static Stack<IContainer> getParentStack(IContainer instance) {
        Stack<IContainer> stack = new Stack<IContainer>();

        IContainer c = instance;

        while (c != null) {
            stack.add(c);
            c = c.getParent();
        }

        return stack;
    }

    /**
     * Collect all parent records in a stack. On top of the returned stack is
     * the parent that resides at the top of the hierarchy.
     *
     * @param element
     *            the record
     * @return all parent records, including this
     */
    private static Stack<IElement> getElementStack(IElement element) {
        Stack<IElement> stack = new Stack<IElement>();

        IElement e = element;

        while (e != null) {
            stack.add(e);

            if (e instanceof IRecord) {
                e = ((IRecord) e).getParentRecord();
            } else if (e instanceof IContainer) {
                e = ((IContainer) e).getParent();

            } else if (e instanceof IFolder) {
                e = ((IFolder) e).getParentFolder();
            } else {
                e = null;
            }
        }

        return stack;
    }

}
