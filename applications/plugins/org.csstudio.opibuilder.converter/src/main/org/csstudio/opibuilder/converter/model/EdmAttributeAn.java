package org.csstudio.opibuilder.converter.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation class for marking EdmAttributes, which should be specialized by specific parser.
 * 
 * @author Matevz
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EdmAttributeAn {

}