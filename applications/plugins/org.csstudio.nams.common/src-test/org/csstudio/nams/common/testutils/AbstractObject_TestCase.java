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
package org.csstudio.nams.common.testutils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

/**
 * An abstract test case or proof the contract of {@link Object}s methods.
 * 
 * @param <T>
 *            The type of class under test.
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1, 2008-03-28
 */
public abstract class AbstractObject_TestCase<T> extends TestCase {
	@SuppressWarnings("unchecked")
	@Test
	public final void testClone() {
		final T x = this.getNewInstanceOfClassUnderTest();
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						x);

		if (x instanceof Cloneable) {

			T xClone = null;
			// Try to clone:
			try {
				Method cloneMethod = null;

				for (final Method m : x.getClass().getMethods()) {
					if (m.getName().equals("clone")) {
						cloneMethod = m;
						break;
					}
				}
				Assert
						.assertNotNull(
								"A cloneable class offers a public and accesible version of clone!",
								cloneMethod);

				xClone = (T) x.getClass().cast(
						cloneMethod.invoke(x, (Object[]) null));
			} catch (final SecurityException e) {
				Assert
						.fail("A cloneable class offers a public and accesible version of clone!");
			} catch (final IllegalArgumentException e) {
				Assert
						.fail("A cloneable class offers a paramless version of clone!");
			} catch (final IllegalAccessException e) {
				Assert
						.fail("A cloneable class offers a public and accesible version of clone!");
			} catch (final InvocationTargetException e) {
				Assert
						.fail("A cloneable class offers a public and accesible version of clone!");
			}

			// Check clone (if reached here clone succeded)
			Assert.assertNotSame(x, xClone);
			Assert.assertSame(x.getClass(), xClone.getClass());
			Assert.assertEquals(x, xClone);
		} else {
			// Try to clone have to fail if Cloneable is not implemented:
			try {
				Method cloneMethod = null;
				for (final Method m : x.getClass().getDeclaredMethods()) {
					if (m.getName().equals("clone")) {
						cloneMethod = m;
						break;
					}
				}
				if (cloneMethod != null) {
					cloneMethod.invoke(x, (Object[]) null);
					Assert
							.fail("clone have to fail if java.lang.Cloneable is not implemented");
					this
							.dummyToSimulaizePossibilityToThrowCloneNotSupportedException();
				}
			} catch (final SecurityException e) {
				// OK!
			} catch (final IllegalArgumentException iae) {
				// OK!
			} catch (final IllegalAccessException iace) {
				// OK!
			} catch (final InvocationTargetException ite) {
				// OK!
			} catch (final CloneNotSupportedException cle) {
				// OK!
			}
		}
	}

	@Test
	public final void testEqualsWithDiffrentInstances() {
		final T[] threeInstances = this
				.getThreeDiffrentNewInstanceOfClassUnderTest();

		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver null or null element",
						threeInstances);
		Assert
				.assertEquals(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does deliver an array of elements",
						3, threeInstances.length);

		final T x = threeInstances[0];
		final T y = threeInstances[1];
		final T z = threeInstances[2];

		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver null or null element",
						x);
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver null or null element",
						y);
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver null or null element",
						z);
		Assert
				.assertNotSame(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver identical element",
						x, y);
		Assert
				.assertNotSame(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver identical element",
						x, z);
		Assert
				.assertNotSame(
						"Implementations of AbstractObject_TestCase<T>#getThreeDiffrentNewInstanceOfClassUnderTest() does not deliver identical element",
						y, z);

		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "It is reflexive: for any non-null reference value x, x.equals(x) should return true",
						x.equals(x));
		// Note: x is non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
						x.equals(y) == true ? y.equals(x) == true : true
				// Note: x, y are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);

		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true",
						(x.equals(y) == true) && (y.equals(z) == true) ? x
								.equals(z) == true : true
				// Note: x, y, z are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5:"
								+ " It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified",
						x.equals(y) == x.equals(y)
				// Note: x, y are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5:"
								+ " For any non-null reference value x, x.equals(null) should return false",
						x.equals(null) == false
				// Note: x, y are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);
	}

	@Test
	public final void testEqualsWithIdenticalInstances() {
		final T x = this.getNewInstanceOfClassUnderTest();
		final T y = x;
		final T z = x;

		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						x);
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						y);
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						z);

		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "It is reflexive: for any non-null reference value x, x.equals(x) should return true",
						x.equals(x));
		// Note: x is non null cause of post-condition of
		// #getNewInstanceOfClassUnderTest()
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "It is symmetric: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true",
						x.equals(y) == true ? y.equals(x) == true : true
				// Note: x, y are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);

		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "It is transitive: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true",
						(x.equals(y) == true) & (y.equals(z) == true) ? x
								.equals(z) == true : true
				// Note: x, y, z are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5:"
								+ " It is consistent: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals comparisons on the objects is modified",
						x.equals(y) == x.equals(y)
				// Note: x, y are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);
		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5:"
								+ " For any non-null reference value x, x.equals(null) should return false",
						x.equals(null) == false
				// Note: x, y are non null cause of post-condition of
				// #getNewInstanceOfClassUnderTest()
				);
	}

	@Test
	public final void testEqualsWithIncompareableOtherObjectInstance() {
		final T x = this.getNewInstanceOfClassUnderTest();
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						x);

		final Object incomparableObject = this
				.getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest();
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() does not deliver null",
						incomparableObject);

		Assert
				.assertFalse(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() does not deliver a instance of a type T which is assignable from the type of class under test C: C.isAssignableFrom(T) == false",
						x.getClass().isAssignableFrom(
								incomparableObject.getClass()));

		Assert
				.assertFalse(
						"Equals returns false, if the type of the object to be compared to is incomparable to the type of the class under test",
						x.equals(incomparableObject));
	}

	@Test
	public final void testHashCode() {
		final T x = this.getNewInstanceOfClassUnderTest();
		final T y = this.getNewInstanceOfClassUnderTest();

		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						x);
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						y);

		Assert
				.assertTrue(
						"Copied from Java API documentation version JDK 1.5: "
								+ "Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application",
						x.hashCode() == x.hashCode());
		Assert
				.assertTrue(
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

	@Test
	public final void testToString() {
		final T x = this.getNewInstanceOfClassUnderTest();
		Assert
				.assertNotNull(
						"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
						x);

		Assert.assertNotNull("toString() does not deliver null", x.toString());
	}

	abstract protected T getNewInstanceOfClassUnderTest();

	abstract protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest();

	abstract protected T[] getThreeDiffrentNewInstanceOfClassUnderTest();

	private void dummyToSimulaizePossibilityToThrowCloneNotSupportedException()
			throws CloneNotSupportedException {
		if (1 != 1) {
			throw new CloneNotSupportedException();
		}
	}
}
