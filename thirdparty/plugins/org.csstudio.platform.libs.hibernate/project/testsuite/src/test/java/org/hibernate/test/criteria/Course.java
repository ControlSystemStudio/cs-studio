//$Id: Course.java 5686 2005-02-12 07:27:32Z steveebersole $
package org.hibernate.test.criteria;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gavin King
 */
public class Course {
	private String courseCode;
	private String description;
	private Set courseMeetings = new HashSet();

	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set getCourseMeetings() {
		return courseMeetings;
	}
	public void setCourseMeetings(Set courseMeetings) {
		this.courseMeetings = courseMeetings;
	}
}
