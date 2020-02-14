//$Id: VideoSystem.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.callbacks;

import javax.persistence.EntityListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.PrePersist;

/**
 * @author Emmanuel Bernard
 */
@ExcludeSuperclassListeners
@EntityListeners({FirstOneListener.class, IncreaseListener.class})
@MappedSuperclass
public class VideoSystem extends CommunicationSystem {
	public transient int counter = 0;
	@PreUpdate
	public void increase() {
		isFirst = false;
		isLast = false;
		communication++;
	}

	@PrePersist
	public void prepareEntity() {
		counter++;
	}
}
