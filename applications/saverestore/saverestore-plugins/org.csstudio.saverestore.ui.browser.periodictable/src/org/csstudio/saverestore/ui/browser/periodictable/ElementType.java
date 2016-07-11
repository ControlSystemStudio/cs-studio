/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui.browser.periodictable;

/**
 *
 * <code>ElementType</code> defines all possible element types in the periodic table. They type is used to determine the
 * color of the element.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public enum ElementType {
    ALKALI_METAL,
    ALKALINE_EARTH_METAL,
    LANTHANIDE,
    ACTINIDE,
    TRANSITION_METAL,
    POST_TRANSITION_METAL,
    METALLOID,
    POLYATOMIC_NONMETAL,
    DIATOMIC_NONMETAL,
    NOBLE_GAS,
    UNKNOWN
}
