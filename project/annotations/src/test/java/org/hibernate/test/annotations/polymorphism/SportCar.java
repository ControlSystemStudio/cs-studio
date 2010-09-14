//$Id: SportCar.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.polymorphism;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.PolymorphismType;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name = "sport_car")
@org.hibernate.annotations.Entity(polymorphism = PolymorphismType.EXPLICIT) //raise a warn
public class SportCar extends Car {
}
