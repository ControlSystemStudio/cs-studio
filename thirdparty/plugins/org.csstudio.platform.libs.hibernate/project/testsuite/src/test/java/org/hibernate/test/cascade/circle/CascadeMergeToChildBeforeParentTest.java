//$Id: $
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */

package org.hibernate.test.cascade.circle;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

/**
 * The test case uses the following model:
 *
 *                         <-    ->
 *                      -- (N : 0,1) -- Tour
 *                      |    <-   ->
 *                      | -- (1 : N) -- (pickup) ----
 *               ->     | |                         |
 * Route -- (1 : N) - Node                      Transport
 *   |                    |  <-   ->                |  |
 *   |                    -- (1 : N) -- (delivery) --  |
 *   |                                                 |
 *   |             ->                    ->            |
 *   -------- (1 : N) ---- Vehicle--(1 : N)------------
 *
 * Arrows indicate the direction of cascade-merge.
 * 
 * I believe it reproduces the following issue:
 *    http://opensource.atlassian.com/projects/hibernate/browse/HHH-3544
 *
 * @author Gail Badner (based on original model provided by Pavol Zibrita)
 */
public class CascadeMergeToChildBeforeParentTest extends FunctionalTestCase {

	public CascadeMergeToChildBeforeParentTest(String string) {
		super(string);
	}

	public String[] getMappings() {
		return new String[] {
				"cascade/circle/CascadeMergeToChildBeforeParent.hbm.xml"
		};
	}
	
	public static Test suite() {
		return new FunctionalTestClassTestSuite( CascadeMergeToChildBeforeParentTest.class );
	}
	
	protected void cleanupTest() {
		Session s = openSession();
		s.beginTransaction();
		s.createQuery( "delete from Transport" );
		s.createQuery( "delete from Tour" );
		s.createQuery( "delete from Node" );
		s.createQuery( "delete from Route" );
		s.createQuery( "delete from Vehicle" );
	}

	public void testMerge()
	{
		Session s = openSession();
		s.beginTransaction();

		Route route = new Route();
		route.setName("routeA");

		s.save( route );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();

		route = (Route) s.get(Route.class, new Long(1));

		route.setTransientField(new String("sfnaouisrbn"));

		Tour tour = new Tour();
		tour.setName("tourB");

		Node pickupNode = new Node();
		pickupNode.setName("pickupNodeB");

		Node deliveryNode = new Node();
		deliveryNode.setName("deliveryNodeB");

		pickupNode.setRoute(route);
		pickupNode.setTour(tour);
		pickupNode.setTransientField("pickup node aaaaaaaaaaa");

		deliveryNode.setRoute(route);
		deliveryNode.setTour(tour);
		deliveryNode.setTransientField("delivery node aaaaaaaaa");

		tour.getNodes().add(pickupNode);
		tour.getNodes().add(deliveryNode);

		route.getNodes().add(pickupNode);
		route.getNodes().add(deliveryNode);

		Route mergedRoute = (Route) s.merge(route);

		s.getTransaction().commit();
		s.close();
	}

	// This test fails because the merge algorithm tries to save a
	// transient child (transport) before cascade-merge gets its
	// transient parent (vehicle); merge does not cascade from the
	// child to the parent.
	public void testMergeTransientChildBeforeTransientParent()
	{
		Session s = openSession();
		s.beginTransaction();

		Route route = new Route();
		route.setName("routeA");

		s.save( route );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();

		route = (Route) s.get(Route.class, new Long(1));

		route.setTransientField(new String("sfnaouisrbn"));

		Tour tour = new Tour();
		tour.setName("tourB");

		Transport transport = new Transport();
		transport.setName("transportB");

		Node pickupNode = new Node();
		pickupNode.setName("pickupNodeB");

		Node deliveryNode = new Node();
		deliveryNode.setName("deliveryNodeB");

		Vehicle vehicle = new Vehicle();
		vehicle.setName("vehicleB");

		pickupNode.setRoute(route);
		pickupNode.setTour(tour);
		pickupNode.getPickupTransports().add(transport);
		pickupNode.setTransientField("pickup node aaaaaaaaaaa");

		deliveryNode.setRoute(route);
		deliveryNode.setTour(tour);
		deliveryNode.getDeliveryTransports().add(transport);
		deliveryNode.setTransientField("delivery node aaaaaaaaa");

		tour.getNodes().add(pickupNode);
		tour.getNodes().add(deliveryNode);

		route.getNodes().add(pickupNode);
		route.getNodes().add(deliveryNode);
		route.getVehicles().add(vehicle);

		transport.setPickupNode(pickupNode);
		transport.setDeliveryNode(deliveryNode);
		transport.setVehicle( vehicle );
		transport.setTransientField("aaaaaaaaaaaaaa");

		vehicle.getTransports().add(transport);
		vehicle.setTransientField( "anewvalue" );
		vehicle.setRoute( route );

		Route mergedRoute = (Route) s.merge(route);

		s.getTransaction().commit();
		s.close();
	}

	public void testMergeData3Nodes()
	{

		Session s = openSession();
		s.beginTransaction();

		Route route = new Route();
		route.setName("routeA");

		s.save( route );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();

		route = (Route) s.get(Route.class, new Long(1));

		route.setTransientField(new String("sfnaouisrbn"));

		Tour tour = new Tour();
		tour.setName("tourB");

		Transport transport1 = new Transport();
		transport1.setName("TRANSPORT1");

		Transport transport2 = new Transport();
		transport2.setName("TRANSPORT2");

		Node node1 = new Node();
		node1.setName("NODE1");

		Node node2 = new Node();
		node2.setName("NODE2");

		Node node3 = new Node();
		node3.setName("NODE3");

		Vehicle vehicle = new Vehicle();
		vehicle.setName("vehicleB");

		node1.setRoute(route);
		node1.setTour(tour);
		node1.getPickupTransports().add(transport1);
		node1.setTransientField("node 1");

		node2.setRoute(route);
		node2.setTour(tour);
		node2.getDeliveryTransports().add(transport1);
		node2.getPickupTransports().add(transport2);
		node2.setTransientField("node 2");

		node3.setRoute(route);
		node3.setTour(tour);
		node3.getDeliveryTransports().add(transport2);
		node3.setTransientField("node 3");

		tour.getNodes().add(node1);
		tour.getNodes().add(node2);
		tour.getNodes().add(node3);

		route.getNodes().add(node1);
		route.getNodes().add(node2);
		route.getNodes().add(node3);
		route.getVehicles().add(vehicle);

		transport1.setPickupNode(node1);
		transport1.setDeliveryNode(node2);
		transport1.setVehicle( vehicle );
		transport1.setTransientField("aaaaaaaaaaaaaa");

		transport2.setPickupNode(node2);
		transport2.setDeliveryNode(node3);
		transport2.setVehicle( vehicle );
		transport2.setTransientField("bbbbbbbbbbbbb");

		vehicle.getTransports().add(transport1);
		vehicle.getTransports().add(transport2);
		vehicle.setTransientField( "anewvalue" );
		vehicle.setRoute( route );

		Route mergedRoute = (Route) s.merge(route);

		s.getTransaction().commit();
		s.close();
	}
	
}
