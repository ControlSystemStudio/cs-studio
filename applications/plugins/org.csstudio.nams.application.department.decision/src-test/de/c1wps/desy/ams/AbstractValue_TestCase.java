/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */
package de.c1wps.desy.ams;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * An abstract test case or proof the contract of {@link Object}s methods
 * corresponding to objects representing a value type (like String).
 * 
 * @param <T>
 *            The type of class under test.
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 2008-03-30
 */
public abstract class AbstractValue_TestCase<T> extends TestCase {
	private static final String NAME_OF_VALUEOF_METHOD = "valueOf";

	@Test
	public final void testAllFieldsAreFinal() throws Throwable {
		Class<?> type = getAValueOfTypeUnderTest().getClass();

		boolean atLeastOneNotFinalFieldFouund = false;
		for (Field field : type.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			boolean fieldIsPrivate = Modifier.isPrivate(modifiers);
			boolean fieldIsFinal = Modifier.isFinal(modifiers);
			boolean fieldIsStatic = Modifier.isStatic(modifiers);

			boolean isOk = fieldIsPrivate && (fieldIsFinal || fieldIsStatic);

			if (!isOk) {
				atLeastOneNotFinalFieldFouund = true;
				break;
			}
		}

		assertFalse("No not final field is present",
				atLeastOneNotFinalFieldFouund);
	}

	@Test
	public final void testClassIsFinalAndDirectSubClassOfObject() throws Throwable {
		Class<?> type = getAValueOfTypeUnderTest().getClass();
		int modifiers = type.getModifiers();

		assertTrue("Class is final", Modifier.isFinal(modifiers));

		assertEquals("Super-type is Object", Object.class, type.getSuperclass());
	}

	@Test
	public final void testConstructorsArePrivate() throws Throwable {
		Class<?> type = getAValueOfTypeUnderTest().getClass();

		boolean atLeastOneNotPrivateConstructorFouund = false;
		for (Constructor<?> constructor : type.getDeclaredConstructors()) {
			int modifiers = constructor.getModifiers();

			if (!Modifier.isPrivate(modifiers)) {
				atLeastOneNotPrivateConstructorFouund = true;
				break;
			}
		}

		assertFalse("No not private construcotr is present",
				atLeastOneNotPrivateConstructorFouund);
	}

	@Test
	public final void testValueOfIsPresent() throws Throwable {
		Class<?> type = getAValueOfTypeUnderTest().getClass();

		boolean atLeastOneValueOfFound = false;
		for (Method method : type.getDeclaredMethods()) {
			int modifiers = method.getModifiers();
			if (Modifier.isStatic(modifiers)
					&& (!Modifier.isPrivate(modifiers))
					&& method.getReturnType().equals(type)
					&& method.getName().equals(NAME_OF_VALUEOF_METHOD)) {
				atLeastOneValueOfFound = true;
				break;
			}
		}

		assertTrue("At least one value of method is present",
				atLeastOneValueOfFound);
	}

	final protected T getAValueOfTypeUnderTest() throws Throwable {
		T valueOfTypeUnderTest = doGetAValueOfTypeUnderTest();
		assertNotNull(valueOfTypeUnderTest);
		return valueOfTypeUnderTest;
	}
	abstract protected T doGetAValueOfTypeUnderTest() throws Throwable ;

	final protected T[] getDifferentInstancesOfTypeUnderTest() throws Throwable {
		T[] differentInstances = doGetDifferentInstancesOfTypeUnderTest();

		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#doGetTwoOrThreeDiffrentValuesOfTypeUnderTest() does not deliver null",
				differentInstances);

