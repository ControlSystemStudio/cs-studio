/*******************************************************************************
 * Copyright (c) 2008 Syntax Consulting, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.testsuite;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.osgi.framework.Bundle;


/**
 * This class allows you harvest unit tests from resolved bundles based on
 * filters you supply. It can harvest tests from both bundles and fragments, and
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
 *  List&lt;Test&gt; tests = testCollector.collectTests(&quot;com.rcpquickstart.&quot;,
 *          &quot;com.rcpquickstart.mypackage.&quot;, &quot;*Test&quot;);
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

    /**
     * Create a list of test classes for the bundles currently resolved by the
     * framework. This method works with JUnit 3.x test cases only, meaning that
     * it searches for classes that subclass the TestCase class.
     *
     * @param suite
     *      to which tests should be added
     * @param bundleRoot
     *      root string that a bundle id needs to start with in order for the
     *      bundle to be included in the search
     * @param packageRoot
     *      root string that a package needs to start with in order for the
     *      package to be included in the search
     * @param testClassFilter
     *      filter string that will be used to search for test cases. The filter
     *      applies to the unqualified class name only (not including the
     *      package name). Wildcards are allowed, as defined by the {@link
     *      Activator Bundle#findEntries(String, String, boolean)} method.
     * @return list of test classes that match the roots and filter passed in
     */
    public List<Test> collectTests(final TestSuite suite,
                                   final String bundleRoot,
                                   final String packageRoot,
                                   final String testClassFilter) {

        final List<Test> tests = new ArrayList<Test>();

        for (final Bundle bundle : Activator.getBundles()) {

            if (!isFragment(bundle) && checkBundleRoot(bundle, bundleRoot)) {
                final List<Class<?>> testClasses =
                    getTestClasesInBundle(bundle, packageRoot, testClassFilter);

                for (final Class<?> clazz : testClasses) {
                    tests.add(new JUnit4TestAdapter(clazz));
                }
            }
        }
        return tests;
    }

    private List<Class<?>> getTestClasesInBundle(final Bundle bundle,
                                                 final String packageRoot,
                                                 final String testClassFilter) {


        final List<Class<?>> testClassesInBundle = new ArrayList<Class<?>>();
        final Enumeration<?> testClassNames =
            bundle.findEntries("/", testClassFilter + ".class", true); //$NON-NLS-1$

        if (testClassNames != null) {
            while (testClassNames.hasMoreElements()) {

                /*
                 * Take relative path produced by findEntries method and convert
                 * it into a properly formatted class name. The package root is
                 * used to determine the start of the qualified class name in
                 * the path.
                 */
                String testClassPath = ((URL) testClassNames.nextElement()).getPath();
                testClassPath = testClassPath.replace('/', '.');

                final int packageRootStart = getPackageRoot(testClassPath, packageRoot);

                /* if class does not begin with package root, just ignore it */
                if (packageRootStart == -1) {
                    continue;
                }

                String testClassName = testClassPath.substring(packageRootStart);
                testClassName = testClassName.substring(0, testClassName.length() - ".class".length()); //$NON-NLS-1$

                /* Attempt to load the class using the bundle classloader. */
                Class<?> testClass = null;
                try {
                    if (!isFragment(bundle)) {
                        testClass = bundle.loadClass(testClassName);
                    } else {
                        final Bundle hostbundle = getHostBundle(bundle);
                        testClass = hostbundle.loadClass(testClassName);

                    }
                    /*
                     * If the class is not abstract, add it to list
                     */
                    if (!Modifier.isAbstract(testClass.getModifiers())) {
                        testClassesInBundle.add(testClass);
                    }
                } catch (final ClassNotFoundException e) {
//                    throw new RuntimeException("Could not load class: " //$NON-NLS-1$
//                            + testClassName, e);
                    // TODO (bknerr) : what's wrong with the class loader
                    // TODO (bknerr) : where to log the test result messages, when the test even can't be loaded?
                    System.out.println("Class loading failed. Ignore test.");
                }

            }
        }
        return testClassesInBundle;
    }

    private boolean isFragment(final Bundle bundle) {
        final Enumeration<?> headerKeys = bundle.getHeaders().keys();
        while (headerKeys.hasMoreElements()) {
            if (headerKeys.nextElement().toString().equals("Fragment-Host")) { //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }

    private static Bundle getHostBundle(final Bundle bundle) {
        String fragmenthost = new String();
        final Enumeration<?> keys = bundle.getHeaders().keys();
        final Enumeration<?> e = bundle.getHeaders().elements();
        while (keys.hasMoreElements() && e.hasMoreElements()) {
            if (keys.nextElement().toString().equals("Fragment-Host")) {
                fragmenthost = e.nextElement().toString();
            } else {
                e.nextElement();
            }
        }

        // if host plugin version (range) is added in manifest
        if(fragmenthost.indexOf(";") > 0) {
            fragmenthost = fragmenthost.substring(0,fragmenthost.indexOf(";"));
        }
        for (final Bundle b : Activator.getBundles()) {
            if(b.getSymbolicName().equals(fragmenthost)) {
                return b;
            }
        }
        return null;
    }

    private static boolean checkBundleRoot(final Bundle bundle, final String bundleRoot) {
        boolean res = false;
        if (bundleRoot.contains(",")) {
            final String[] bundleRoots = bundleRoot.split(",");
            for(final String root : bundleRoots) {
                if (bundle.getSymbolicName().startsWith(root)) {
                    res = true;
                    break;
                }
            }
        } else {
            if (bundleRoot.equals("")) {
                res = true;
            } else {
                res = bundle.getSymbolicName().startsWith(bundleRoot);
            }
        }
        return res;
    }

    private static int getPackageRoot(final String testClassPath, final String packageRoot) {
        int res = -1;
        if (packageRoot.contains(",")) {
            final String[] packageRoots = packageRoot.split(",");
            for(final String root : packageRoots) {
                res = testClassPath.indexOf(root);
                if (res > -1) {
                    break;
                }
            }
        } else {
            res = testClassPath.indexOf(packageRoot);
        }
        return res;
    }
}
