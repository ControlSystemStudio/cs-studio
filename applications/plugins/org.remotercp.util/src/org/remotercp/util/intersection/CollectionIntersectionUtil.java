package org.remotercp.util.intersection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionIntersectionUtil {

	@SuppressWarnings("unchecked")
	public static Set<Object> getIntersectionSet(Collection objects) {
		Set<Object> intersectionSet = new HashSet<Object>();

		for (Object obj : objects) {
			if (obj instanceof Set) {
				Set<Object> set = (Set<Object>) obj;

				/*
				 * If the intersaction is emty put initial data in the set
				 */
				if (intersectionSet.isEmpty()) {
					intersectionSet.addAll(set);
				} else {
					/*
					 * transforms inersectionSet into intersection of
					 * intersectionSet and set
					 * 
					 * The intersection of two sets is the set containing only
					 * the elements common to both sets
					 */

					intersectionSet.retainAll(set);
				}
			}

		}

		return intersectionSet;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> getIntersectionCollection(
			Collection objects) {
		Collection<Object> intersectionCollection = new ArrayList<Object>();

		for (Object obj : objects) {
			if (obj instanceof Collection) {
				Collection<Object> temp = (Collection<Object>) obj;
				if (intersectionCollection.isEmpty()) {
					intersectionCollection.addAll(temp);
				} else {
					intersectionCollection.retainAll(temp);
				}
			}
		}
		return intersectionCollection;
	}
}
