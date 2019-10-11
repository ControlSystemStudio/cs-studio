package org.hibernate.test.annotations.derivedidentities.e2.b;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public class DependentId implements Serializable {
	String name;
	EmployeeId empPK;
}