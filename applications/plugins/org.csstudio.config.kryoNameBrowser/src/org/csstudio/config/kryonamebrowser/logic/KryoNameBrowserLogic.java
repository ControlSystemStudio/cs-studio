package org.csstudio.config.kryonamebrowser.logic;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.csstudio.config.kryonamebrowser.config.OracleSettings;
import org.csstudio.config.kryonamebrowser.config.Settings;
import org.csstudio.config.kryonamebrowser.database.DBConnect;
import org.csstudio.config.kryonamebrowser.database.TableNames;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoPlantEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoPlantResolved;

/**
 * Main logic for the name browser.
 * 
 * @author Alen Vrecko
 */
public class KryoNameBrowserLogic {
    
    private static final int DESCRIPTION_LENGTH = 200;
    private DBConnect database;
    public static final int NO_PARENT_PLANT_ID = 1;
    public static final int NO_PARENT_SUPER_PLANT_ID = 0;
    public static final int NO_PARENT_OBJECT_ID = 0;
    public static final int ROW_FETCH_SIZE = 50;
    private static short NAME_CELL_WIDTH = 4000;
    
    private static short NOM_CELL_WIDTH = 1000;
    
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            new Thread(new Runnable() {
                public void run() {
                    
                    KryoNameBrowserLogic logic = new KryoNameBrowserLogic(new OracleSettings());
                    
                    try {
                        logic.openConnection();
                        for (int i = 0; i < 100; i++) {
                            
                            long l = System.currentTimeMillis();
                            
                            KryoNameResolved resolved = new KryoNameResolved();
                            resolved.getPlants().add(new KryoPlantResolved(4));
                            logic.search(resolved);
                            
                            System.out.println("" + Thread.currentThread().getName() + " "
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
     * Opens the underlying database connection. You should call this method once before using the
     * methods on this class.
     * 
     * @throws SQLException
     */
    public synchronized void openConnection() throws SQLException {
        database.openConnection();
    }
    
    private String getQueryForExample(KryoNameResolved example) {
        StringBuilder nameQuery = new StringBuilder();
        
        // handle plants filtering
        List<KryoPlantResolved> plantsExample = example.getPlants();
        boolean isUsed = false;
        if (plantsExample.size() > 0) {
            isUsed = true;
            // TODO: Handle better? HARD-CODED VALUE
            nameQuery.append("X");
            for (KryoPlantEntry kryoPlantEntry : plantsExample) {
                nameQuery.append(kryoPlantEntry.getLabel());
                
                if (kryoPlantEntry.getNumberOfPlants() > 0) {
                    nameQuery.append(kryoPlantEntry.getNumberOfPlants());
                    nameQuery.append("%");
                } else {
                    nameQuery.append("%");
                }
            }
            
        }
        
        nameQuery.append("%:%");
        
        // handle objects entry
        List<KryoObjectEntry> objectsExample = example.getObjects();
        if (objectsExample.size() > 0) {
            isUsed = true;
            // remove the last one which is '%'
            nameQuery.deleteCharAt(nameQuery.length() - 1);
            
            for (KryoObjectEntry kryoObjectEntry : objectsExample) {
                nameQuery.append(kryoObjectEntry.getLabel());
            }
            nameQuery.append("%");
            
        }
        
        // handle process and seq kryo number
        // process id is combined with seq number.
        // if (example.getProcess() != null) {
        // isUsed = true;
        // nameQuery.append(example.getProcess().getId());
        // }
        
        if (example.getSeqKryoNumber() >= 0) {
            isUsed = true;
            int number = example.getSeqKryoNumber();
            if (number < 10) {
                nameQuery.append("000" + number);
            } else if (number < 100) {
                nameQuery.append("00" + number);
            } else if (number < 1000) {
                nameQuery.append("0" + number);
            } else {
                nameQuery.append(number);
            }
        } else {
            nameQuery.append("__");
        }
        
        return isUsed ? nameQuery.toString() : "";
    }
    
    public List<KryoNameResolved> search(String searchExpression) throws SQLException {
        
        StringBuffer selectQuery = new StringBuffer("SELECT IO_NAME_ID , IO_NAME , PLANT_ID , OBJECT_ID , SEQ_KRYO_NUMBER_4 , KRYO_NAME_LABEL FROM ")
                .append(TableNames.NAMES_TABLE);
        
        selectQuery.append("  WHERE  io_name like '").append(searchExpression).append("'");
        
        return searchSQL(selectQuery.toString());
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
    public synchronized List<KryoNameResolved> search(KryoNameResolved example) throws SQLException {
        
        StringBuffer selectQuery = new StringBuffer("SELECT IO_NAME_ID , IO_NAME , PLANT_ID , OBJECT_ID , SEQ_KRYO_NUMBER_4 , KRYO_NAME_LABEL FROM ")
                .append(TableNames.NAMES_TABLE);
        
        String nameQuery = getQueryForExample(example);
        
        if (nameQuery.length() > 0) {
            selectQuery.append("  WHERE  io_name like '").append(nameQuery.toString()).append("'");
        }
        
        return searchSQL(selectQuery.toString());
        
    }
    
    public synchronized List<KryoNameResolved> searchSQL(String sql) throws SQLException {
        
        ArrayList<KryoNameResolved> results = new ArrayList<KryoNameResolved>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = database.getConnection().createStatement();
            statement.setFetchSize(ROW_FETCH_SIZE);
            
            resultSet = statement.executeQuery(sql);
            
            HashMap<Integer, KryoObjectEntry> objectCache = new HashMap<Integer, KryoObjectEntry>();
            HashMap<Integer, KryoPlantEntry> plantCache = new HashMap<Integer, KryoPlantEntry>();
            
            while (resultSet.next()) {
                results.add(convert(resultSet, objectCache, plantCache));
            }
            
        } finally {
            
            if (statement != null) {
                statement.close();
            }
            
        }
        return results;
        
    }
    
    private KryoNameResolved convert(ResultSet resultSet,
                                     HashMap<Integer, KryoObjectEntry> objectCache,
                                     HashMap<Integer, KryoPlantEntry> plantCache) throws SQLException {
        // resolve all but the types that have subtypes
        KryoNameResolved kryoNameResolved = new KryoNameResolved(resultSet.getString(2), resultSet
                .getString(6), resultSet.getInt(1), resultSet.getInt(5));
        
        // resolve the subtypes into a list
        List<KryoObjectEntry> objects = kryoNameResolved.getObjects();
        
        KryoObjectEntry kryoObjectEntry = getObjectEntry(resultSet.getInt(4), objectCache);
        
        while (kryoObjectEntry != null) {
            
            objects.add(kryoObjectEntry);
            kryoObjectEntry = getObjectEntry(kryoObjectEntry.getParent(), objectCache);
        }
        
        Collections.reverse(objects);
        
        List<KryoPlantResolved> plants = kryoNameResolved.getPlants();
        
        KryoPlantEntry kryoPlantEntry = getPlantEntry(resultSet.getInt(3), plantCache);
        
        // split by ':' in two halves, use the left halve to check
        // numbers
        
        String[] split = kryoNameResolved.getName().split(":");
        
        String[] plantHalve = split[0].split("[A-Z]+");
        
        int plantHalveIndex = plantHalve.length - 1;
        
        while (kryoPlantEntry != null) {
            int nrOfPlants = -1;
            
            // do we allow plants, if so parse the int.
            if (kryoPlantEntry.getNumberOfPlants() > 0) {
                
                try {
                    nrOfPlants = Integer.parseInt(plantHalve[plantHalveIndex]);
                    
                } catch (Exception e) {
                    // TODO: invalid entry, not valid LOG LOG LOG !!!
                }
                
                plantHalveIndex--;
            }
            
            KryoPlantResolved plantResolved = new KryoPlantResolved(kryoPlantEntry);
            plantResolved.setNumberOfPlants(nrOfPlants);
            
            plants.add(plantResolved);
            kryoPlantEntry = getPlantEntry(kryoPlantEntry.getParent(), plantCache);
        }
        
        Collections.reverse(plants);
        return kryoNameResolved;
        
    }
    
    public synchronized KryoNameResolved getName(int id) throws SQLException {
        
        Statement statement = database.getConnection().createStatement();
        try {
            ResultSet rs = statement
                    .executeQuery("SELECT IO_NAME_ID , IO_NAME , PLANT_ID , OBJECT_ID , SEQ_KRYO_NUMBER_4 , KRYO_NAME_LABEL FROM "
                            + TableNames.NAMES_TABLE + " WHERE IO_NAME_ID = " + id);
            
            if (rs.next()) {
                return convert(rs,
                               new HashMap<Integer, KryoObjectEntry>(),
                               new HashMap<Integer, KryoPlantEntry>());
            } else {
                return null;
            }
            
        } finally {
            statement.close();
        }
        
    }
    
    /**
     * Get Process from process id. TODO keep table content to prevent database query for each
     * request.
     * 
     * @param id
     * @return
     * @throws SQLException
     */
    // private KryoProcessEntry getProcessEntry(String id) throws SQLException {
    // Statement statement = database.getConnection().createStatement();
    // ResultSet resultSet = statement
    // .executeQuery("select CRYO_PROCESS_NAME, CRYO_PROCESS_ID, CRYO_PROCESS_EXPLANATION "
    // + "from NSB_CRYO_PROCESS where NSB_CRYO_PROCESS.CRYO_PROCESS_ID = "
    // + id);
    //
    // try {
    // if (resultSet.next()) {
    // return new KryoProcessEntry(resultSet.getString(1), resultSet
    // .getString(2), resultSet.getString(3));
    // } else {
    // throw new RuntimeException("Missing process for id " + id);
    // }
    // } finally {
    // statement.close();
    // }
    // }
    
    private KryoObjectEntry getObjectEntry(int id, HashMap<Integer, KryoObjectEntry> objectCache) throws SQLException {
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
                KryoObjectEntry entry = new KryoObjectEntry(resultSet.getString(1), resultSet
                        .getString(2), resultSet.getInt(3), resultSet.getInt(4), resultSet
                        .getString(5), resultSet.getInt(6));
                objectCache.put(id, entry);
                return entry;
            } else {
                throw new RuntimeException("Missing object for id " + id);
            }
        } finally {
            statement.close();
        }
    }
    
    private KryoPlantEntry getPlantEntry(int id, HashMap<Integer, KryoPlantEntry> plantCache) throws SQLException {
        if (id == NO_PARENT_PLANT_ID) {
            return null;
        }
        
        if (plantCache.containsKey(id)) {
            return plantCache.get(id);
        }
        
        Statement statement = database.getConnection().createStatement();
        ResultSet resultSet = statement
                .executeQuery("select PLANT_NAME, PLANT_LABEL, PLANT_EXPLANATION, PLANT_ID,"
                        + " PLANT_PARENT, PLANT_NO from NSB_PLANT where NSB_PLANT.PLANT_ID = " + id);
        
        try {
            if (resultSet.next()) {
                KryoPlantEntry kryoPlantEntry = new KryoPlantEntry(resultSet.getString(1),
                                                                   resultSet.getString(2),
                                                                   resultSet.getString(3),
                                                                   resultSet.getInt(4),
                                                                   resultSet.getInt(5),
                                                                   resultSet.getInt(6));
                plantCache.put(id, kryoPlantEntry);
                return kryoPlantEntry;
            } else {
                throw new RuntimeException("Missing plant for id" + id);
            }
        } finally {
            statement.close();
        }
    }
    
    public boolean isValid(KryoNameEntry newEntry) throws SQLException {
        // validation is not very nice looking but hey it is the best I can do
        // Rudimentary check
        if (newEntry.getName() == null || (newEntry.getName().length() == 0)
                || newEntry.getObjectId() == 0 || newEntry.getPlantId() == 0
                || !newEntry.getName().startsWith("X")) {
            
            return false;
        }
        
        // validate last 4 digits in the name
        String name = newEntry.getName();
        // String processId = name.substring(name.length() - 4, name.length() - 2);
        String seqNum = name.substring(name.length() - 4, name.length());
        
        // precess id is now comined with seq number
        // if (!newEntry.getProcessId().equals(processId)
        // || processId.length() != 2) {
        // return false;
        // }
        
        if (seqNum.length() != 4) {
            return false;
        }
        
        try {
            int parseInt = Integer.parseInt(seqNum);
            if (parseInt != newEntry.getSeqKryoNumber()) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        
        // by this point the process and seq number are valid
        
        // validate objects
        
        int colon = name.indexOf(":");
        
        if (colon < 0) {
            return false;
        }
        
        // must be lowest level object
        
        if (!isLowestLevelObject(newEntry.getObjectId())) {
            return false;
        }
        
        String objectPart = name.substring(name.indexOf(":") + 1, name.length() - 4);
        
        HashMap<Integer, KryoObjectEntry> objectCache = new HashMap<Integer, KryoObjectEntry>();
        KryoObjectEntry entry = getObjectEntry(newEntry.getObjectId(), objectCache);
        
        if (entry == null) {
            return false;
        }
        
        while (entry != null) {
            if (!objectPart.endsWith(entry.getLabel())) {
                return false;
            }
            objectPart = objectPart.substring(0, objectPart.length() - entry.getLabel().length());
            entry = getObjectEntry(entry.getParent(), objectCache);
            
        }
        
        // validate plants
        String plantPart = name.substring(0, name.indexOf(":"));
        
        HashMap<Integer, KryoPlantEntry> plantCache = new HashMap<Integer, KryoPlantEntry>();
        KryoPlantEntry plantEntry = getPlantEntry(newEntry.getPlantId(), plantCache);
        
        if (plantEntry == null) {
            return false;
        }
        
        String plantStringPart = plantPart.replaceAll("\\d", "");
        while (plantEntry != null) {
            if (!plantStringPart.endsWith(plantEntry.getLabel())) {
                return false;
            }
            
            //Remove number check, because it should be possible to add parts also without a number.
//            if (plantEntry.getNumberOfPlants() > 0) {
//                int index = plantPart.lastIndexOf(plantEntry.getLabel())
//                        + plantEntry.getLabel().length();
//                if (index >= plantPart.length() || !Character.isDigit(plantPart.charAt(index))) {
//                	if (index >= plantPart.length()) {
//                    return false;
//                }
//                
//            }
            
            plantStringPart = plantStringPart.substring(0, plantStringPart.length()
                    - plantEntry.getLabel().length());
            plantEntry = getPlantEntry(plantEntry.getParent(), plantCache);
            
        }
        
        // should be valid
        return true;
    }
    
    public synchronized void add(KryoNameEntry newEntry) throws SQLException {
        
        if (!isValid(newEntry)) {
            throw new RuntimeException("Validation failed.");
        }
        
        if (doesExist(newEntry.getName())) {
            throw new IllegalArgumentException("Cannot add already existing name");
        }
        
        Statement statement = database.getConnection().createStatement();
        
        try {
            
            statement.executeUpdate("insert into NSB_IO_NAME (IO_NAME, PLANT_ID, OBJECT_ID, "
                    + "SEQ_KRYO_NUMBER_4, KRYO_NAME_LABEL) values ('" + newEntry.getName() + "','"
                    + newEntry.getPlantId() + "','" + newEntry.getObjectId() + "','"
                    + newEntry.getSeqKryoNumber() + "','" + newEntry.getLabel() + "')");
            
        } finally {
            statement.close();
        }
    }
    
    public synchronized void delete(KryoNameEntry kryoNameEntry) throws SQLException {
        
        Statement statement = database.getConnection().createStatement();
        try {
            statement.executeUpdate("delete from NSB_IO_NAME where IO_NAME_ID = '"
                    + kryoNameEntry.getId() + "'");
        } finally {
            statement.close();
        }
    }
    
    /**
     * The entry's name will be used to update the label from the corresponding name entry in the
     * database.
     * 
     * @param kryoNameEntry
     *            entry from which to use the name and new label
     */
    public synchronized void updateLabel(KryoNameEntry kryoNameEntry) throws SQLException {
        
        Statement statement = database.getConnection().createStatement();
        
        try {
            statement.executeUpdate("update NSB_IO_NAME set KRYO_NAME_LABEL = '"
                    + kryoNameEntry.getLabel() + "' where IO_NAME_ID = '" + kryoNameEntry.getId()
                    + "'");
            
        } finally {
            statement.close();
        }
        
    }
    
    private boolean isLowestLevelObject(int id) throws SQLException {
        Statement statement = database.getConnection().createStatement();
        boolean next = false;
        try {
            next = statement
                    .executeQuery("select OBJECT_NAME from NSB_OBJECT where OBJECT_PARENT='" + id
                            + "'").next();
            
        } finally {
            statement.close();
        }
        
        return !next;
        
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
            next = statement.executeQuery("select IO_NAME from NSB_IO_NAME where IO_NAME='" + name
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
    public synchronized List<KryoObjectEntry> findToplevelObjectChoices() throws SQLException {
        return findObjectChoices(new KryoObjectEntry(NO_PARENT_OBJECT_ID));
    }
    
    /**
     * Returns a list of {@link KryoObjectEntry} where their parent is specified.
     * 
     * @param parent
     *            entry
     * @return list of entries corresponding to the parent
     */
    public synchronized List<KryoObjectEntry> findObjectChoices(KryoObjectEntry parent) throws SQLException {
        
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
                entries.add(new KryoObjectEntry(rs.getString(1), rs.getString(2), rs.getInt(3), rs
                        .getInt(4), rs.getString(5), rs.getInt(6)));
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
    public synchronized List<KryoPlantEntry> findToplevelPlantChoices() throws SQLException {
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
    public synchronized List<KryoPlantEntry> findPlantChoices(KryoPlantEntry parent) throws SQLException {
        
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
                                               rs.getString(2),
                                               rs.getString(3),
                                               rs.getInt(4),
                                               rs.getInt(5),
                                               rs.getInt(6)));
            }
        } finally {
            statement.close();
        }
        
        return results;
    }
    
    // precess id is now comined with seq number
    // /**
    // * Returns a list of {@link KryoProcessEntry}.
    // *
    // * @return list of all process entries.
    // * @throws java.sql.SQLException
    // */
    // public synchronized List<KryoProcessEntry> findProcessChoices()
    // throws SQLException {
    //
    // List<KryoProcessEntry> results = new ArrayList<KryoProcessEntry>();
    //
    // Statement statement = database.getConnection().createStatement();
    // statement.setFetchSize(ROW_FETCH_SIZE);
    // try {
    // ResultSet rs = statement
    // .executeQuery("select CRYO_PROCESS_NAME, CRYO_PROCESS_ID, CRYO_PROCESS_EXPLANATION "
    // + "from NSB_CRYO_PROCESS");
    //
    // while (rs.next()) {
    // results.add(new KryoProcessEntry(rs.getString(1), rs
    // .getString(2), rs.getString(3)));
    // }
    //
    // } finally {
    // statement.close();
    // }
    //
    // return results;
    // }
    
    /**
     * Exports the given list to Excel. Make sure you properly close the stream. This method does
     * not close the stream.
     * 
     * @param list
     * @param fileInputStream
     * @throws IOException
     */
    public void excelExport(ArrayList<KryoNameResolved> list, OutputStream outputStream) throws IOException {
        
        short rownum = 0;
        
        // create a new file
        // create a new workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        // create a new sheet
        HSSFSheet s = wb.createSheet();
        // declare a row object reference
        HSSFRow r = null;
        // declare a cell object reference
        HSSFCell c = null;
        // create a cell style
        HSSFCellStyle csCapital = wb.createCellStyle();
        
        HSSFCellStyle csNormal = wb.createCellStyle();
        // create a font object
        HSSFFont fontCapital = wb.createFont();
        HSSFFont fontNormal = wb.createFont();
        
        // set font 1 to 12 point type
        fontCapital.setFontHeightInPoints((short) 10);
        fontCapital.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        
        // make it blue
        // f.setColor( (short)0xc );
        // make it bold
        // arial is the default font
        // f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        
        csCapital.setFont(fontCapital);
        
        // set the sheet name
        wb.setSheetName(0, "KryoNames", HSSFWorkbook.ENCODING_COMPRESSED_UNICODE);
        
        // setCaption(s, r, c);
        // create a row
        r = s.createRow(rownum);
        
        // create cells
        c = r.createCell((short) 0);
        // set this cell to the first cell style we defined
        c.setCellStyle(csCapital);
        // set the cell's string value to "Test"
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Kryo Name");
        s.setColumnWidth((short) 0, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 1);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Plant");
        s.setColumnWidth((short) 1, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 2);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("No");
        s.setColumnWidth((short) 2, NOM_CELL_WIDTH);
        
        c = r.createCell((short) 3);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Sub Plant 1");
        s.setColumnWidth((short) 3, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 4);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("No");
        s.setColumnWidth((short) 4, NOM_CELL_WIDTH);
        
        c = r.createCell((short) 5);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Sub Plant 2");
        s.setColumnWidth((short) 5, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 6);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("No");
        s.setColumnWidth((short) 6, NOM_CELL_WIDTH);
        
        c = r.createCell((short) 7);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Sub Plant 3");
        s.setColumnWidth((short) 7, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 8);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("No");
        s.setColumnWidth((short) 8, NOM_CELL_WIDTH);
        
        c = r.createCell((short) 9);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Object");
        s.setColumnWidth((short) 9, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 10);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Object Function");
        s.setColumnWidth((short) 10, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 11);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Object Subfunction");
        s.setColumnWidth((short) 11, NAME_CELL_WIDTH);
        
        // c = r.createCell((short) 12);
        // c.setCellStyle(csCapital);
        // c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        // c.setCellValue("Process Part");
        // s.setColumnWidth((short) 12, NAME_CELL_WIDTH);
        
        c = r.createCell((short) 12);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Seq No");
        s.setColumnWidth((short) 12, (short) 1700);
        
        c = r.createCell((short) 13);
        c.setCellStyle(csCapital);
        c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
        c.setCellValue("Description");
        s.setColumnWidth((short) 13, (short) 3000);
        
        fontNormal.setFontHeightInPoints((short) 10);
        
        csNormal.setFont(fontNormal);
        
        // create a sheet with rows
        // for (rownum = (short) 1; rownum < sd.getKryoNameList().size(); rownum++)
        for (KryoNameResolved resolved : list) {
            
            rownum++;
            // create a row
            r = s.createRow(rownum);
            // create cells
            c = r.createCell((short) 0);
            c.setCellValue(resolved.getName());
            
            // c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
            
            List<KryoPlantResolved> plants = resolved.getPlants();
            
            int i = 1;
            for (KryoPlantEntry kryoPlantEntry : plants) {
                c = r.createCell((short) i++);
                c.setCellValue(kryoPlantEntry.getName());
                c = r.createCell((short) i++);
                int numberOfPlants = kryoPlantEntry.getNumberOfPlants();
                
                c.setCellValue(numberOfPlants < 0 ? "" : "" + numberOfPlants);
                
            }
            
            i = 9;
            
            List<KryoObjectEntry> objects = resolved.getObjects();
            
            for (KryoObjectEntry kryoObjectEntry : objects) {
                c = r.createCell((short) i++);
                c.setCellValue(kryoObjectEntry.getName());
                
            }
            // process id is combined with seq number
            // c = r.createCell((short) 12);
            // c.setCellValue(resolved.getProcess().getName());
            
            c = r.createCell((short) 12);
            c.setCellValue(resolved.getSeqKryoNumber());
            
            c = r.createCell((short) 13);
            String label = resolved.getLabel();
            c.setCellValue(label != null ? label.substring(0, Math.min(DESCRIPTION_LENGTH, label
                    .length())) : "");
        }
        
        // write the workbook to the output stream
        // close our file (don't blow out our file handles
        wb.write(outputStream);
        
    }
    
}