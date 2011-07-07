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
package org.csstudio.domain.desy;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;


/**
 * Run context introduced for the case where it cannot be avoided to check whether code should run
 * for tests or for production code.
 * Typically, that's an indicator for bad design as code that is *only* executed for production
 * should rather not be executed at all. But for instance code that submits
 * jvm shutdown hooks via {@link Runtime#addShutdownHook(Thread)} interferes with the starting and
 * stopping procedure for tests of the JUnit framework.
 * In more detail: when JUnit ends a test run, the shutdown thread is invoked by the platform sort
 * of out of sight of the just finished test. The the shutdown hook thread is likely to run when the
 * next test is started by JUnit, which breaks the test encapsulation.
 *
 * @author bknerr
 * @since 07.07.2011
 */
public enum DesyRunContext {
    UNIT_TEST(true),
    CI_TEST(true),
    HEADLESS(false),
    CSS(false);

    public static final String SYS_PROP_CONTEXT_KEY = "context";

    private boolean _isTest;

    /**
     * Constructor.
     */
    private DesyRunContext(final boolean isTest) {
        _isTest = isTest;
    }

    private boolean isTest() {
        return _isTest;
    }

    public static boolean isTestContext() {
        final DesyRunContext context = getContext();
        return context != null ? context.isTest() : false;
    }

    public static boolean isProductionContext() {
        return !isTestContext();
    }

    @CheckForNull
    public static DesyRunContext getContext() {
        final String contextStr = System.getProperty(SYS_PROP_CONTEXT_KEY);
        try {
            return valueOf(contextStr);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    public static void setContext(@Nonnull final DesyRunContext context) {
        System.setProperty(SYS_PROP_CONTEXT_KEY, context.name());
    }
}
