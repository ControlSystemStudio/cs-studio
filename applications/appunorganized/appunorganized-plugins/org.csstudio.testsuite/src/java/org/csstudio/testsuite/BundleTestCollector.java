/*******************************************************************************
 * Copyright (c) 2008 Syntax Consulting, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.testsuite;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * This class allows you harvest tests from resolved bundles based on
 * filters you supply. It can harvest tests from both bundles and
 * can also be used during automated builds using the Eclipse Testing Framework.
 * <p>
 * This class is similar to the JUnit TestCollector class, except that it takes
 * responsibility for both loading the classes and adding them to the test
 * suite. The collector must load the classes using the appropriate bundle
 * classloader for each test, so this work cannot be done in the suite.
 * <p>
 * To use this collector, simply create a JUnit test suite with a method like
 * this:
 * <p>
 *
 * <pre>
 * public static Test suite() {
 *
 *  TestSuite suite = new TestSuite(&quot;All Tests&quot;);
 *
 *  BundleTestCollector testCollector = new BundleTestCollector();
 *  List&lt;Test&gt; tests = testCollector.collectTests('list of bundle roots to be cut from the bundle names',
 *                                                      'list of bundle names of interest');
 *
 *  return suite;
 *
 * }
 * </pre>
 * <p>
 * Note (x1) that because JUnit 4 implements suites through annotations, a similar
 * mechanism cannot be used. If anyone has ideas concerning how this could be
 * made to work using JUnit 4, please let me know. Comments and suggestions can
 * be sent to patrick@rcpquickstart.com.
 *
 * (x1) Note that with the JUnit4TestAdapter this problem should be solved
 *
 * @author Patrick Paulin
 * @author bknerr (bastian.knerr@desy.de)
 *
 */
public class BundleTestCollector {

    private static final Logger LOG = LoggerFactory.getLogger(BundleTestCollector.class);

    /**
     * Create a list of test classes for the bundles currently resolved by the
     * framework. This method works with JUnit 3.x and JUnit 4.x test cases by using
     * the {@link JUnit4TestAdapter}.
     *
     * @param bundleNames
     *      List of bundle prefixes that shall be considered for test collecting
     * @param packageBlackList
     *      package prefix list that are explicitly excluded from the selected bundles
     * @param testClassFilters
     *      List of filter strings that will be used to search for test cases. The filter
     *      applies to the unqualified class name only (not including the
     *      package name). Wildcards are allowed, as defined by the {@link
     *      TestSuiteActivator Bundle#findEntries(String, String, boolean)} method.
     * @return list of test classes that match the bundle prefixes and filters passed in
     */
    public List<Test> collectTests(final Iterable<String> bundleNames,
                                   final Iterable<String> bundlesBlackList,
                                   final Iterable<String> packageBlackList,
                                   final Iterable<String> testClassFilters,
                                   final String commonSuffix) {

        final List<Test> tests = new ArrayList<Test>();

        final Bundle[] bundles = TestSuiteActivator.getInstance().getBundles();
        for (final Bundle bundle : bundles) {

            if (!isFragment(bundle) && isBundleValid(bundle, bundleNames, bundlesBlackList)) {
                final List<Class<?>> testClasses =
                    getTestClassesInBundle(bundle,
                                           bundleNames,
                                           packageBlackList,
                                           testClassFilters,
                                           commonSuffix);

                for (final Class<?> clazz : testClasses) {
                    tests.add(new JUnit4TestAdapter(clazz));
                }
            }
        }
        return tests;
    }

    private List<Class<?>> getTestClassesInBundle(final Bundle bundle,
                                                  final Iterable<String> bundleNames,
                                                  final Iterable<String> packageBlackList,
                                                  final Iterable<String> testClassFilters,
                                                  final String commonFilterSuffix) {

        final Enumeration<?> allClassNames = bundle.findEntries("/", "*.class", true); //$NON-NLS-1$

        if (allClassNames != null) {

            final List<Class<?>> testClassesInBundle = new ArrayList<Class<?>>();
            while (allClassNames.hasMoreElements()) {
                final String classPath = findPathAndConvertToClassName((URL) allClassNames.nextElement());

                final int packageRootStartIndex = getPackageRoot(classPath, bundleNames);

                /* if class does not begin with package root, just ignore it */
                if (packageRootStartIndex == -1) {
                    continue;
                }

                String className = classPath.substring(packageRootStartIndex);
                className = className.substring(0, className.length() - ".class".length()); //$NON-NLS-1$

                final Class<?> testClass = loadClass(bundle, className);
                if (testClass != null) {
                    if (checkForValidTest(testClassFilters,
                                          commonFilterSuffix,
                                          packageBlackList,
                                          testClass)) {
                        testClassesInBundle.add(testClass);
                    }
                }
            }
            return testClassesInBundle;
        }
        return Collections.emptyList();
    }


    private boolean checkForValidTest(final Iterable<String> testClassFilters,
                                      final String commonFilterSuffix,
                                      final Iterable<String> blackList,
                                      final Class<?> testClass) {
        final String className = testClass.getName();

        if (isBlacklistedOrAbstract(blackList, testClass, className)) {
            return false;
        }

        if (isItATestClass(testClass)) {
            if (!className.endsWith(commonFilterSuffix)) {
                LOG.warn("Class {} is a test, but does not end on *{}.java.\n Please rename to one out of for this launch config: {}",
                         new Object[] {className, commonFilterSuffix, Joiner.on(", ").join(testClassFilters)});
            } else {
                for (final String filter : testClassFilters) { // check for filters
                    if (className.endsWith(filter)) {
                        return true; // Valid test found
                    }
                }
            }
        } else {
            if (className.endsWith(commonFilterSuffix)) {
                LOG.warn("Class {} is NOT a test!\nPlease rename to a different suffix. (Perhaps *Demo?).", className);
            }
        }
        return false;
    }

