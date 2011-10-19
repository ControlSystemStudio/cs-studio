/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.epics.name;

import java.util.regex.Matcher;

import javax.annotation.Nonnull;

/**
 * Epics Name Specification.<br/>
 * According to EPICS Application Developers Guide - EPICS Base Release 3.14.11 25 February 2010.
 *
 * @author bknerr
 * @since 24.06.2011
 */
public class EpicsChannelName {

    public static final Integer MAX_BASENAME_LENGTH = 60;
    /**
     * Regex specifying the permitted structure of an EPICS channel base name.<br/>
     * Any out of: a-z A-Z 0-9 _ - + : [ ] < > ;
     */
    public static final String BASENAME_REGEX = "[a-zA-Z0-9_\\+:;<>\\[\\]-]{1," + MAX_BASENAME_LENGTH + "}";
    //

    public static final Integer MAX_FIELD_LENGTH = 4;
    public static final String FIELD_SEP = ".";
    /**
     * The {@link java.lang.String#split(String)} method does not like the
     * {@link Matcher#quoteReplacement(String)} output as input... try it and cry.
     */
    public static final String FIELD_SEP_FOR_SPLIT = "\\" + FIELD_SEP;

    /**
     * Regex specifying the permitted structure of an EPICS channel record field name.
     */
    public static final String FIELD_REGEX = "[A-Z0-9]{1," + MAX_FIELD_LENGTH + "}";
    public static final String FIELD_SEP_REGEX = Matcher.quoteReplacement(FIELD_SEP);
    public static final String FULLNAME_REGEX = BASENAME_REGEX +
                                                "(" + FIELD_SEP_REGEX +
                                                FIELD_REGEX + ")?";

    private final String _baseName;
    private final IRecordField _field;

    /**
     * Constructor.
     */
    public EpicsChannelName(@Nonnull final String fullName) {
        _baseName = EpicsNameSupport.parseBaseName(fullName);
       _field = EpicsNameSupport.parseField(fullName);
    }

    /**
     * Constructor.
     */
    public EpicsChannelName(@Nonnull final String baseName,
                            @Nonnull final IRecordField field) {

        _baseName = EpicsNameSupport.parseBaseName(baseName);
        _field = field;
    }

    @Nonnull
    public String getBaseName() {
        return _baseName;
    }

    @Nonnull
    public IRecordField getField() {
        return _field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String toString() {
        return _baseName + FIELD_SEP + _field.getFieldName();
    }
}
