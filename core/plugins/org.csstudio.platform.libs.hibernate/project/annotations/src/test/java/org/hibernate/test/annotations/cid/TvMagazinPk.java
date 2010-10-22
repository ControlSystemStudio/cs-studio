//$Id: TvMagazinPk.java 17977 2009-11-13 18:15:10Z hardy.ferentschik $
package org.hibernate.test.annotations.cid;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public class TvMagazinPk implements Serializable {
	@ManyToOne
	public Channel channel;
	
	@ManyToOne
	public Presenter presenter;
}
