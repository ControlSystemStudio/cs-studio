/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.model;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.FontData;

/**
 * The types that can be used for properties of SDS widget models. It is not
 * guaranteed that these types are matched to JAVA types of the same of similar
 * name.
 *
 * @author Sven Wende, Stefan Hofer, Kai Meyer
 * @version $Revision: 1.2 $
 *
 */
public enum PropertyTypesEnum {

    /**
     * An array of double values.
     */
    DOUBLEARRAY("sds.doublearray", double[].class, ValueType.DOUBLE_SEQUENCE), //$NON-NLS-1$

    /**
     * An option.
     */
    OPTION("sds.choiceoption", String.class, ValueType.STRING), //$NON-NLS-1$

    /**
     * An option.
     */
    ARRAYOPTION("sds.option", Integer.class, ValueType.LONG), //$NON-NLS-1$

    /**
     * A double value.
     */
    DOUBLE("sds.double", Double.class, ValueType.DOUBLE), //$NON-NLS-1$

    /**
     * An integer value.
     */
    INTEGER("sds.integer", Integer.class, ValueType.LONG), //$NON-NLS-1$

    /**
     * A string.
     */
    STRING("sds.string", String.class, ValueType.DOUBLE), //$NON-NLS-1$

    /**
     * A parameterized string.
     */
    PARAMSTRING("sds.paramstring", String.class, ValueType.DOUBLE), //$NON-NLS-1$

    /**
     * A boolean.
     */
    BOOLEAN("sds.boolean", Boolean.class, ValueType.DOUBLE), //$NON-NLS-1$

    /**
     * A color.
     */
    COLOR("sds.color", String.class, ValueType.LONG), //$NON-NLS-1$

    /**
     * A font.
     */
    FONT("sds.font", FontData.class, ValueType.LONG), //$NON-NLS-1$

    /**
     * A list of points (x/y).
     */
    POINT_LIST("sds.pointlist", PointList.class, ValueType.LONG), //$NON-NLS-1$

    /**
     * A Map. The key and the value is a String
     */
    MAP("sds.map", Map.class, ValueType.LONG),

    /**
     * A IResource.
     */
    RESOURCE("sds.path", IPath.class, ValueType.LONG),

    /**
     * A ProcessVariable.
     */
    PROCESSVARIABLE("sds.pv", IProcessVariableAddress.class, ValueType.LONG),

    /**
     * A Action.
     */
    ACTION("sds.action", ActionData.class, ValueType.LONG),

    /**
     * A Behavior
     */
    BEHAVIOR("sds.behavior", String.class, ValueType.STRING);

    /**
     * The ID of the property type. Will be used as portable representation of
     * the created instance.
     */
    private String _id;

    /**
     * The Java type, which is expected for property values.
     */
    @SuppressWarnings("unchecked")
    private Class _javaType;

    /**
     * A hint for the necessary DAL property type.
     */
    private ValueType _valueType;

    /**
     * Constructor.
     *
     * @param id
     *            The ID. Will be used as portable representation of the created
     *            instance.
     * @param javaType
     *            the Java type, which is expected for property values
     * @param valueType
     *            a hint for the necessary DAL property type
     */
    @SuppressWarnings("unchecked")
    private PropertyTypesEnum(final String id, final Class javaType,
            final ValueType valueType) {
        assert id != null;
        assert javaType != null;
        assert valueType != null;
        _id = id;
        _javaType = javaType;
        _valueType = valueType;
    }

    /**
     * Returns the Java type, which is expected for property values.
     *
     * @return the Java type, which is expected for property values
     */
    @SuppressWarnings("unchecked")
    public Class getJavaType() {
        return _javaType;
    }

    /**
     * Returns a hint for the necessary DAL property type.
     *
     * @return a hint for the necessary DAL property type
     */
    public ValueType getTypeHint() {
        return _valueType;
    }

    /**
     * @return An ID that allows for persisting and recreating instances of this
     *         class.
     */
    public String toPortableString() {
        return _id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _id;
    }

    /**
     * A map that contains all instances of this class.
     */
    private static Map<String, PropertyTypesEnum> _mapping;

    static {
        _mapping = new HashMap<String, PropertyTypesEnum>();

        for (PropertyTypesEnum type : PropertyTypesEnum.values()) {
            _mapping.put(type.toPortableString(), type);
        }
    }

    /**
     * Creates an instance of this class from a string representation.
     *
     * @param portableString
     *            Required.
     * @return The instance that is represented by the string.
     * @throws Exception
     *             Thrown if the string does not represent an instance of this
     *             class.
     */
    public static PropertyTypesEnum createFromPortable(
            final String portableString) throws Exception {
        assert portableString != null;

        PropertyTypesEnum result = _mapping.get(portableString);

        if (result == null) {
            throw new Exception(
                    "No type exists for the id " + portableString + "!"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return result;
    }

    /**
     * Creates an instance of this class from a java class.
     *
     * @param type
     *            Required.
     * @return The instance that is represented by the class.
     * @throws RuntimeException
     *             Thrown if the class does not belong to an instance of this
     *             class.
     */
    @SuppressWarnings("unchecked")
    public static PropertyTypesEnum createFromJavaType(final Class type) throws RuntimeException {
        for (PropertyTypesEnum pte : PropertyTypesEnum.values()) {
            if (pte.getJavaType().equals(type)) {
                return pte;
            }
        }
        throw new RuntimeException(
                "No type exists for the class " + type + "!"); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