		for (T instance : differentInstances) {
			assertNotNull(
					"Implementations of AbstractObject_TestCase<T>#doGetTwoOrThreeDiffrentValuesOfTypeUnderTest() does not deliver a null element",
					instance);
			for (T instance2 : differentInstances) {
				if (instance != instance2)
					assertNotSame(instance, instance2);
			}
		}
		return differentInstances;
	}

	/**
	 * 
	 * @return T[] with different Instances, each element differs in at least one different instance variable.
	 */
	abstract protected T[] doGetDifferentInstancesOfTypeUnderTest() throws Throwable ;

	@Test
	public final void testEqualsWithIdenticalInstances() throws Throwable {
		T x = getAValueOfTypeUnderTest();
		T y = x;
		T z = x;

		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				x);
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				y);
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				z);

		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "It is reflexive: for any non-null reference value x, x.equals(x) should return true",
				x.equals(x));
		// Note: x is non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
				x.equals(y) == true ? y.equals(x) == true : true
		// Note: x, y are non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		);

		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true",
				x.equals(y) == true & y.equals(z) == true ? x.equals(z) == true
						: true
		// Note: x, y, z are non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		);
		assertTrue(
				"Copied from Java API documentation version JDK 1.5:"
						+ " It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified",
				x.equals(y) == x.equals(y)
		// Note: x, y are non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		);
		assertTrue(
				"Copied from Java API documentation version JDK 1.5:"
						+ " For any non-null reference value x, x.equals(null) should return false",
				x.equals(null) == false
		// Note: x, y are non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		);
	}

	@Test
	public final void testEqualsWithDiffrentInstances() throws Throwable {
		T[] differentInstances = getDifferentInstancesOfTypeUnderTest();

		T x = differentInstances[0];
		T y = differentInstances[1];

		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "It is reflexive: for any non-null reference value x, x.equals(x) should return true",
				x.equals(x));
		// Note: x is non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()

		for (T instance : differentInstances) {
			assertTrue(
					"Copied from Java API documentation version JDK 1.5: "
							+ "It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
					x.equals(instance) == true ? instance.equals(x) : true
			// Note: x, y are non null cause of post-condition of
			// #getNewInstanceOfClassUnderTest()
			);
			assertTrue(
					"Copied from Java API documentation version JDK 1.5: "
							+ "It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true",
					x.equals(y) == true && y.equals(instance) == true ? x
							.equals(instance) == true : true

			// Note: x, y, z are non null cause of post-condition of
			// #getNewInstanceOfClassUnderTest()
			);
			assertTrue(
					"Copied from Java API documentation version JDK 1.5:"
							+ " It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified",
					x.equals(instance) == x.equals(instance)
			// Note: x, y are non null cause of post-condition of
			// #getNewInstanceOfClassUnderTest()
			);

			assertTrue(
					"Copied from Java API documentation version JDK 1.5:"
							+ " For any non-null reference value x, x.equals(null) should return false",
					instance.equals(null) == false
			// Note: x, y are non null cause of post-condition of
			// #getNewInstanceOfClassUnderTest()
			);

		}
	}

	@Test
	public final void testEqualsWithIncompareableOtherObjectInstance() throws Throwable {
		T x = getAValueOfTypeUnderTest();
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				x);

		Object incomparableObject = new Object(); // A Object is never a value
		// and by this always
		// incomparable.
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() does not deliver null",
				incomparableObject);

		assertFalse(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() does not deliver a instance of a type T which is assignable from the type of class under test C: C.isAssignableFrom(T) == false",
				x.getClass().isAssignableFrom(incomparableObject.getClass()));

		assertFalse(
				"Equals returns false, if the type of the object to be compared to is incomparable to the type of the class under test",
				x.equals(incomparableObject));
	}

	@Test
	public final void testToString() throws Throwable {
		T x = getAValueOfTypeUnderTest();
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				x);

		assertNotNull("toString() does not deliver null", x.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void testClone() throws Throwable {
		T x = getAValueOfTypeUnderTest();
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				x);

		if (x instanceof Cloneable) {

			T xClone = null;
			// Try to clone:
			try {
				Method cloneMethod = null;

				for (Method m : x.getClass().getMethods()) {
					if (m.getName().equals("clone")) {
						cloneMethod = m;
						break;
					}
				}
				assertNotNull(
						"A cloneable class offers a public and accesible version of clone!",
						cloneMethod);

				xClone = (T) x.getClass().cast(
						cloneMethod.invoke(x, (Object[]) null));
			} catch (SecurityException e) {
				fail("A cloneable class offers a public and accesible version of clone!");
			} catch (IllegalArgumentException e) {
				fail("A cloneable class offers a paramless version of clone!");
			} catch (IllegalAccessException e) {
				fail("A cloneable class offers a public and accesible version of clone!");
			} catch (InvocationTargetException e) {
				fail("A cloneable class offers a public and accesible version of clone!");
			}

			// Check clone (if reached here clone succeded)
			assertNotSame(x, xClone);
			assertSame(x.getClass(), xClone.getClass());
			assertEquals(x, xClone);
		} else {
			// Try to clone have to fail if Cloneable is not implemented:
			try {
				Method cloneMethod;
				cloneMethod = x.getClass().getMethod("clone", (Class<?>) null);
				cloneMethod.invoke(x, (Object[]) null);
				fail("clone have to fail if java.lang.Cloneable is not implemented");
				dummyToSimulaizePossibilityToThrowCloneNotSupportedException();
			} catch (SecurityException e) {
				// OK!
			} catch (NoSuchMethodException nsme) {
				// OK!
			} catch (IllegalArgumentException iae) {
				// OK!
			} catch (IllegalAccessException iace) {
				// OK!
			} catch (InvocationTargetException ite) {
				// OK!
			} catch (CloneNotSupportedException cle) {
				// OK!
			}
		}
	}

	private void dummyToSimulaizePossibilityToThrowCloneNotSupportedException()
			throws CloneNotSupportedException {
		if (1 != 1)
			throw new CloneNotSupportedException();
	}

	@Test
	public final void testHashCode() throws Throwable {
		T x = getAValueOfTypeUnderTest();
		T y = getAValueOfTypeUnderTest();

		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				x);
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				y);

		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application",
				x.hashCode() == x.hashCode());
		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result",
				x.equals(y) ? x.hashCode() == y.hashCode() : true);
		// Note (Copied from Java API documentation version JDK 1.5): It is not
		// required that if two objects are unequal according to the
		// equals(java.lang.Object) method, then calling the hashCode method on
		// each of the two objects must produce distinct integer results.
		// However, the programmer should be aware that producing distinct
		// integer results for unequal objects may improve the performance of
		// hashtables.
	}
}
