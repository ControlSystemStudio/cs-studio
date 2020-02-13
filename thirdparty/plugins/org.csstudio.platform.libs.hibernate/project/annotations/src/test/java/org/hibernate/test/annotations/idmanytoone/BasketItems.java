//$
package org.hibernate.test.annotations.idmanytoone;

import java.io.Serializable;
import javax.persistence.IdClass;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.Basic;
import javax.persistence.JoinColumns;
import javax.persistence.Entity;

@Entity
@Table(name="BasketItems")
@org.hibernate.annotations.Proxy(lazy=false)
@IdClass(BasketItemsPK.class)
public class BasketItems implements Serializable {

	private static final long serialVersionUID = -4580497316918713849L;

	@Id
	@ManyToOne(cascade={ CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumns({ @JoinColumn(name="basketDatetime", referencedColumnName="basketDatetime"), @JoinColumn(name="customerID", referencedColumnName="customerID") })
	@Basic(fetch= FetchType.LAZY)
	private ShoppingBaskets shoppingBaskets;

	@Column(name="cost", nullable=false)
	@Id
	private Double cost;

	public void setCost(double value) {
		setCost(new Double(value));
	}

	public void setCost(Double value) {
		this.cost = value;
	}

	public Double getCost() {
		return cost;
	}

	public void setShoppingBaskets(ShoppingBaskets value) {
		this.shoppingBaskets = value;
	}

	public ShoppingBaskets getShoppingBaskets() {
		return shoppingBaskets;
	}

}

