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

public class AllTestsSuite {

	//Comma separated list, if list is empty, every root will be chosen
	//private static final String bundleRoot = "org.csstudio";
	private static final String BUNDLE_ROOT = "org.,de.desy.";


	//Comma separated list, must contain one entry!
	//private static final String packageRoot = "org.csstudio";
	private static final String PACKAGE_ROOT = "org.csstudio,org.epics,org.remotercp,de.desy";


	//Only tests in fragments will be used
	// TODO (bknerr) : check whether this switch is necessary and if so how to hoist it up to configuration
	private static final boolean ONLY_FRAGMENTS = false;

	private static final String TEST_CLASS_FILTER = "*AllTests";
	//private static final String testClassFilter = "*Test";
	private static final String TEST_SUITE_NAME = "CSSTestsSuite";


	public static Test suite() {
		final TestSuite suite = new TestSuite(TEST_SUITE_NAME);
		for (final Bundle bundle : Activator.getBundles()) {
		    //System.out.print(bundle.getSymbolicName());
			// check fragment & bundleRoot conditions
			if (checkFragment(bundle) && checkBundleRoot(bundle) ) {
			    //System.out.println("\t\t\t is in!");
				final List<Class<?>> testClasses = getTestClassesInBundle(bundle);
				for (final Class<?> clazz : testClasses) {
					suite.addTest(new JUnit4TestAdapter(clazz));
				}
			}
		}
		return suite;
	}

	private static boolean checkFragment(final Bundle bundle) {
		boolean res = true;
		if (ONLY_FRAGMENTS) {
            res = isFragment(bundle);
        }
		return res;
	}

	private static boolean checkBundleRoot(final Bundle bundle) {
		boolean res = false;
		if (BUNDLE_ROOT.contains(",")) {
			final String[] bundleRoots = BUNDLE_ROOT.split(",");
			for(final String root:bundleRoots) {
				if (bundle.getSymbolicName().startsWith(root)) {
					res = true;
					break;
				}
			}
		} else {
			if (BUNDLE_ROOT.equals("")) {
				res = true;
			} else {
				res = bundle.getSymbolicName().startsWith(BUNDLE_ROOT);
			}
		}
		return res;
	}


	private static List<Class<?>> getTestClassesInBundle(final Bundle bundle) {
		final List<Class<?>> testClassesInBundle = new ArrayList<Class<?>>();
		final Enumeration<?> testClassNames = bundle.findEntries("/", TEST_CLASS_FILTER + ".class", true);

		if (testClassNames != null) {
			while (testClassNames.hasMoreElements()) {

				String testClassPath = ((URL) testClassNames.nextElement()).getPath();
				testClassPath = testClassPath.replace('/', '.');

				final int packageRootStart = getPackageRoot(testClassPath);

				/* if class does not begin with package root, just ignore it */
				if (packageRootStart == -1) {
					continue;
				}

				String testClassName = testClassPath
						.substring(packageRootStart); // de.desy.language.snl.AllTests.class
				testClassName = testClassName.substring(0, testClassName.length() - ".class".length());

				if(bundle.getSymbolicName().equals("org.csstudio.nams.service.logging")) {
					System.out.println(testClassName);
				}
				/* Attempt to load the class using the bundle classloader. */
				Class<?> testClass = null;
				if (!isFragment(bundle)) {
					try {
						testClass = bundle.loadClass(testClassName);
						System.out.println("TestClassName: " + testClassName);
					} catch (final Exception e) {
						throw new RuntimeException("Could not load class: " + testClassName, e);
					}
				} else {
					final Bundle hostbundle = getHostBundle(bundle);
					try {
					    System.out.println("TestClassName: " + testClassName);
						testClass = hostbundle.loadClass(testClassName);
					} catch (final Exception e) {
						throw new RuntimeException("Could not load class: " + testClassName, e);
					}
				}

				/*
				 * If the class is not null and not abstract, add it to list
				 */
				if(testClass != null) {
					if (!Modifier.isAbstract(testClass.getModifiers())) {
						testClassesInBundle.add(testClass); // add de.desy.language.snl.AllTests
					}
				}
			}
		}
		return testClassesInBundle;
	}


	private static int getPackageRoot(final String testClassPath) {
		int res = -1;
		if (PACKAGE_ROOT.contains(",")) {
			final String[] packageRoots = PACKAGE_ROOT.split(",");
			for(final String root : packageRoots) {
				res = testClassPath.indexOf(root);
				if (res > -1) {
                    break;
                }
			}
		} else {
			res = testClassPath.indexOf(PACKAGE_ROOT);
		}
		return res;
	}


	private static boolean isFragment(final Bundle bundle) {
		final Enumeration<String> headerKeys = bundle.getHeaders().keys();
		while (headerKeys.hasMoreElements()) {
			if (headerKeys.nextElement().toString().equals("Fragment-Host")) {
				return true;
			}
		}
		return false;
	}

	private static Bundle getHostBundle(final Bundle bundle) {
		String fragmenthost = new String();
		final Enumeration keys = bundle.getHeaders().keys();
		final Enumeration e = bundle.getHeaders().elements();
		while (keys.hasMoreElements()&&e.hasMoreElements()) {
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
}
