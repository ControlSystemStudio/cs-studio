package org.csstudio.sds.internal.connection;

/**
 * Central definitions of the SDS connection settings.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public class ConnectionSettings {
	/**
	 * The update interval of the bundeling thread.
	 */
	public static final int BUNDELING_THREADS_REFRESH_RATE = 1000/48;

	/**
	 * The amount of widget models that are created at once from the connection
	 * performance view.
	 */
	public static final int CREATE_TESTDUMMIES_COUNT = 500;

	/**
	 * Flag that indicates if the created widget models should have a dynamic
	 * fill level.
	 */
	public static final boolean CREATE_TESTDUMMIES_WITH_FILL = true;

	/**
	 * Flag that indicates if the created widgets should have a dynamic
	 * background color.
	 */
	public static final boolean CREATE_TESTDUMMIES_WITH_COLOR = false;

	/**
	 * Flag that indicats if the created widgets should by dynamised for DAL or
	 * the internal data simulator.
	 */
	public static final boolean FOR_DAL = true;

}
