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
package org.csstudio.domain.common.junit;

import java.lang.reflect.Constructor;

import javax.annotation.Nonnull;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Junit4 Class Runner that can evaluate a RunIf annotation.
 *
 * @author bknerr
 * @since 30.05.2011
 */
public class ConditionalClassRunner extends BlockJUnit4ClassRunner {

    private final boolean _executeTestsForClass;

    /**
     * Constructor.
     */
    public ConditionalClassRunner(@Nonnull final Class<?> clazz) throws InitializationError {
        super(clazz);
        _executeTestsForClass = conditionTrueForClass(clazz);
    }

    @Override
    protected void runChild(@Nonnull final FrameworkMethod method,
                            @Nonnull final RunNotifier notifier) {
           if (_executeTestsForClass && conditionTrueForMethod(method)) {
               super.runChild(method, notifier);
           } else {
               final Description desc =
                   Description.createTestDescription(this.getTestClass().getJavaClass(),
                                                     method.getName());
               notifier.fireTestIgnored(desc);
           }
    }

    public boolean conditionTrueForMethod(@Nonnull final FrameworkMethod method) {
        final RunIf resource = method.getAnnotation(RunIf.class);
        if (resource == null) {
            return true;
        }
        final Class<? extends IRunCondition> test = resource.conditionClass();
        try {
            final IRunCondition checker = createConditionReflectively(resource, test);
            return checker.shallBeRun();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean conditionTrueForClass(@Nonnull final Class<?> testClass) {
        final RunIf resource = testClass.getAnnotation(RunIf.class);
        if (resource == null) {
            return true;
        }
        final Class<? extends IRunCondition> test = resource.conditionClass();
        try {
            final IRunCondition checker = createConditionReflectively(resource, test);
            return checker.shallBeRun();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private IRunCondition createConditionReflectively(@Nonnull final RunIf resource,
                                                     @Nonnull final Class<? extends IRunCondition> condition) throws Exception {
        final String[] arguments = resource.arguments();
        IRunCondition checker;
        if (arguments == null || arguments.length == 0) {
            checker = condition.newInstance();
        } else {
            if (arguments.length == 1) {
                final Constructor<? extends IRunCondition> constructor = condition.getConstructor(String.class);
                checker = constructor.newInstance(arguments[0]);
            } else {
                final Constructor<? extends IRunCondition> constructor = condition.getConstructor(String[].class);
                checker = constructor.newInstance(new Object[]{arguments});
            }
        }
        return checker;
    }
}
