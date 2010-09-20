//$Id: Author.java 16383 2009-04-21 14:02:32Z jcosta@redhat.com $
package org.hibernate.ejb.test.cascade;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Author {
 @Id @GeneratedValue
 private Long id;

}
