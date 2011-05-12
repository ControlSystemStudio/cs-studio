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
package org.csstudio.domain.desy.preferences;

import javax.annotation.Nonnull;

/**
 * Test Helper class.  
 * 
 * @author bknerr
 * @since 20.04.2011
 * @param <T> the preference type
 */
final class HeadlessTestPreference<T> extends AbstractPreference<T> {

    /**
     * For test purposes
     */
    @SuppressWarnings("unused")
    private final Integer _notTestPreference = Integer.valueOf(0);

    public static final HeadlessTestPreference<String> STRING_PREF =
        new HeadlessTestPreference<String>("String_Pref", "Some string");

    public static final HeadlessTestPreference<Integer> INT_PREF =
        new HeadlessTestPreference<Integer>("Int_Pref", 1234);

    public static final HeadlessTestPreference<Long> LONG_PREF =
        new HeadlessTestPreference<Long>("Long_Pref", 1234L);

    public static final HeadlessTestPreference<Float> FLOAT_PREF =
        new HeadlessTestPreference<Float>("Float_Pref", 12.34f);

    public static final HeadlessTestPreference<Double> DOUBLE_PREF =
        new HeadlessTestPreference<Double>("Double_Pref", 12.34);

    public static final HeadlessTestPreference<Double> DOUBLE_PREF_WITH_VAL =
        (HeadlessTestPreference<Double>) new HeadlessTestPreference<Double>("Double_Pref", 
                               12.34).addValidator(new MinMaxPreferenceValidator<Double>(0.0, 100.0));

    public static final HeadlessTestPreference<Boolean> BOOLEAN_PREF =
        new HeadlessTestPreference<Boolean>("Boolean_Pref", true);

    /**
     * For test purposes
     */
    public static final Integer STATIC_NOT_TESTPREFERENCE = new Integer(0);


    /**
     * The following two lines of a non static instance field of type <itself> enable an
     * infinite recursion while constructing the object => stack overflow.
     *
     * public final TestPreference<Boolean> NOT_STATIC =
     *     new TestPreference<Boolean>("NOT_STATIC", true);
     */
    private HeadlessTestPreference(@Nonnull final String keyAsString, 
                           @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @Override
    @Nonnull 
    public String getPluginID() {
        return "QualifierForTest";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Nonnull 
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) HeadlessTestPreference.class;
    }

}
