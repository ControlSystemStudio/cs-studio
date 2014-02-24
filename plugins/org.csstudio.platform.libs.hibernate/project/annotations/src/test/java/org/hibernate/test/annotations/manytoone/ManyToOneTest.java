//$Id: ManyToOneTest.java 18602 2010-01-21 20:48:59Z hardy.ferentschik $
package org.hibernate.test.annotations.manytoone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.Company;
import org.hibernate.test.annotations.Customer;
import org.hibernate.test.annotations.Discount;
import org.hibernate.test.annotations.Flight;
import org.hibernate.test.annotations.Passport;
import org.hibernate.test.annotations.TestCase;
import org.hibernate.test.annotations.Ticket;

/**
 * @author Emmanuel Bernard
 */
public class ManyToOneTest extends TestCase {

	public ManyToOneTest(String x) {
		super( x );
	}

	public void testEager() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Color c = new Color();
		c.setName( "Yellow" );
		s.persist( c );
		Car car = new Car();
		car.setBodyColor( c );
		s.persist( car );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		car = (Car) s.get( Car.class, car.getId() );
		tx.commit();
		s.close();
		assertNotNull( car );
		assertNotNull( car.getBodyColor() );
		assertEquals( "Yellow", car.getBodyColor().getName() );

	}

	public void testDefaultMetadata() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Color c = new Color();
		c.setName( "Blue" );
		s.persist( c );
		Car car = new Car();
		car.setBodyColor( c );
		s.persist( car );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		car = (Car) s.get( Car.class, car.getId() );
		assertNotNull( car );
		assertNotNull( car.getBodyColor() );
		assertEquals( c.getId(), car.getBodyColor().getId() );
		tx.rollback();
		s.close();
	}

	public void testCreate() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Flight firstOne = new Flight();
		firstOne.setId( new Long( 1 ) );
		firstOne.setName( "AF0101" );
		firstOne.setDuration( new Long( 1000 ) );
		Company frenchOne = new Company();
		frenchOne.setName( "Air France" );
		firstOne.setCompany( frenchOne );
		s.persist( firstOne );
		tx.commit();
		s.close();
		assertNotNull( "identity id should work", frenchOne.getId() );

		s = openSession();
		tx = s.beginTransaction();
		firstOne = (Flight) s.get( Flight.class, new Long( 1 ) );
		assertNotNull( firstOne.getCompany() );
		assertEquals( frenchOne.getName(), firstOne.getCompany().getName() );
		tx.commit();
		s.close();
	}

	public void testCascade() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Discount discount = new Discount();
		discount.setDiscount( 20.12 );
		Customer customer = new Customer();
		Collection discounts = new ArrayList();
		discounts.add( discount );
		customer.setName( "Quentin Tarantino" );
		discount.setOwner( customer );
		customer.setDiscountTickets( discounts );
		s.persist( discount );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		discount = (Discount) s.get( Discount.class, discount.getId() );
		assertNotNull( discount );
		assertEquals( 20.12, discount.getDiscount() );
		assertNotNull( discount.getOwner() );
		customer = new Customer();
		customer.setName( "Clooney" );
		discount.setOwner( customer );
		discounts = new ArrayList();
		discounts.add( discount );
		customer.setDiscountTickets( discounts );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		discount = (Discount) s.get( Discount.class, discount.getId() );
		assertNotNull( discount );
		assertNotNull( discount.getOwner() );
		assertEquals( "Clooney", discount.getOwner().getName() );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		customer = (Customer) s.get( Customer.class, customer.getId() );
		s.delete( customer );
		tx.commit();
		s.close();
	}

	public void testFetch() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Discount discount = new Discount();
		discount.setDiscount( 20 );
		Customer customer = new Customer();
		Collection discounts = new ArrayList();
		discounts.add( discount );
		customer.setName( "Quentin Tarantino" );
		discount.setOwner( customer );
		customer.setDiscountTickets( discounts );
		s.persist( discount );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		discount = (Discount) s.get( Discount.class, discount.getId() );
		assertNotNull( discount );
		assertFalse( Hibernate.isInitialized( discount.getOwner() ) );
		tx.commit();

		s = openSession();
		tx = s.beginTransaction();
		discount = (Discount) s.load( Discount.class, discount.getId() );
		assertNotNull( discount );
		assertFalse( Hibernate.isInitialized( discount.getOwner() ) );
		tx.commit();

		s = openSession();
		tx = s.beginTransaction();
		s.delete( s.get( Discount.class, discount.getId() ) );
		tx.commit();
		s.close();
	}

	public void testCompositeFK() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		ParentPk ppk = new ParentPk();
		ppk.firstName = "John";
		ppk.lastName = "Doe";
		Parent p = new Parent();
		p.age = 45;
		p.id = ppk;
		s.persist( p );
		Child c = new Child();
		c.parent = p;
		s.persist( c );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		//FIXME: fix this when the small parser bug will be fixed
		Query q = s.createQuery( "from " + Child.class.getName() ); //+ " c where c.parent.id.lastName = :lastName");
		//q.setString("lastName", p.id.lastName);
		List result = q.list();
		assertEquals( 1, result.size() );
		Child c2 = (Child) result.get( 0 );
		assertEquals( c2.id, c.id );
		tx.commit();
		s.close();
	}

	public void testImplicitCompositeFk() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Node n1 = new Node();
		n1.setDescription( "Parent" );
		NodePk n1pk = new NodePk();
		n1pk.setLevel( 1 );
		n1pk.setName( "Root" );
		n1.setId( n1pk );
		Node n2 = new Node();
		NodePk n2pk = new NodePk();
		n2pk.setLevel( 2 );
		n2pk.setName( "Level 1: A" );
		n2.setParent( n1 );
		n2.setId( n2pk );
		s.persist( n2 );
		tx.commit();

		s = openSession();
		tx = s.beginTransaction();
		n2 = (Node) s.get( Node.class, n2pk );
		assertNotNull( n2 );
		assertNotNull( n2.getParent() );
		assertEquals( 1, n2.getParent().getId().getLevel() );
		tx.commit();
		s.close();
	}

	public void testManyToOneNonPk() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Order order = new Order();
		order.setOrderNbr( "123" );
		s.persist( order );
		OrderLine ol = new OrderLine();
		ol.setItem( "Mouse" );
		ol.setOrder( order );
		s.persist( ol );
		s.flush();
		s.clear();
		ol = (OrderLine) s.get( OrderLine.class, ol.getId() );
		assertNotNull( ol.getOrder() );
		assertEquals( "123", ol.getOrder().getOrderNbr() );
		assertTrue( ol.getOrder().getOrderLines().contains( ol ) );
		tx.rollback();
		s.close();
	}

	public void testTwoManyToOneNonPk() throws Exception {
		//2 many to one non pk pointing to the same referencedColumnName should not fail
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		org.hibernate.test.annotations.manytoone.Customer customer = new org.hibernate.test.annotations.manytoone.Customer();
		customer.userId="123";
		org.hibernate.test.annotations.manytoone.Customer customer2 = new org.hibernate.test.annotations.manytoone.Customer();
		customer2.userId="124";
		s.persist( customer2 );
		s.persist( customer );
		Deal deal = new Deal();
		deal.from = customer;
		deal.to = customer2;
		s.persist( deal );
		s.flush();
		s.clear();
		deal = (Deal) s.get( Deal.class, deal.id );
		assertNotNull( deal.from );
		assertNotNull( deal.to );
		tx.rollback();
		s.close();
	}

	public void testFormulaOnOtherSide() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Frame frame = new Frame();
		frame.setName( "Prada" );
		s.persist( frame );
		Lens l = new Lens();
		l.setFocal( 2.5f );
		l.setFrame( frame );
		s.persist( l );
		Lens r = new Lens();
		r.setFocal( 1.2f);
		r.setFrame( frame );
		s.persist( r );
		s.flush();
		s.clear();
		frame = (Frame) s.get( Frame.class, frame.getId() );
		assertEquals( 2, frame.getLenses().size() );
		assertTrue( frame.getLenses().iterator().next().getLength() <= 1/1.2f );
		assertTrue( frame.getLenses().iterator().next().getLength() >= 1/2.5f );
		tx.rollback();
		s.close();
	}

	/**
	 * @see org.hibernate.test.annotations.TestCase#getAnnotatedClasses()
	 */
	protected java.lang.Class[] getAnnotatedClasses() {
		return new java.lang.Class[]{
				Deal.class,
				org.hibernate.test.annotations.manytoone.Customer.class,
				Car.class,
				Color.class,
				Flight.class,
				Company.class,
				Customer.class,
				Discount.class,
				Ticket.class,
				Passport.class,
				Parent.class,
				Child.class,
				Node.class,
				User.class,
				DistrictUser.class,
				Order.class,
				OrderLine.class,
				Frame.class,
				Lens.class
		};
	}

}