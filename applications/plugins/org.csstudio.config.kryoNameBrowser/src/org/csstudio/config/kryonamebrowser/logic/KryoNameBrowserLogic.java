package org.csstudio.config.kryonamebrowser.logic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

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
	public static final int NO_PARENT_PLANT_ID = 1;
	public static final int NO_PARENT_OBJECT_ID = 0;
	public static final int ROW_FETCH_SIZE = 50;

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 30; i++) {
			new Thread(new Runnable() {
				public void run() {

					KryoNameBrowserLogic logic = new KryoNameBrowserLogic(
							new OracleSettings());

					try {
						logic.openConnection();
						for (int i = 0; i < 100; i++) {

							long l = System.currentTimeMillis();

							KryoNameResolved resolved = new KryoNameResolved();
							resolved.getPlants().add(new KryoPlantEntry(4));
							logic.search(resolved);

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

	public void closeConnection() throws SQLException {
		database.closeConnection();

	}

	public KryoNameBrowserLogic(Settings settings) {
		this.database = new DBConnect(settings);
	}

	/**
	 * Opens the underlying database connection. You should call this method once before using the methods on this
	 * class.
	 * 
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
	public synchronized List<KryoNameResolved> search(KryoNameResolved example)
			throws SQLException {

		ArrayList<KryoNameResolved> results = retriveAll();

		List<KryoNameResolved> filter = filter(example, results);

		return filter;

	}

	private List<KryoNameResolved> filter(KryoNameResolved example,
			ArrayList<KryoNameResolved> results) {
		// filter
		// by name
		String name = example.getName();
		// by plant
		List<KryoPlantEntry> plants = example.getPlants();
		// by object
		List<KryoObjectEntry> objects = example.getObjects();
		// by process
		KryoProcessEntry processId = example.getProcess();
		// by cryo number
		int seqKryoNumber = example.getSeqKryoNumber();

		outer: for (Iterator<KryoNameResolved> iterator = results.iterator(); iterator
				.hasNext();) {
			KryoNameResolved kryoNameResolved = iterator.next();

			if (!isEmpty(name) && !name.equals(kryoNameResolved.getName())) {
				iterator.remove();

				continue;
			}

			if (plants.size() > 0) {
				List<KryoPlantEntry> plantsResolved = kryoNameResolved
						.getPlants();

				if (plants.size() > plantsResolved.size()) {
					iterator.remove();
					continue outer;
				}

				for (int i = 0; i < plants.size(); i++) {
					KryoPlantEntry exampleE = plants.get(i);
					KryoPlantEntry resolvedE = plantsResolved.get(i);
					if (exampleE.getId() == resolvedE.getId()) {
						if (exampleE.getNumberOfPlants() >= 0
								&& resolvedE.getNumberOfPlants() != exampleE
										.getNumberOfPlants()) {
							iterator.remove();
							continue outer;
						}
					} else {
						iterator.remove();
						continue outer;
					}
				}

			}

			if (objects.size() > 0) {
				List<KryoObjectEntry> objectsResolved = kryoNameResolved
						.getObjects();

				if (objects.size() > objectsResolved.size()) {
					iterator.remove();
					continue outer;
				}

				for (int i = 0; i < objects.size(); i++) {
					if (objects.get(i).getId() != objectsResolved.get(i)
							.getId()) {
						iterator.remove();
						continue outer;
					}

				}

			}

			if (processId != null
					&& !processId.getId().equals(
							kryoNameResolved.getProcess().getId())) {
				iterator.remove();
				continue outer;
			}

			if (seqKryoNumber >= 0) {
				if (!(seqKryoNumber == kryoNameResolved.getSeqKryoNumber())) {

					iterator.remove();
				}
			}

		}

		return results;
	}

	private ArrayList<KryoNameResolved> retriveAll() throws SQLException {
		StringBuffer buffer = new StringBuffer(
				"SELECT IO_NAME_ID , IO_NAME , PLANT_ID , OBJECT_ID , CRYO_PROCESS_ID , SEQ_KRYO_NUMBER , KRYO_NAME_LABEL FROM ")
				.append(TableNames.NAMES_TABLE);

		ArrayList<KryoNameResolved> results = new ArrayList<KryoNameResolved>();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = database.getConnection().createStatement();
			statement.setFetchSize(ROW_FETCH_SIZE);

			resultSet = statement.executeQuery(buffer.toString());

			HashMap<Integer, KryoObjectEntry> objectCache = new HashMap<Integer, KryoObjectEntry>();
			HashMap<Integer, KryoPlantEntry> plantCache = new HashMap<Integer, KryoPlantEntry>();

			while (resultSet.next()) {

				// resolve all but the types that have subtypes
				KryoNameResolved kryoNameResolved = new KryoNameResolved(
						resultSet.getString(2), resultSet.getString(7),
						resultSet.getInt(1), resultSet.getInt(6),
						getProcessEntry(resultSet.getString(5)));

				// resolve the subtypes into a list
				List<KryoObjectEntry> objects = kryoNameResolved.getObjects();

				KryoObjectEntry kryoObjectEntry = getObjectEntry(resultSet
						.getInt(4), objectCache);

				while (kryoObjectEntry != null) {

					objects.add(kryoObjectEntry);
					kryoObjectEntry = getObjectEntry(kryoObjectEntry
							.getParent(), objectCache);
				}

				Collections.reverse(objects);

				List<KryoPlantEntry> plants = kryoNameResolved.getPlants();

				KryoPlantEntry kryoPlantEntry = getPlantEntry(resultSet
						.getInt(3), plantCache);

				// split by ':' in two halves, use the left halve to check
				// numbers

				String[] split = kryoNameResolved.getName().split(":");

				String[] plantHalve = split[0].split("[A-Z]+");

				int plantHalveIndex = plantHalve.length - 1;

				while (kryoPlantEntry != null) {
					if (kryoPlantEntry.getNumberOfPlants() > 0) {

						try {
							kryoPlantEntry.setNumberOfPlants(Integer
									.parseInt(plantHalve[plantHalveIndex]));
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,
									"Invalid name entry found "
											+ kryoNameResolved.getName()
											+ " please notify supervisor!");
						}

						plantHalveIndex--;
					} else {
						kryoPlantEntry.setNumberOfPlants(-1);
					}
					plants.add(kryoPlantEntry);
					kryoPlantEntry = getPlantEntry(kryoPlantEntry.getParent(),
							plantCache);
				}

				Collections.reverse(plants);

				results.add(kryoNameResolved);
			}

		} finally {

			if (statement != null) {
				statement.close();
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

			return new KryoObjectEntry(objectCache.get(id));
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
			return new KryoPlantEntry(plantCache.get(id));
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

	private boolean isLowestLevelPlant(int id) throws SQLException {
		Statement statement = database.getConnection().createStatement();
		boolean next = false;
		try {
			next = statement.executeQuery(
					"select PLANT_NAME from NSB_PLANT where PLANT_PARENT='"
							+ id + "'").next();

		} finally {
			statement.close();
		}

		return next;

	}

	private boolean isLowestLevelObject(int id) throws SQLException {
		Statement statement = database.getConnection().createStatement();
		boolean next = false;
		try {
			next = statement.executeQuery(
					"select OBJECT_NAME from NSB_OBJECT where OBJECT_PARENT='"
							+ id + "'").next();

		} finally {
			statement.close();
		}

		return next;

	}

	public synchronized void add(KryoNameEntry newEntry) throws SQLException {

		if (newEntry.getName() != null || newEntry.getName().isEmpty()
				|| newEntry.getProcessId() == null
				|| newEntry.getProcessId().isEmpty()) {
			throw new IllegalStateException("Missing name or process");
		}

		if (doesExist(newEntry.getName())) {
			throw new IllegalStateException("Cannot add already existing name");
		}

		Statement statement = database.getConnection().createStatement();

		try {

			// make sure that numbers are not set where there are not allowed
			// and the only the lowest level is added.

			if (!isLowestLevelPlant(newEntry.getPlantId())) {
				throw new RuntimeException("Validation failed");
			}

			if (!isLowestLevelObject(newEntry.getObjectId())) {
				throw new RuntimeException("Validation failed");
			}

			// TODO: validation of numbers set is quite difficult, if time will
			// add later

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
	 * The entry's name will be used to update the label from the corresponding name entry in the database.
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
	 * Returns a list of {@link KryoObjectEntry} where their parent is specified.
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