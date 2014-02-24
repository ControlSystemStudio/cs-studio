// $Id: package-info.java 19255 2010-04-21 01:57:44Z steve.ebersole@jboss.com $
@AnyMetaDefs(
		@AnyMetaDef( name= "Property", metaType = "string", idType = "integer",
			metaValues = {
					@MetaValue(value = "C", targetEntity = CharProperty.class),
					@MetaValue(value = "I", targetEntity = IntegerProperty.class),
					@MetaValue(value = "S", targetEntity = StringProperty.class),
					@MetaValue(value = "L", targetEntity = LongProperty.class)
					})
)

package org.hibernate.test.annotations.any;

import org.hibernate.annotations.AnyMetaDefs;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;