    private boolean isBlacklistedOrAbstract(final Iterable<String> blackList,
                                            final Class<?> testClass,
                                            final String className) {
        if (isClassBlackListed(className, blackList)) {
            return true;
        }
        if (Modifier.isAbstract(testClass.getModifiers())) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the className beginning matches any of the blacklisted packages
     * @param className
     * @param packageBlackList
     * @return
     */
    private boolean isClassBlackListed(final String className,
                                       final Iterable<String> packageBlackList) {

        for (final String badPackage : packageBlackList) {
            if (className.startsWith(badPackage)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks class for being either inherited from Testcase or that it contains @Test annotations.
     * @param testClass
     * @return
     */
    private boolean isItATestClass(final Class<?> testClass) {

        // Check for junit <4.0 test
        Class<?> parent = testClass.getSuperclass();
        while (!parent.getName().equals(Object.class.getName())) {

            if (parent.getName().endsWith(TestCase.class.getName())) {
                return true;
            }
            parent = parent.getSuperclass();
        }
        // Nope, now check for public methods that have @Test annotations, junit > 4.0
        for (final Method method : testClass.getMethods()) {
            if (method.getAnnotation(org.junit.Test.class) != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Take relative path produced by findEntries method and convert
     * it into a properly formatted class name. The package root is
     * used to determine the start of the qualified class name in
     * the path.
     * @param testClassNames
     * @return
     */
    private String findPathAndConvertToClassName(final URL testClassName) {
         String testClassPath = testClassName.getPath();
         testClassPath = testClassPath.replace('/', '.');
        return testClassPath;
    }

    /**
     * @param bundle
     * @param testClassesInBundle
     * @param testClassName
     * @return
     */
    private Class<?> loadClass(final Bundle bundle,
                               final String testClassName) {
        /* Attempt to load the class using the bundle classloader. */
         Class<?> testClass = null;
         try {

             if (!isFragment(bundle)) {
                 testClass = bundle.loadClass(testClassName);
             } else {
                 final Bundle hostbundle = getHostBundle(bundle);
                 if (hostbundle != null) {
                     testClass = hostbundle.loadClass(testClassName);
                 }
             }
             /*
              * If the class is not abstract, add it to list
              */
             if (testClass != null && !Modifier.isAbstract(testClass.getModifiers())) {
                 return testClass;
             }
         } catch (final ClassNotFoundException e) {
//                    throw new RuntimeException("Could not load class: " //$NON-NLS-1$
//                            + testClassName, e);
             // TODO (bknerr) : what's wrong with the class loader
             // TODO (bknerr) : where to log the test result messages, when the test even can't be loaded?

             LOG.error("\nClass loading of " + testClassName + " failed. Ignore test!\n");
             System.out.println("\nClass loading of " + testClassName + " failed. Ignore test!\n");
             e.printStackTrace();
         }
        return null;
    }


    private static boolean isFragment(final Bundle bundle) {
        final Enumeration<?> headerKeys = bundle.getHeaders().keys();
        while (headerKeys.hasMoreElements()) {
            if ("Fragment-Host".equals(headerKeys.nextElement().toString())) { //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }

    private static Bundle getHostBundle(final Bundle bundle) {
        String fragmenthost = "";
        final Enumeration<?> keys = bundle.getHeaders().keys();
        final Enumeration<?> e = bundle.getHeaders().elements();
        while (keys.hasMoreElements() && e.hasMoreElements()) {
            if ("Fragment-Host".equals(keys.nextElement().toString())) {
                fragmenthost = e.nextElement().toString();
            } else {
                e.nextElement();
            }
        }

        // if host plugin version (range) is added in manifest
        if(fragmenthost.indexOf(";") > 0) {
            fragmenthost = fragmenthost.substring(0,fragmenthost.indexOf(";"));
        }
        final Bundle[] bundles = TestSuiteActivator.getInstance().getBundles();
        for (final Bundle b : bundles) {
            if(b.getSymbolicName().equals(fragmenthost)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Checks wether the currently analysed bundle features the 'selected bundle prefix'.
     * @param bundle
     * @param bundles
     * @param blackList
     * @return true if the bundle name matches any of the bundles' prefixes
     */
    private static boolean isBundleValid(final Bundle bundle,
                                         final Iterable<String> bundles,
                                         final Iterable<String> blackList) {
        final String symbolicName = bundle.getSymbolicName();

        for (final String bundlePrefix : bundles) {
            if (symbolicName.startsWith(bundlePrefix)) {
                for (final String black : blackList) { // blacklist
                    if (symbolicName.startsWith(black)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the testClass (with full bundle and package class name) belongs to any of the
     * packages in packageRoots and, if so, returns the char index of where the package root begins,
     * and -1 otherwise
     *
     * @param testClassPath
     * @param bundleRoots
     * @return the index in the testClassPathName where package root begins, -1 if it is not contained
     */
    private static int getPackageRoot(final String testClassPath,
                                      final Iterable<String> bundleRoots) {

        for(final String root : bundleRoots) {
            final int res = testClassPath.indexOf(root);
            if (res > -1) { // found
                return res; // return root index
            }
        }
        return -1;
    }
}
