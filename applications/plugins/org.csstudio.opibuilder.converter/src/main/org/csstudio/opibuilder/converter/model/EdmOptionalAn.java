package org.csstudio.opibuilder.converter.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation class for marking optional EdmAttributes, 
 * which are not always specified in EDM data model.
 * 
 * @author Matevz
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EdmOptionalAn {

}