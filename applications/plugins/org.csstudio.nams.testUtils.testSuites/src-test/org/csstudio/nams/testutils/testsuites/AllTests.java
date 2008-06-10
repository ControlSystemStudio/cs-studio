package org.csstudio.nams.testutils.testsuites;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

	private static final String TEST_CLASS_SUFFIX = "_Test.class";

	public static Test suite() {
		System.out.println("AllTests.suite()");
		TestSuite suite = new TestSuite(
				"Test for org.csstudio.nams.testutils.testsuites");
		// $JUnit-BEGIN$
		suite.addTestSuite(AllTests.class);
		collectTestCasesOfPackageAndSubPackages(suite,
				"org.csstudio.nams.testutils.testsuites");
		collectTestCasesOfPackageAndSubPackages(suite, "de.c1wps.desy.ams");
		collectTestCasesOfPackageAndSubPackages(suite, "org.csstudio.nams");
		// $JUnit-END$
		System.out.println("AllTests.suite() - found: "
				+ suite.countTestCases() + " test cases!");
		return suite;
	}

	@SuppressWarnings("unchecked")
	private static void collectTestCasesOfPackageAndSubPackages(
			TestSuite suite, String packageName) {
		try {
			Class<?>[] classes = getClasses(packageName);
			// System.out.println("AllTests.suite(): found: " + classes.length);
			for (Class<?> class1 : classes) {
				// System.out.println(class1.getName());
				if (TestCase.class.isAssignableFrom(class1)) {
					Class<? extends TestCase> test = (Class<? extends TestCase>) class1;
					System.out.println("AllTests.suite() - test case found: "
							+ test.getName());
					suite.addTestSuite(test);
				}
			}
		} catch (ClassNotFoundException e) {
			Assert.fail("" + e.getMessage());
		} catch (IOException e) {
			Assert.fail("" + e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private static String decodeURLPath(String encoded) {
		return URLDecoder.decode(encoded);
	}

	/**
	 * Inspired by:
	 * http://www.codeclippers.com/clippings/view/854/Get_all_classes_within_a_package
	 * 
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		// System.out.println("AllTests.findClasses() dir/pckname: "
		// + directory.toString() + " / " + packageName);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			// System.out.println("AllTests.findClasses() - dir not exists");
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			// System.out
			// .println("AllTests.findClasses() file: " + file.getName());
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "."
						+ file.getName()));
			} else if (file.getName().endsWith(TEST_CLASS_SUFFIX)) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * Inspired by:
	 * http://www.codeclippers.com/clippings/view/854/Get_all_classes_within_a_package
	 * 
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class<?>[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		// System.out.println("AllTests.getClasses() path: " + path);
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String decoded = decodeURLPath(resource.getFile());
			dirs.add(new File(decoded));
			// System.out.println("AllTests.getClasses() found: " + resource);
		}
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	@org.junit.Test
	public void testAssertionsAktiviert() {
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			Assert.fail("Nein, Assertions sind nicht aktiviert");
		} catch (AssertionError ae) {
			Assert.assertEquals("Ok, Assertions sind aktiviert!", ae
					.getMessage());
		}
	}
}
