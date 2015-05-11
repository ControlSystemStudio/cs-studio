package org.csstudio.dct.util;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.model.IElement;

/**
 * Collection of utility methods that help comparing things.
 *
 * @author Sven Wende
 *
 */
public final class CompareUtil {
    private CompareUtil() {
    }

    /**
     * Compares two Objects.
     *
     * @param o1
     *            Object 1
     * @param o2
     *            Object 2
     * @return true, if both Object equal
     */
    public static boolean equals(Object o1, Object o2) {
        boolean result = false;

        if (o1 == null) {
            if (o2 == null) {
                result = true;
            }
        } else {
            if (o1.equals(o2)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Compares the id´s of two elements.
     *
     * @param o1
     *            element 1
     * @param o2
     *            element 2
     * @return true, if the id´s of both elements equal or both elements are
     *         null
     */
    public static boolean idsEqual(IElement o1, IElement o2) {
        boolean result = false;

        if (o1 != null) {
            if (o2 != null) {
                result = equals(o1.getId(), o2.getId());
            }
        } else {
            if (o2 == null) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Returns true, if the specified list contains only objects that are
     * compatible to the specified class type.
     *
     * @param type
     *            the class type
     * @param elements
     *            the list of objects
     *
     * @return true of the list contains only objects of a certain type
     */
    public static boolean containsOnly(Class type, List elements) {
        boolean result = true;

        for (Object e : elements) {
            result &= type.isAssignableFrom(e.getClass());
        }

        return result;
    }

    public static <E> List<E> convert(List elements) {
        List<E> result = new ArrayList<E>();

        for (Object o : elements) {
            result.add((E) o);
        }

        return result;
    }
}
