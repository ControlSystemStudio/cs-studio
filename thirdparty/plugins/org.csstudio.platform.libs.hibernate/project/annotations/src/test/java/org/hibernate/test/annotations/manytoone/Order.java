//$Id: Order.java 17524 2009-09-17 17:36:24Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.OneToMany;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name="ORDERS")
public class Order implements Serializable {
	private Integer id;
	private String orderNbr;
	private Set<OrderLine> orderLines;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="order_nbr")
	public String getOrderNbr() {
		return orderNbr;
	}

	public void setOrderNbr(String orderNbr) {
		this.orderNbr = orderNbr;
	}

	@OneToMany(mappedBy = "order")
	public Set<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(Set<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}
}
