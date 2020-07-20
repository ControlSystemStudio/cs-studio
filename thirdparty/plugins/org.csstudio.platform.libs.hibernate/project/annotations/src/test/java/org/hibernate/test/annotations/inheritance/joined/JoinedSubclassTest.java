//$Id: JoinedSubclassTest.java 19907 2010-07-07 13:39:12Z sharathjreddy $
package org.hibernate.test.annotations.inheritance.joined;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.TestCase;

/**
 * @author Emmanuel Bernard
 */
public class JoinedSubclassTest extends TestCase {

	public JoinedSubclassTest(String x) {
		super( x );
	}

	public void testDefault() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		File doc = new Document( "Enron Stuff To Shred", 1000 );
		Folder folder = new Folder( "Enron" );
		s.persist( doc );
		s.persist( folder );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		List result = s.createCriteria( File.class ).list();
		assertNotNull( result );
		assertEquals( 2, result.size() );
		File f2 = (File) result.get( 0 );
		checkClassType( f2, doc, folder );
		f2 = (File) result.get( 1 );
		checkClassType( f2, doc, folder );
		s.delete( result.get( 0 ) );
		s.delete( result.get( 1 ) );
		tx.commit();
		s.close();
	}

	public void testManyToOneOnAbstract() throws Exception {
		Folder f = new Folder();
		f.setName( "data" );
		ProgramExecution remove = new ProgramExecution();
		remove.setAction( "remove" );
		remove.setAppliesOn( f );
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		s.persist( f );
		s.persist( remove );
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		remove = (ProgramExecution) s.get( ProgramExecution.class, remove.getId() );
		assertNotNull( remove );
		assertNotNull( remove.getAppliesOn().getName() );
		s.delete( remove );
		s.delete( remove.getAppliesOn() );
		tx.commit();
		s.close();

	}

	private void checkClassType(File fruitToTest, File f, Folder a) {
		if ( fruitToTest.getName().equals( f.getName() ) ) {
			assertFalse( fruitToTest instanceof Folder );
		}
		else if ( fruitToTest.getName().equals( a.getName() ) ) {
			assertTrue( fruitToTest instanceof Folder );
		}
		else {
			fail( "Result does not contains the previously inserted elements" );
		}
	}

	public void testJoinedAbstractClass() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		s.getTransaction().begin();
		Sweater sw = new Sweater();
		sw.setColor( "Black" );
		sw.setSize( 2 );
		sw.setSweat( true );
		s.persist( sw );
		s.getTransaction().commit();
		s.clear();

		s = openSession();
		s.getTransaction().begin();
		sw = (Sweater) s.get( Sweater.class, sw.getId() );
		s.delete( sw );
		s.getTransaction().commit();
		s.close();
	}

	public void testInheritance() throws Exception {
		Session session = openSession();
		Transaction transaction = session.beginTransaction();
		String eventPK = "event1";
		EventInformation event = (EventInformation) session.get( EventInformation.class, eventPK );
		if ( event == null ) {
			event = new EventInformation();
			event.setNotificationId( eventPK );
			session.persist( event );
		}
		String alarmPK = "alarm1";
		Alarm alarm = (Alarm) session.get( Alarm.class, alarmPK );
		if ( alarm == null ) {
			alarm = new Alarm();
			alarm.setNotificationId( alarmPK );
			alarm.setEventInfo( event );
			session.persist( alarm );
		}
		transaction.commit();
		session.close();
	}
	
	//HHH-4250 : @ManyToOne - @OneToMany doesn't work with @Inheritance(strategy= InheritanceType.JOINED)
	public void testManyToOneWithJoinTable() {

		Session s = openSession();
		Transaction tx = s.beginTransaction();
				
		Client c1 = new Client();
		c1.setFirstname("Firstname1");
		c1.setName("Name1");
		c1.setCode("1234");
		c1.setStreet("Street1");
		c1.setCity("City1");
		
		Account a1 = new Account();
		a1.setNumber("1000");
		a1.setBalance(5000.0);
		
		a1.addClient(c1);
		
		s.persist(c1);
		s.persist(a1);
	    
		s.flush();
		s.clear();
		
		c1 = (Client) s.load(Client.class, c1.getId());
		assertEquals(5000.0, c1.getAccount().getBalance());
		
		s.flush();
		s.clear();
		
		a1 = (Account) s.load(Account.class,a1.getId());
		Set<Client> clients = a1.getClients();
		assertEquals(1, clients.size());
		Iterator<Client> it = clients.iterator();
		c1 = it.next();
		assertEquals("Name1", c1.getName());
				
		tx.rollback();
		s.close();
	}
	
	/**
     *   HHH-4240 - SecondaryTables not recognized when using JOINED inheritance
	 */	
	public void testSecondaryTables() {
		
		Session s = openSession();
		s.getTransaction().begin();
		
		Company company = new Company();
		company.setCustomerName("Mama");
		company.setCustomerCode("123");
		company.setCompanyName("Mama Mia Pizza");
		company.setCompanyAddress("Rome");
		
		s.persist( company );
		s.getTransaction().commit();
		s.clear();
		
		s = openSession();
		s.getTransaction().begin();
		company = (Company) s.get( Company.class, company.getId());
		assertEquals("Mama", company.getCustomerName());
		assertEquals("123", company.getCustomerCode());
		assertEquals("Mama Mia Pizza", company.getCompanyName());
		assertEquals("Rome", company.getCompanyAddress());
				
		s.delete( company );
		s.getTransaction().commit();
		s.close();
	}
	

//	public void testManyToOneAndJoin() throws Exception {
//		Session session = openSession();
//		Transaction transaction = session.beginTransaction();
//		Parent parent = new Parent();
//		session.persist( parent );
//		PropertyAsset property = new PropertyAsset();
//		property.setParent( parent );
//		property.setPrice( 230000d );
//		FinancialAsset financial = new FinancialAsset();
//		financial.setParent( parent );
//		financial.setPrice( 230000d );
//		session.persist( financial );
//		session.persist( property );
//		session.flush();
//		session.clear();
//		parent = (Parent) session.get( Parent.class, parent.getId() );
//		assertNotNull( parent );
//		assertEquals( 1, parent.getFinancialAssets().size() );
//		assertEquals( 1, parent.getPropertyAssets().size() );
//		assertEquals( property.getId(), parent.getPropertyAssets().iterator().next() );
//		transaction.rollback();
//		session.close();
//	}

	@Override
	protected String[] getXmlFiles() {
		return new String[] {
				//"org/hibernate/test/annotations/inheritance/joined/Asset.hbm.xml"
		};
	}

	/**
	 * @see org.hibernate.test.annotations.TestCase#getAnnotatedClasses()
	 */
	protected Class[] getAnnotatedClasses() {
		return new Class[]{
				File.class,
				Folder.class,
				Document.class,
				SymbolicLink.class,
				ProgramExecution.class,
				Clothing.class,
				Sweater.class,
				EventInformation.class,
				Alarm.class,
				Client.class,
				Account.class,
				Customer.class,
				Company.class
				//Asset.class,
				//Parent.class,
				//PropertyAsset.class,
				//FinancialAsset.class
		};
	}

}
