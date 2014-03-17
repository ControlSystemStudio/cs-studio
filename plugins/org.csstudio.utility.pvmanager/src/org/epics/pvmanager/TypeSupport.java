/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implements the mechanism for registering different types so that the library
 * knows how to handle them.
 * <p>
 * For a type to be usable by the library it needs to be defined:
 * <ul>
 *   <li>How to copy - since values given to the UI should be modified only
 *   within the UI thread, it follows that new values cannot be prepared
 *   "in place", on the same object that was given to the UI. At notification,
 *   there will be then two copies, the old and the new, and in need to be clear
 *   how the new copy should be delivered. (e.g. just pass the new copy, modify
 *   the old object in place, etc...).</li>
 *   <li>When to notify - by comparing elements of the value, it should
 *   decide on what condition the old value need to be modified and the
 *   UI should be notified of the change.</li>
 * </ul>
 *
 * @param <T> the type for which to add support
 * @author carcassi
 */
public abstract class TypeSupport<T> {
    
    /**
     * Internal class to improve readability.
     * @author bknerr
     * @since 20.01.2011
     */
    private static final class TypeSupportMap extends ConcurrentHashMap<Class, TypeSupport> {
        private static final long serialVersionUID = -8726785703555122582L;
        public TypeSupportMap() { /* EMPTY */ }
    }
    
    private static final Map<Class<? extends TypeSupport>, TypeSupportMap> allTypeSupports = 
        new ConcurrentHashMap<Class<? extends TypeSupport>, TypeSupportMap>();
    private static final Map<Class<? extends TypeSupport>, TypeSupportMap> allCalcTypeSupports = 
        new ConcurrentHashMap<Class<? extends TypeSupport>, TypeSupportMap>();
    
    
    private static 
    void addTypeSupportFamilyIfNotExists(final Map<Class<? extends TypeSupport>, TypeSupportMap> map, 
                                         final Class<? extends TypeSupport> typeSupportFamily) {
        TypeSupportMap familyMap = map.get(typeSupportFamily);
        if (familyMap == null) {
            TypeSupportMap supportMap = new TypeSupportMap();
            map.put(typeSupportFamily, supportMap);
        }
    }
    
    /**
     * Adds type support for the given class. The type support added will apply
     * to the given class and all of its subclasses. Support of the same
     * family cannot be added twice and will cause an exception. Support for
     * the more specific subclass overrides support for the more abstract class.
     * A class cannot have two types support in the same family coming from
     * two different and unrelated interfaces.
     *
     * @param typeSupport the support to add
     */
    public static
    void addTypeSupport(final TypeSupport<?> typeSupport) {
        Class<? extends TypeSupport> typeSupportFamily = typeSupport.getTypeSupportFamily();
        
        addTypeSupportFamilyIfNotExists(allTypeSupports, typeSupportFamily);
        addTypeSupportFamilyIfNotExists(allCalcTypeSupports, typeSupportFamily);

        // Can't install support for the same type twice
        if (allTypeSupports.get(typeSupportFamily).get(typeSupport.getType()) != null) {
            throw new RuntimeException(typeSupportFamily.getSimpleName() + " was already added for type " + typeSupport.getType().getName());
        }
        
        allTypeSupports.get(typeSupportFamily).put(typeSupport.getType(), typeSupport);
        // Need to clear all calculated supports since registering an
        // interface may affect all the calculated supports
        // of all the implementations
        allCalcTypeSupports.get(typeSupportFamily).clear();
    }
    
    /**
     * Checks whether the type is supported directly or through one of the
     * supertypes.
     * 
     * @param type type to check support for
     * @param typeSupportFamily the family in which to look for
     * @return true if supported
     * @throws RuntimeException if multiple conflicting support is found 
     */
    public static boolean isTypeSupported(Class<? extends TypeSupport> typeSupportFamily, Class<?> type) {
        return findTypeSupportFor(typeSupportFamily, type) != null;
    }
    
    /**
     * Checks whether the type is supported on the same exact type.
     * 
     * @param type type to check support for
     * @param typeSupportFamily the family in which to look for
     * @return true if supported
     */
    public static boolean isTypeDirectlySupported(Class<? extends TypeSupport> typeSupportFamily, Class<?> type) {
        return allTypeSupports.get(typeSupportFamily) != null &&
                allTypeSupports.get(typeSupportFamily).get(type) != null;
    }

