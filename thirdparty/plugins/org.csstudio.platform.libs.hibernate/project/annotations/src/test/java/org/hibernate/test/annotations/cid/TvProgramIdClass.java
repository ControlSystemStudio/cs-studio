//$Id: TvProgramIdClass.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.cid;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@SecondaryTable( name = "TV_PROGRAM_IDCLASS", pkJoinColumns =
		{
		@PrimaryKeyJoinColumn( name = "CHANNEL_ID" ),
		@PrimaryKeyJoinColumn( name = "PRESENTER_NAME" )
				} )
@IdClass( TvMagazinPk.class )
public class TvProgramIdClass {
	@Id
	public Channel channel;
	@Id
	public Presenter presenter;

	@Temporal( TemporalType.TIME )
	Date time;

	@Column( name = "TXT", table = "TV_PROGRAM_IDCLASS" )
	public String text;
}


