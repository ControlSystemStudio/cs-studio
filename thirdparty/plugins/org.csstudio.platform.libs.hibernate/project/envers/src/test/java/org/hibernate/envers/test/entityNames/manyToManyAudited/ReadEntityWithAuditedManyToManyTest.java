package org.hibernate.envers.test.entityNames.manyToManyAudited;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.MappingException;
import org.hibernate.envers.test.AbstractSessionTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Hern�n Chanfreau
 * 
 */

public class ReadEntityWithAuditedManyToManyTest extends AbstractSessionTest{

	private long id_car1;
	private long id_car2;
	
	private long id_pers1;
	
	protected void initMappings() throws MappingException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("mappings/entityNames/manyToManyAudited/mappings.hbm.xml");
        config.addFile(new File(url.toURI()));
	}
	
	
    @BeforeClass(dependsOnMethods = "init")
    public void initData() {
    	
    	newSessionFactory();

        Person pers1 = new Person("Hernan", 28);
        Person pers2 = new Person("Leandro", 29);
        Person pers3 = new Person("Barba", 32);
        Person pers4 = new Person("Camomo", 15);

        //REV 1 
        getSession().getTransaction().begin();
        List<Person > owners = new ArrayList<Person>();
        owners.add(pers1);
        owners.add(pers2);
        owners.add(pers3);
        Car car1 = new Car(5, owners);

        getSession().persist(car1);
        getSession().getTransaction().commit();
        id_pers1 = pers1.getId();
        id_car1 = car1.getId();

        owners = new ArrayList<Person>();
        owners.add(pers2);
        owners.add(pers3);
        owners.add(pers4);
        Car car2 = new Car(27, owners);
        //REV 2
        getSession().getTransaction().begin();
        Person person1 = (Person)getSession().get("Personaje", id_pers1);
        person1.setName("Hernan David");
        person1.setAge(40);
        getSession().persist(car1);
        getSession().persist(car2);
        getSession().getTransaction().commit();
        id_car2 = car2.getId();

    }
    
    @Test
    public void testObtainManyYoManyWithEntityName() {
    	
    	Car car1 = getAuditReader().find(Car.class, id_car1, 2);
    	Car car2 = getAuditReader().find(Car.class, id_car2, 2);

    	System.out.println("  > Car: " + car1.getNumber());
    	System.out.println("  > Owners:");
    	for (Person owner : car1.getOwners()) {
    		System.out.println("    > Name: " + owner.getName() + " - Age:" + owner.getAge());
    		System.out.println("    > Cars owned:");
    		for (Car ownedCar : owner.getCars()) {
				System.out.println("      o Car: " + ownedCar.getNumber());
			}
		}
    	System.out.println("  > Car: " + car2.getNumber());
    	System.out.println("  > Owners:");
    	for (Person owner : car2.getOwners()) {
    		System.out.println("    > Name: " + owner.getName() + " - Age:" + owner.getAge());
    		System.out.println("    > Cars owned:");
    		for (Car ownedCar : owner.getCars()) {
				System.out.println("      o Car: " + ownedCar.getNumber());
			}
		}
    }
    

}
