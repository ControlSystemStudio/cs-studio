package org.csstudio.display.pace.model.old;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** PACE Data model
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
public class Model
{
    final private String title;
    private ColumnInfo colInfo;
    private RowInfo rowInfo;
    final private ArrayList<Rows> rows = new ArrayList<Rows>();
    
    // TODO remove
    public Table table;
    public TableViewer table_viewer;
    public Point pt;

    /** Initialize model from XML file stream
     *  @param stream Stream for XML file
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public Model(final InputStream stream) throws Exception
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(stream);
        doc.getDocumentElement().normalize();

        // Check root element
        final String root_name = doc.getDocumentElement().getNodeName();
        if (!root_name.equals("paceconfig"))
            throw new Exception("Got " + root_name + " instead of 'paceconfig'");

        // Get Title
        NodeList titleLst = doc.getElementsByTagName("title");
        Element titleEl = (Element) titleLst.item(0);
        title = titleEl.getFirstChild().getNodeValue();

        NodeList inodeLst = doc.getElementsByTagName("instances");
        Node ifstNode = inodeLst.item(0);
        rowInfo = new RowInfo(ifstNode);
        int numRows = getNumRows();

        NodeList cnodeLst = doc.getElementsByTagName("columns");
        Node cfstNode = cnodeLst.item(0);
        colInfo = new ColumnInfo();
        colInfo.createColumns(cfstNode);
        int numCols = getNumColumns();
        // Convert macros to pv names
        for (int k = 0; k < numRows; k++)
        {
            final Cell cells[] = new Cell[numCols];
            cells[0] = new Cell(getRowName(k));

            for (int i = 1; i < numCols; i++)
            {
                String INPUT = getPvName(i);
                final int numMacros = getNumMacros(k);
                for (int j = 0; j < numMacros; j++)
                { // ${SomeName}
                    final String REGEX = "\\$\\{" + getMacroName(k, j) + "\\}";
                    final String REPLACE = getMacroVal(k, j);
                    Pattern p = Pattern.compile(REGEX);
                    Matcher m = p.matcher(INPUT); // get a matcher object
                    INPUT = m.replaceAll(REPLACE);
                }
                cells[i] = new Cell(colInfo.getColumn(i).getAccess(), INPUT);
            }
            rows.add(new Rows(cells));
        }
    }

    /** @return The title. */
    public String getTitle()
    {
        return title;
    }

    /** @return The number of columns. */
    public int getNumColumns()
    {
        return colInfo.numCols();
    }

    /**
     * @return The pv from the input column number.
     * @param ndx
     *            Column number.
     */
    public String getPvName(int ndx)
    {
        return colInfo.getColumn(ndx).getPvName();
    }

    /** @return if the table has been edited. */
    public boolean isEdited()
    {
        for (int i = 0; i < getNumRows(); i++)
            if (getRow(i).isEdited() == true)
                return true;
        return false;
    }

    /** @return The number of rows in the table. */
    public int getNumRows()
    {
        return rowInfo.numRows();
    }

    /**
     * @return The name of the row for the input row number.
     * @param ndx
     *            Row number.
     */
    public String getRowName(int ndx)
    {
        return rowInfo.getRowName(ndx);
    }

    /**
     * @return The row for the input row number.
     * @param ndx
     *            Row number.
     */
    public Rows getRow(int ndx)
    {
        return rows.get(ndx);
    }

    /**
     * @return The number of macros for the input instance number.
     * @param instNum
     *            Row number.
     */
    int getNumMacros(int instNum)
    {
        return rowInfo.getNumMacros(instNum);
    }

    /**
     * @return The value of the macro for the input instance number and macro
     *         number.
     * @param instNum
     *            Row number.
     * @param macNum
     *            Macro index.
     */
    String getMacroVal(int instNum, int macNum)
    {
        return rowInfo.getMacroVal(instNum, macNum);
    }

    /**
     * @return The instantiated macro string for the input row number.
     * @param instNum
     *            Row number.
     * @param macNum
     *            Macro index.
     */

    String getMacroName(int instNum, int macNum)
    {
        return rowInfo.getMacroName(instNum, macNum);
    }

    /**
     * @return The macro string for the input row number.
     * @param ndx
     *            Macro index.
     */
    public String getMacroStr(int ndx)
    {
        return rowInfo.getMacroStr(ndx);
    }

    /**
     * @return The column name for the input column number.
     * @param ndx
     *            Column number.
     */
    public String getColumnName(int ndx)
    {
        return colInfo.getColumn(ndx).getName();
    }

    /**
     * @return The column for the input column number.
     * @param ndx
     *            Column number.
     */
    public Columns getColumn(int ndx)
    {
        return colInfo.getColumn(ndx);
    }

    /** Open all pv connections */
    public void startAll() throws Exception
    {
        for (Rows row : rows)
            for (int c = 1; c < row.getNumCells(); c++)
                row.getCell(c).start();
    }

    /** Close all pv connections */
    public void stopAll() throws Exception
    {
        for (Rows row : rows)
            for (int c = 1; c < row.getNumCells(); c++)
                row.getCell(c).stop();
    }

    public void setPt(Point p)
    {
        pt = p;
    }

    public void setTable(Table t)
    {
        table = t;
    }

    public Cell getCell(int row, int column)
    {
        return getRow(row).getCell(column);
    }

    public Table getTable()
    {
        return table;
    }

    public Point getPt()
    {
        return pt;
    }
}
