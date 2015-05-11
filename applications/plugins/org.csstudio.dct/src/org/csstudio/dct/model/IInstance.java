package org.csstudio.dct.model;



/**
 * Represents an instance.
 *
 * @author Sven Wende
 *
 */
public interface IInstance extends  IContainer {


    /**
     * Returns the prototype this instances is derived from.
     *
     * @return the prototype
     */
    IPrototype getPrototype();



}
