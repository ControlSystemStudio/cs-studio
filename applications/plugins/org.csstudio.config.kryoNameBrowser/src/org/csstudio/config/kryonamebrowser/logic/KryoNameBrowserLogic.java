package org.csstudio.config.kryonamebrowser.logic;

import org.csstudio.config.kryonamebrowser.config.OracleSettings;
import org.csstudio.config.kryonamebrowser.database.DBConnect;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoProcessEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KryoNameBrowserLogic {

    private DBConnect database;

    public static void main(String[] args) {
        DBConnect database1 = new DBConnect(new OracleSettings());
        database1.openConnection();

        KryoNameBrowserLogic logic = new KryoNameBrowserLogic(database1);

        List<KryoObjectEntry> kryoObjectEntryList = logic.findToplevelObjectChoices();

        database1.closeConnection();
    }

    public KryoNameBrowserLogic(DBConnect database) {
        this.database = database;
    }

    public List<KryoNameResolved> search(KryoNameEntry example) {




        return null;

    }

    public void add(KryoNameEntry newEntry) {
        try {
            database.executeUpdate("insert into NSB_IO_NAME (IO_NAME, PLANT_ID, OBJECT_ID, CRYO_PROCESS_ID, "
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
                    + "','" + newEntry.getLabel() + "')");

        } catch (SQLException e) {
            throw new RuntimeException("Failed ", e);
        }
    }

    public void delete(KryoObjectEntry kryoNameEntry) {
        try {
            database.executeUpdate("delete from NSB_IO_NAME where IO_NAME = '"
                    + kryoNameEntry.getName() + "'");
        } catch (SQLException e) {
            throw new RuntimeException("Failed ", e);
        }
    }

    /**
     * The entry's name will be used to update the label from the corresponding name entry in the database.
     *
     * @param kryoNameEntry entry from which to use the name and new label
     */
    public void updateLabel(KryoObjectEntry kryoNameEntry) {
        try {
            database.executeUpdate("update NSB_IO_NAME set KRYO_NAME_LABEL = '"
                    + kryoNameEntry.getLabel() + "' where IO_NAME = '" + kryoNameEntry.getName() + "'");
        } catch (SQLException e) {
            throw new RuntimeException("Failed ", e);
        }
    }

    /**
     * Checks if the name already exists.
     *
     * @param name name of the PV
     * @return true if exists
     */
    public boolean doesExist(String name) {
        try {
            return database.executeQuery("select IO_NAME from NSB_IO_NAME where IO_NAME='" + name + "'").next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed ", e);
        }
    }

    /**
     * Returns a list of {@link KryoObjectEntry} where their parent is 0.
     *
     * @return list of toplevel entries
     */
    public List<KryoObjectEntry> findToplevelObjectChoices() {
        return findObjectChoices(new KryoObjectEntry(0));
    }

    /**
     * Returns a list of {@link KryoObjectEntry} where their parent is specified.
     *
     * @param parent entry
     * @return list of entries corresponding to the parent
     */
    public List<KryoObjectEntry> findObjectChoices(KryoObjectEntry parent) {
        try {
            ResultSet rs = database.executeQuery("select OBJECT_NAME, OBJECT_EXPLANATION, OBJECT_ID, OBJECT_PARENT," +
                    " OBJECT_LABEL , OBJECT_LEVEL from NSB_OBJECT where NSB_OBJECT.OBJECT_PARENT = "
                    + parent.getId() + ")");
            ArrayList<KryoObjectEntry> entries = new ArrayList<KryoObjectEntry>();
            while (rs.next()) {
                entries.add(new KryoObjectEntry(rs.getString(1), rs.getString(2), rs
                        .getInt(3), rs.getInt(4), rs.getString(5), rs.getInt(6)));
            }

            return entries;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get toplevel objects " + e);
        }

    }

    /**
     * Returns a list of {@link KryoPlantEntry} where their parent is 0.
     *
     * @return list of toplevel entries
     */
    public List<KryoPlantEntry> findToplevelPlantChoices() {
        return findPlantChoices(new KryoPlantEntry(0));
    }

    /**
     * Returns a list of {@link KryoPlantEntry} where their parent is specified.
     *
     * @param parent entry
     * @return list  of entries corresponding to the parent
     */
    public List<KryoPlantEntry> findPlantChoices(KryoPlantEntry parent) {
        try {
            List<KryoPlantEntry> results = new ArrayList<KryoPlantEntry>();
            ResultSet rs = database.executeQuery("select PLANT_NAME, PLANT_LABEL, PLANT_EXPLANATION, PLANT_ID," +
                    " PLANT_PARENT, PLANT_NO from NSB_PLANT where NSB_PLANT.PLANT_PARENT = "
                    + parent.getId());
            while (rs.next()) {
                results.add(new KryoPlantEntry(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),
                        rs.getInt(5), rs.getInt(6)));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed " + e);
        }

    }

    /**
     * Returns a list of {@link KryoProcessEntry}.
     *
     * @return list of all process entries.
     */
    public List<KryoProcessEntry> findToplevelProcessChoices() {
        try {
            List<KryoProcessEntry> results = new ArrayList<KryoProcessEntry>();

            ResultSet rs = database.executeQuery("select CRYO_PROCESS_NAME, CRYO_PROCESS_ID, CRYO_PROCESS_EXPLANATION " +
                    "from NSB_CRYO_PROCESS");
            while (rs.next()) {
                results.add(new KryoProcessEntry(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed " + e);
        }

    }


}
