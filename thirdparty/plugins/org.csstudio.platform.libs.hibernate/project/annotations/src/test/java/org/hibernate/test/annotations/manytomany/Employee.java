//$Id: Employee.java 18543 2010-01-13 19:35:54Z smarlow@redhat.com $
package org.hibernate.test.annotations.manytomany;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Column;

import org.hibernate.annotations.Cascade;

/**
 * Employee in an Employer-Employee relationship
 *
 * @author Emmanuel Bernard
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("serial")
public class Employee implements Serializable {
	private Integer id;
	private Collection<Employer> employers;
	private String name;
	ContactInfo contactInfo;
	JobInfo jobInfo;

	// ContactInfo is for ManyToMany testing
	@Embedded
	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	// JobInfo is for OneToMany testing
	@Embedded
	public JobInfo getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}


	@Column(name="fld_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer integer) {
		id = integer;
	}

	@ManyToMany(
			cascade = {CascadeType.PERSIST, CascadeType.MERGE},
			mappedBy = "employees"
	)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.PERSIST})
	public Collection<Employer> getEmployers() {
		return employers;
	}

	public void setEmployers(Collection<Employer> employers) {
		this.employers = employers;
	}
}
