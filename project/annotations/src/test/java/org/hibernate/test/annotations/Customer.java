//$Id: Customer.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import java.io.Serializable;
import java.util.Collection;
import java.util.SortedSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import static org.hibernate.annotations.CascadeType.ALL;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;


/**
 * Company customer
 *
 * @author Emmanuel Bernard
 */
@Entity
public class Customer implements Serializable {
	Long id;
	String name;
	SortedSet<Ticket> tickets;
	Collection discountTickets;
	Passport passport;

	public Customer() {
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(Long long1) {
		id = long1;
	}

	public void setName(String string) {
		name = string;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "CUST_ID")
	@Sort(type = SortType.COMPARATOR, comparator = TicketComparator.class)
	public SortedSet<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(SortedSet<Ticket> tickets) {
		this.tickets = tickets;
	}

	@OneToMany(targetEntity = org.hibernate.test.annotations.Discount.class,
			cascade = CascadeType.ALL, mappedBy = "owner")
	@Cascade({ALL})
	public Collection getDiscountTickets() {
		return discountTickets;
	}

	public void setDiscountTickets(Collection collection) {
		discountTickets = collection;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Passport getPassport() {
		return passport;
	}

	public void setPassport(Passport passport) {
		this.passport = passport;
	}

}

