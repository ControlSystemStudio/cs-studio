//$Id: Customer.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.idmanytoone;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * @author Emmanuel Bernard
 */



@Entity
@Table(name = "Bs")
public class Customer implements Serializable {
    @Id @GeneratedValue
	public Integer id;

    @OneToMany(mappedBy = "customer")
    public Set<StoreCustomer> stores;

    private static final long serialVersionUID = 3818501706063039923L;
}
