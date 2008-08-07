package org.csstudio.config.kryonamebrowser.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.csstudio.config.kryonamebrowser.config.OracleSettings;
import org.csstudio.config.kryonamebrowser.config.Settings;
import org.csstudio.config.kryonamebrowser.database.DBConnect;
import org.csstudio.config.kryonamebrowser.database.TableNames;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;

/**
 * Main logic for the name browser.
 * 
 * @author Alen Vrecko
 */
public class KryoNameBrowserLogic {

	private DBConnect database;
	private static final int NO_PARENT_PLANT_ID = 0;
	private static final int NO_PARENT_OBJECT_ID = 0;
	private static final int ROW_FETCH_SIZE = 50;

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 30; i++) {
			new Thread(new Runnable() {
				public void run() {
					

					KryoNameBrowserLogic logic = new KryoNameBrowserLogic(new OracleSettings());
					
					try {
						logic.openConnection();
						for (int i = 0; i < 100; i++) {

							long l = System.currentTimeMillis();

							logic.search(new KryoNameEntry());

							System.out.println(""
									+ Thread.currentThread().getName() + " "
									+ (System.currentTimeMillis() - l));

							try {
								Thread.sleep((long) (Math.random() * 10000));
							} catch (InterruptedException e) {
								e.printStackTrace(); // To change body of catch
														// statement use File |
														// Settings | File
														// Templates.
							}

						}

					} catch (SQLException e) {
						e.printStackTrace();
						
					} 

					try {
						logic.closeConnection();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		}
	}

	public  void closeConnection() throws SQLException {
		database.closeConnection();
		
	}

	public KryoNameBrowserLogic(Settings settings) {
		this.database = new DBConnect(settings);
	}

	/**
	 * Opens the underlying database connection. You should call this method
	 * once before using the methods on this class.
	 * @throws SQLException 
	 */
	public synchronized void openConnection() throws SQLException {
		database.openConnection();
	}

	/**
	 * List of all {@link KryoNameResolved} which are subsets of the example.
	 * 
	 * @param example
	 *            entry used for comparison
	 * @return list of all resolved objects
	 * @throws SQLException
	 *             if something went wrong with the database
	 */
	public synchronized List<KryoNameResolved> search(KryoNameEntry example)
			throws SQLException {

		
		StringBuffer buffer = new StringBuffer(
				"SELECT IO_NAME_ID , IO_NAME , PLANT_ID , OBJECT_ID , CRYO_PROCESS_ID , SEQ_KRYO_NUMBER , KRYO_NAME_LABEL FROM ")
				.append(TableNames.NAMES_TABLE);

		// in first part just get all names resolved
		ArrayList<KryoNameResolved> results = new ArrayList<KryoNameResolved>();
		Statement statement = database.getConnection().createStatement();
		statement.setFetchSize(ROW_FETCH_SIZE);
		ResultSet resultSet = statement.executeQuery(buffer.toString());

		HashMap<Integer, KryoObjectEntry> objectCache = new HashMap<Integer, KryoObjectEntry>();
		HashMap<Integer, KryoPlantEntry> plantCache = new HashMap<Integer, KryoPlantEntry>();

		while (resultSet.next()) {

			// resolve all but the types that have subtypes
			KryoNameResolved kryoNameResolved = new KryoNameResolved(resultSet
					.getString(2), resultSet.getString(7), resultSet.getInt(1),
					resultSet.getInt(6),
					getProcessEntry(resultSet.getString(5)));

			// resolve the subtypes into a list
			List<KryoObjectEntry> objects = kryoNameResolved.getObjects();

			KryoObjectEntry kryoObjectEntry = getObjectEntry(resultSet
					.getInt(4), objectCache);

			while (kryoObjectEntry != null) {
				objects.add(kryoObjectEntry);
				kryoObjectEntry = getObjectEntry(kryoObjectEntry.getParent(),
						objectCache);
			}

			Collections.reverse(objects);

			List<KryoPlantEntry> plants = kryoNameResolved.getPlants();

			KryoPlantEntry kryoPlantEntry = getPlantEntry(resultSet.getInt(3),
					plantCache);

			while (kryoPlantEntry != null) {
				plants.add(kryoPlantEntry);
				kryoPlantEntry = getPlantEntry(kryoPlantEntry.getParent(),
						plantCache);
			}

			Collections.reverse(plants);

			results.add(kryoNameResolved);
		}

		// second part: filter trough the names
		resultSet.close();
		statement.close();

		// filter
		// by name
		String name = example.getName();
		// by plant
		int plantId = example.getPlantId();
		// by object
		int objectId = example.getObjectId();
		// by process
		String processId = example.getProcessId();
		// by cryo number
		int seqKryoNumber = example.getSeqKryoNumber();

		for (Iterator<KryoNameResolved> iterator = results.iterator(); iterator
				.hasNext();) {
			KryoNameResolved kryoNameResolved = iterator.next();

			if (!isEmpty(name) && !name.equals(kryoNameResolved.getName())) {
				iterator.remove();
				System.out.println("NAme");
				continue;
			}

			if (plantId >= 0) {
				boolean contains = false;

				for (KryoPlantEntry plant : kryoNameResolved.getPlants()) {
					if (plantId == plant.getId()) {
						contains = true;
						break;
					}

				}

				if (!contains) {
					iterator.remove();
					System.out.println("PLant");
					continue;
				}

			}

			if (objectId >= 0) {
				boolean contains = false;

				for (KryoObjectEntry object : kryoNameResolved.getObjects()) {
					if (objectId == object.getId()) {
						contains = true;
						break;
					}

				}

				if (!contains) {
					iterator.remove();
					System.out.println("Object");
					continue;
				}

			}

			if (!isEmpty(processId)) {

				if (kryoNameResolved.getProcess() == null
						|| !processId.equals(kryoNameResolved.getProcess()
								.getId())) {
					iterator.remove();
					System.out.println("Process");
					continue;
				}

			}

			if (seqKryoNumber >= 0) {
				if (!(seqKryoNumber == kryoNameResolved.getSeqKryoNumber())) {
					System.out.println("Seq");
					iterator.remove();
				}
			}

		}

		return results;

	}

	private KryoProcessEntry getProcessEntry(String id) throws SQLException {
		Statement statement = database.getConnection().createStatement();
		ResultSet resultSet = statement
				.executeQuery("select CRYO_PROCESS_NAME, CRYO_PROCESS_ID, CRYO_PROCESS_EXPLANATION "
						+ "from NSB_CRYO_PROCESS where NSB_CRYO_PROCESS.CRYO_PROCESS_ID = "
						+ id);

		try {
			if (resultSet.next()) {
				return new KryoProcessEntry(resultSet.getString(1), resultSet
						.getString(2), resultSet.getString(3));
			} else {
				throw new RuntimeException("Missing process for id " + id);
			}
		} finally {
			statement.close();
		}
	}

	private KryoObjectEntry getObjectEntry(int id,
			HashMap<Integer, KryoObjectEntry> objectCache) throws SQLException {
		if (id == NO_PARENT_OBJECT_ID) {
			return null;
		}

		if (objectCache.containsKey(id)) {
			return objectCache.get(id);
		}

		Statement statement = database.getConnection().createStatement();
		ResultSet resultSet = statement
				.executeQuery("Select OBJECT_NAME, OBJECT_EXPLANATION, OBJECT_ID, OBJECT_PARENT, OBJECT_LABEL , OBJECT_LEVEL from NSB_OBJECT where NSB_OBJECT.OBJECT_ID = "
						+ id);

		try {
			if (resultSet.next()) {
				KryoObjectEntry entry = new KryoObjectEntry(resultSet
						.getString(1), resultSet.getString(2), resultSet
						.getInt(3), resultSet.getInt(4),
						resultSet.getString(5), resultSet.getInt(6));
				objectCache.put(id, entry);
				return entry;
			} else {
				throw new RuntimeException("Missing object for id " + id);
			}
		} finally {
			statement.close();
		}
	}

	private KryoPlantEntry getPlantEntry(int id,
			HashMap<Integer, KryoPlantEntry> plantCache) throws SQLException {
		if (id == NO_PARENT_PLANT_ID) {
			return null;
		}

		if (plantCache.containsKey(id)) {
			return plantCache.get(id);
		}

		Statement statement = database.getConnection().createStatement();
		ResultSet resultSet = statement
				.executeQuery("select PLANT_NAME, PLANT_LABEL, PLANT_EXPLANATION, PLANT_ID,"
						+ " PLANT_PARENT, PLANT_NO from NSB_PLANT where NSB_PLANT.PLANT_ID = "
						+ id);

		try {
			if (resultSet.next()) {
				KryoPlantEntry kryoPlantEntry = new KryoPlantEntry(resultSet
						.getString(1), resultSet.getString(2), resultSet
						.getString(3), resultSet.getInt(4),
						resultSet.getInt(5), resultSet.getInt(6));
				plantCache.put(id, kryoPlantEntry);
				return kryoPlantEntry;
			} else {
				throw new RuntimeException("Missing plant for id" + id);
			}
		} finally {
			statement.close();
		}
	}

	public synchronized void add(KryoNameEntry newEntry) throws SQLException {
		Statement statement = database.getConnection().createStatement();

		try {
			statement
					.executeUpdate("insert into NSB_IO_NAME (IO_NAME, PLANT_ID, OBJECT_ID, CRYO_PROCESS_ID, "
							+ "SEQ_KRYO_NUMBER, KRYO_NAME_LABEL) values ('"
							+ newEntry.getName()
							+ "','"
							+ newEntry.getPlantId()
							+ "','"
							+ newEntry.getObjectId()
							+ "','"
							+ newEntry.getProcessId()
							+ "','"
							+ newEntry.getSeqKryoNumber()
							+ "','"
							+ newEntry.getLabel() + "')");

		} finally {
			statement.close();
		}
	}

	public synchronized void delete(KryoObjectEntry kryoNameEntry)
			throws SQLException {
		Statement statement = database.getConnection().createStatement();
		try {
			statement.executeUpdate("delete from NSB_IO_NAME where IO_NAME = '"
					+ kryoNameEntry.getName() + "'");
		} finally {
			statement.close();
		}
	}

	/**
	 * The entry's name will be used to update the label from the corresponding
	 * name entry in the database.
	 * 
	 * @param kryoNameEntry
	 *            entry from which to use the name and new label
	 */
	public synchronized void updateLabel(KryoObjectEntry kryoNameEntry)
			throws SQLException {

		Statement statement = database.getConnection().createStatement();

		try {
			statement
					.executeUpdate("update NSB_IO_NAME set KRYO_NAME_LABEL = '"
							+ kryoNameEntry.getLabel() + "' where IO_NAME = '"
							+ kryoNameEntry.getName() + "'");

		} finally {
			statement.close();
		}

	}

	/**
	 * Checks if the name already exists.
	 * 
	 * @param name
	 *            name of the PV
	 * @return true if exists
	 */
	public synchronized boolean doesExist(String name) throws SQLException {
		Statement statement = database.getConnection().createStatement();
		boolean next = false;
		try {
			next = statement.executeQuery(
					"select IO_NAME from NSB_IO_NAME where IO_NAME='" + name
							+ "'").next();

		} finally {
			statement.close();
		}

		return next;

	}

	/**
	 * Returns a list of {@link KryoObjectEntry} where their parent is 0.
	 * 
	 * @return list of toplevel entries
	 */
	public synchronized List<KryoObjectEntry> findToplevelObjectChoices()
			throws SQLException {
		return findObjectChoices(new KryoObjectEntry(NO_PARENT_OBJECT_ID));
	}

	/**
	 * Returns a list of {@link KryoObjectEntry} where their parent is
	 * specified.
	 * 
	 * @param parent
	 *            entry
	 * @return list of entries corresponding to the parent
	 */
	public synchronized List<KryoObjectEntry> findObjectChoices(
			KryoObjectEntry parent) throws SQLException {

		Statement statement = database.getConnection().createStatement();
		statement.setFetchSize(ROW_FETCH_SIZE);

		ArrayList<KryoObjectEntry> entries;
		try {

			ResultSet rs = statement
					.executeQuery("select OBJECT_NAME, OBJECT_EXPLANATION, OBJECT_ID, OBJECT_PARENT,"
							+ " OBJECT_LABEL , OBJECT_LEVEL from NSB_OBJECT where NSB_OBJECT.OBJECT_PARENT = "
							+ parent.getId());

			entries = new ArrayList<KryoObjectEntry>();

			while (rs.next()) {
				entries.add(new KryoObjectEntry(rs.getString(1), rs
						.getString(2), rs.getInt(3), rs.getInt(4), rs
						.getString(5), rs.getInt(6)));
			}

		} finally {
			statement.close();
		}

		return entries;
	}

	/**
	 * Returns a list of {@link KryoPlantEntry} where their parent is 0.
	 * 
	 * @return list of toplevel entries
	 * @throws java.sql.SQLException
	 */
	public synchronized List<KryoPlantEntry> findToplevelPlantChoices()
			throws SQLException {
		return findPlantChoices(new KryoPlantEntry(NO_PARENT_PLANT_ID));
	}

	/**
	 * Returns a list of {@link KryoPlantEntry} where their parent is specified.
	 * 
	 * @param parent
	 *            entry
	 * @return list of entries corresponding to the parent
	 * @throws java.sql.SQLException
	 */
	public synchronized List<KryoPlantEntry> findPlantChoices(
			KryoPlantEntry parent) throws SQLException {

		List<KryoPlantEntry> results = new ArrayList<KryoPlantEntry>();
		Statement statement = database.getConnection().createStatement();
		statement.setFetchSize(ROW_FETCH_SIZE);

		try {
			ResultSet rs = statement
					.executeQuery("select PLANT_NAME, PLANT_LABEL, PLANT_EXPLANATION, PLANT_ID,"
							+ " PLANT_PARENT, PLANT_NO from NSB_PLANT where NSB_PLANT.PLANT_PARENT = "
							+ parent.getId());
			while (rs.next()) {
				results.add(new KryoPlantEntry(rs.getString(1),
						rs.getString(2), rs.getString(3), rs.getInt(4), rs
								.getInt(5), rs.getInt(6)));
			}
		} finally {
			statement.close();
		}

		return results;
	}

	/**
	 * Returns a list of {@link KryoProcessEntry}.
	 * 
	 * @return list of all process entries.
	 * @throws java.sql.SQLException
	 */
	public synchronized List<KryoProcessEntry> findProcessChoices()
			throws SQLException {

		List<KryoProcessEntry> results = new ArrayList<KryoProcessEntry>();

		Statement statement = database.getConnection().createStatement();
		statement.setFetchSize(ROW_FETCH_SIZE);
		try {
			ResultSet rs = statement
					.executeQuery("select CRYO_PROCESS_NAME, CRYO_PROCESS_ID, CRYO_PROCESS_EXPLANATION "
							+ "from NSB_CRYO_PROCESS");

			while (rs.next()) {
				results.add(new KryoProcessEntry(rs.getString(1), rs
						.getString(2), rs.getString(3)));
			}

		} finally {
			statement.close();
		}

		return results;
	}

	private boolean isEmpty(String string) {
		return string == null || "".equals(string);
	}

}