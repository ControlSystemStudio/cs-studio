package org.csstudio.dct.model;



/**
 * Represents an instance.
 * 
 * In the most simple case an instance is derived from a prototype and inherits
 * all of its records.
 * 
 * If a prototype contains instances of other prototypes, these are inherited as
 * well. In this case, the prototype instance will get the parent instance.
 * 
 * Instances are generally marked as "inheritedFromPrototype" when they are
 * inherited via a prototype.
 * 
 * Example:
 * 
 * Prototype P1 contains two records R11 and R12.
 * 
 * Prototype P2 contains a record R21 and an P1-instance IN1. IN1 will contain
 * two records R11' (parent = R11) and R12' (parent = R12).
 * 
 * An P2-instance IN2 will now contain a record R21' (parent=R21) and an
 * P1-instance IN1'' (parent = IN1'). IN1'' will contain two records R11''
 * (parent = R11') and R12'' (parent = R12').
 * 
 * To conclude, IN1' and IN1'' are "inheritedFromPrototype" while IN1 and IN2
 * are not.
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