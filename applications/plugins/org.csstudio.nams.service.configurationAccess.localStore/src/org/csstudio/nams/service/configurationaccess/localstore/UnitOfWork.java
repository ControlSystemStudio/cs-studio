
package org.csstudio.nams.service.configurationaccess.localstore;

/**
 * Encapsulates exact one unti of work for a database access.
 */
interface UnitOfWork<T> {

	/**
	 * Performs the work. Accessing database using given mapper.
	 */
	public T doWork(Mapper mapper) throws Throwable;
}