    /**
     * Calculates and caches the type support for a particular class, so that
     * introspection does not occur at every call.
     * <p>
     * Find the supports for all supertypes. If multiple supports for different
     * supertypes are found, and there isn't a most specific one (i.e. one
     * that implements all the others) then an exception is thrown.
     *
     * @param <T> the type to retrieve support for
     * @param supportFamily the support family for which to find support
     * @param typeClass the class of the type
     * @return the support for the type or null
     * @throws RuntimeException if multiple conflicting support is found 
     */
    protected static <T> TypeSupport<T> findTypeSupportFor(final Class<? extends TypeSupport> supportFamily,
                                                             final Class<T> typeClass) {
        
        TypeSupportMap calcSupportMap = allCalcTypeSupports.get(supportFamily);
        TypeSupportMap supportMap = allTypeSupports.get(supportFamily);
        
        if (supportMap == null || calcSupportMap == null) {
            return null;
        }

        // If we get the cached support for a specific type,
        // we are guaranteed that they support is for that type
        @SuppressWarnings("unchecked")
        TypeSupport<T> support = (TypeSupport<T>) calcSupportMap.get(typeClass);
        if (support == null) {
            support = calculateSupport(typeClass, supportMap);
            if (support == null) {
                // It's up to the specific support to decide what to do
                return null;
            }
            calcSupportMap.put(typeClass, support);
        }
        return support;
    }
    
    /**
     * Returns all the type supports available for the given class.
     *
     * @param <T> the kind of type support
     * @param supportFamily the supported type
     * @return the list of type supports for the given type
     */
    protected static <T extends TypeSupport<?>> Collection<T> typeSupportsFor(final Class<T> supportFamily) {
        TypeSupportMap map = allTypeSupports.get(supportFamily);
        if (map == null)
            return Collections.emptyList();
        @SuppressWarnings("unchecked")
        Collection<T> supports = (Collection<T>) (Collection) map.values();
        return supports;
    }

    private static <T> TypeSupport<T> calculateSupport(final Class<T> typeClass,
                                                               final TypeSupportMap supportMap) {
        // Get all super types that have a support defined on
        Set<Class> superTypes = new HashSet<Class>();
        recursiveAddAllSuperTypes(typeClass, superTypes);
        superTypes.retainAll(supportMap.keySet());

        // No super type found, no support for this type
        if (superTypes.isEmpty()) {
            return null;
        }

        // Super types found, make sure that there is one
        // type that implements everything
        for (Class<?> type : superTypes) {
            boolean assignableToEverything = true;
            for (Class<?> compareType : superTypes) {
                assignableToEverything = assignableToEverything && compareType.isAssignableFrom(type);
            }
            if (assignableToEverything) {
                // The introspection above guarantees that the type
                // support is of a compatible type
                @SuppressWarnings("unchecked")
                TypeSupport<T> support = (TypeSupport<T>) supportMap.get(type);
                return support;
            }
        }

        throw new RuntimeException("Multiple support for type " + typeClass + " through " + superTypes);
    }

    private static void recursiveAddAllSuperTypes(Class clazz, Set<Class> superClasses) {
        // If already visited or null , return
        if (clazz == null || superClasses.contains(clazz)) {
            return;
        }

        superClasses.add(clazz);
        recursiveAddAllSuperTypes(clazz.getSuperclass(), superClasses);
        for (Class interf : clazz.getInterfaces()) {
            recursiveAddAllSuperTypes(interf, superClasses);
        }
    }
    /**
     * Creates a new type support of the given type
     * 
     * @param type the type on which support is defined
     * @param typeSupportFamily the kind of support is being defined
     */
    public TypeSupport(Class<T> type, Class<? extends TypeSupport> typeSupportFamily) {
        this.type = type;
        this.typeSupportFamily = typeSupportFamily;
    }

    // Type on which the support is defined
    private final Class<T> type;

    // Which kind of type support is defined
    private final Class<? extends TypeSupport> typeSupportFamily;


    /**
     * Defines which type of support is implementing, notification or time.
     *
     * @return the support family
     */
    protected Class<? extends TypeSupport> getTypeSupportFamily() {
        return typeSupportFamily;
    }

    /**
     * Defines on which class the support is defined.
     * 
     * @return the type of the class
     */
    protected Class<T> getType() {
        return type;
    }

}
