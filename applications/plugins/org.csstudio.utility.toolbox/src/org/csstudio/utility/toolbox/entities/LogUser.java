package org.csstudio.utility.toolbox.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.utility.toolbox.framework.annotations.ReadOnly;
import org.csstudio.utility.toolbox.framework.binding.TextValue;

@Entity
@Table(name = "loguser")
@NamedQueries({ @NamedQuery(name = LogUser.FIND_ALL, query = "from LogUser l order by l.accountname") })
public class LogUser implements TextValue {

	public static final String FIND_ALL = "LogUser.findAll";

	@Id
	@Column(name = "accountname")
	@ReadOnly
	private String accountname;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "loggroup")
	private String loggroup;

	@Column(name = "email")
	private String email;

	public static enum UserGetValueProperty{ACCOUNT_NAME, ACCOUNT_EMAIL}

	@Transient
	private UserGetValueProperty valueProperty = UserGetValueProperty.ACCOUNT_NAME;
	
	public String getAccountname() {
		return accountname;
	}
	
	public void setAccountname(String value) {
		this.accountname = value;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getLoggroup() {
		return loggroup;
	}

	public String getEmail() {
		return email;
	}
	
	public void setValueProperty(UserGetValueProperty valueProperty) {
		this.valueProperty = valueProperty;
	}

	@Override
	public String getValue() {
		if (valueProperty == UserGetValueProperty.ACCOUNT_NAME) {
			return accountname;
		} else if (valueProperty == UserGetValueProperty.ACCOUNT_EMAIL) {
			return email;
		} else {
			throw new IllegalStateException("Unknown valueProperty");
		}
	}

}
