//$Id: Sky.java 16414 2009-04-23 05:56:50Z epbernard $
package org.hibernate.test.annotations;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Emmanuel Bernard
 */
@Entity
@Table(name = "tbl_sky",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"month", "day"})}
)
public class Sky implements Serializable {
	@Id
	protected Long id;
	@Column(unique = true, columnDefinition = "varchar(250)", nullable = false)
	protected String color;
	@Column(nullable = false)
	protected String day;
	@Column(name = "MONTH", nullable = false)
	protected String month;
	static protected String area;
}
