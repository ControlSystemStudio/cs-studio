//$Id: TicketComparator.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import java.util.Comparator;

/**
 * Ticket comparator ordering longest first
 *
 * @author Emmanuel Bernard
 */
public class TicketComparator implements Comparator<Ticket> {

	public int compare(Ticket ticket, Ticket ticket1) {
		if ( ticket == null || ticket1 == null ) {
			throw new IllegalStateException( "Ticket comparison only available through non null tickets" );
		}
		return ticket1.getNumber().length() - ticket.getNumber().length();

	}
}
