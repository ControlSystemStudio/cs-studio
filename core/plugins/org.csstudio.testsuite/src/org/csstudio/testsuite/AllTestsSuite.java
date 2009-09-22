package org.csstudio.testsuite;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestsSuite {
	
	private static final String bundleRoot = "org.csstudio";
	private static final String packageRoot = "org.csstudio";
	private static final String testClassFilter = "*Test";
	private static final String testsuitename = "CSSTestsSuite";
	
	public static Test suite() {
		TestSuite suite = new TestSuite(testsuitename);
		for (Bundle bundle : Activator.getBundles()) {
			if (isFragment(bundle)
					&& bundle.getSymbolicName().startsWith(bundleRoot)) {
				List<Class> testClasses = getTestClasesInBundle(bundle);

				for (Class clazz : testClasses) {
					suite.addTest(new JUnit4TestAdapter(clazz));				
				}
			}
		}
		return suite;
	}

	private static List<Class> getTestClasesInBundle(Bundle bundle) {
		List<Class> testClassesInBundle = new ArrayList<Class>();

		Enumeration testClassNames = bundle.findEntries("/", testClassFilter + ".class", true); 
		if (testClassNames != null) {
			while (testClassNames.hasMoreElements()) {

				/*
				 * Take relative path produced by findEntries method and convert
				 * it into a properly formatted class name. The package root is
				 * used to determine the start of the qualified class name in
				 * the path.
				 */
				String testClassPath = ((URL) testClassNames.nextElement())
						.getPath();
				testClassPath = testClassPath.replace('/', '.');

				int packageRootStart = testClassPath.indexOf(packageRoot);

				/* if class does not begin with package root, just ignore it */
				if (packageRootStart == -1) {
					continue;
				}

				String testClassName = testClassPath
						.substring(packageRootStart);
				testClassName = testClassName.substring(0, testClassName
						.length()
						- ".class".length());

				/* Attempt to load the class using the bundle classloader. */
				Class testClass = null;
				if (!isFragment(bundle)) {
					try {
						testClass = bundle.loadClass(testClassName);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("Could not load class: " 
								+ testClassName, e);
					}
				} else {
					Bundle hostbundle = getHostBundle(bundle);
					try {
						testClass = hostbundle.loadClass(testClassName);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("Could not load class: " 
								+ testClassName, e);
					}
				}

				/*
				 * If the class is not abstract, add it to list
				 */
				if (!Modifier.isAbstract(testClass.getModifiers())) {
//					System.out.println("Add class " + testClassPath);
					testClassesInBundle.add(testClass);
				}
			}
		}
		return testClassesInBundle;
	}
	
	private static boolean isFragment(Bundle bundle) {
		Enumeration headerKeys = bundle.getHeaders().keys();
		while (headerKeys.hasMoreElements()) {
			if (headerKeys.nextElement().toString().equals("Fragment-Host")) {
				return true;
			}
		}
		return false;
	}
	
	private static Bundle getHostBundle(Bundle bundle) {
		String fragmenthost = new String();
		Enumeration keys = bundle.getHeaders().keys();
		Enumeration e = bundle.getHeaders().elements();
		while (keys.hasMoreElements()&&e.hasMoreElements()) {
			if (keys.nextElement().toString().equals("Fragment-Host")) { 
				fragmenthost = e.nextElement().toString();
			} else {
				e.nextElement();
			}
		}
		
		// if host plugin version (range) is added in manifest
		if(fragmenthost.indexOf(";")>0) {
			fragmenthost = fragmenthost.substring(0,fragmenthost.indexOf(";"));
		}
		for (Bundle b : Activator.getBundles()) {
			if(b.getSymbolicName().equals(fragmenthost)) 
				return b;
		}
		return null;
	}
}
