//$Id: Paper.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.generics;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Paper extends Item<PaperType, SomeGuy> {
}
