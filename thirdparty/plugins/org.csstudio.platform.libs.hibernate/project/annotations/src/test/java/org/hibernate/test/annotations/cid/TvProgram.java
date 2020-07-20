//$Id: TvProgram.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.cid;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Chandra Patni
 */
@Entity
@SecondaryTable( name = "TV_PROGRAM_EXT", pkJoinColumns = {
@PrimaryKeyJoinColumn( name = "CHANNEL_ID" ),
@PrimaryKeyJoinColumn( name = "PRESENTER_NAME" )
		} )
public class TvProgram {
	@EmbeddedId
	public TvMagazinPk id;

	@Temporal( TemporalType.TIME )
	Date time;

	@Column( name = "TXT", table = "TV_PROGRAM_EXT" )
	public String text;

}