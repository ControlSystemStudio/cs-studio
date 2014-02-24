//$Id: ZipCode.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Emmanuel Bernard
 */
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@org.hibernate.annotations.Entity(mutable = false)
public class ZipCode {
	@Id
	public String code;
}
