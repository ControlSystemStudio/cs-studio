//$Id: Night.java 17530 2009-09-22 17:19:24Z epbernard $
package org.hibernate.test.annotations.query;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

/**
 * Entity declaring a named query
 *
 * @author Emmanuel Bernard
 */
@Entity
@NamedQuery(name = "night.moreRecentThan", query = "select n from Night n where n.date >= :date")
@org.hibernate.annotations.NamedQuery(
		name = "night.duration",
		query = "select n from Night n where n.duration = :duration",
		cacheable = true, cacheRegion = "nightQuery"
)
public class Night extends Darkness {
	private Integer id;
	private long duration;
	private Date date;
	private Area area;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "night_duration")
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Column(name = "night_date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
}
