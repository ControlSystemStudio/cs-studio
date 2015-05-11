package com.cosylab.vdct;

/**
 * @author ssah
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.cosylab.vdct.plugin.*;
import com.cosylab.vdct.util.*;

public class PluginManagerDialog extends JDialog implements PluginListener,
    PropertyChangeListener
{

class ButtonEventHandler implements ActionListener
{

public void actionPerformed(java.awt.event.ActionEvent event)
{
    if(event.getSource() == installButton)
        installButtonActionPerformed();
    else if(event.getSource() == closeButton)
        dispose();
    else if(event.getSource() == startButton)
        startButtonActionPerformed();
    else if(event.getSource() == stopButton)
        stopButtonActionPerformed();
    else if(event.getSource() == uninstallButton)
        uninstallButtonActionPerformed();
}

private void installButtonActionPerformed()
{
    if(pluginChooser.showOpenDialog(PluginManagerDialog.this) == JFileChooser.CANCEL_OPTION)
        return;

    URL jarURL = null;

    try
    {
        jarURL = pluginChooser.getSelectedFile().toURL();
    }
    catch (Exception e)
    {
        System.err.println("Failed to translate the file to URL.");
        return;
    }

    URL[] listOfURLs = new URL[1];
    listOfURLs[0] = jarURL;

    URLClassLoader pluginClassLoader = new URLClassLoader(listOfURLs, null);

    URL xml = pluginClassLoader.getResource(Constants.PLUGINS_FILE_NAME);

    if(xml == null)
    {
        Console.getInstance().println("Plugins configuration file '" + Constants.PLUGINS_FILE_NAME
            + "' not found.");
        return;
    }

    PluginManager pluginManager = PluginManager.getInstance();

    try
    {
        pluginManager.getPluginSerializer().importPlugins(xml.toString(), pluginManager);
    }
    catch(Exception exception)
    {
        Console.getInstance().println("An error occurred while loading the plugins list!");
        Console.getInstance().println(exception);
    }
}

private void startButtonActionPerformed()
{
    Iterator pluginIterator = PluginManager.getInstance().getPlugins();
    PluginObject plugin = null;

    int rowCounter = 0;

    while(pluginIterator.hasNext())
    {
        plugin = (PluginObject)(pluginIterator.next());

        if(table.isRowSelected(rowCounter))
            plugin.start();

        rowCounter++;
    }
}

private void stopButtonActionPerformed()
{
    Iterator pluginIterator = PluginManager.getInstance().getPlugins();
    PluginObject plugin = null;

    int rowCounter = 0;

    while(pluginIterator.hasNext())
    {
        plugin = (PluginObject)(pluginIterator.next());

        if(table.isRowSelected(rowCounter))
            plugin.stop();

        rowCounter++;
    }
}

private void uninstallButtonActionPerformed()
{
    Iterator pluginIterator = PluginManager.getInstance().getPlugins();
    int numberOfPlugins = 0;

    while(pluginIterator.hasNext())
    {
        pluginIterator.next();
        numberOfPlugins++;
    }

    PluginObject[] plugins = new PluginObject[numberOfPlugins];

    int rowCounter = 0;
    pluginIterator = PluginManager.getInstance().getPlugins();

    while(pluginIterator.hasNext())
    {
        plugins[rowCounter] = (PluginObject)(pluginIterator.next());
        rowCounter++;
    }

    for(rowCounter = 0; rowCounter < numberOfPlugins; rowCounter++)
        if(table.isRowSelected(rowCounter))
            PluginManager.getInstance().removePlugin(plugins[rowCounter]);
}

};

class PluginTableModel extends AbstractTableModel
{

private final static String NOT_LOADED_PLUGIN = "Not loaded";
private final static String LOADED_PLUGIN = "Loaded";
private final static String INVALID_PLUGIN = "Invalid";
private final static String INITIALIZED_PLUGIN = "Initialized";
private final static String STARTED_PLUGIN = "Started";
private final static String STOPPED_PLUGIN = "Stopped";

// shp: unused
//private final static String AUTOSTART_PLUGIN = "Auto";
//private final static String NO_AUTOSTART_PLUGIN = "Manual";

private final String[] columnName = {"Autostart", "Status", "Name", "Version", "Description", "Author"};

public int getRowCount()
{
    Iterator plugin = PluginManager.getInstance().getPlugins();
    int rows = 0;

    while(plugin.hasNext())
    {
        plugin.next();
        rows++;
    }

    return rows;
}

public int getColumnCount()
{
    return columnName.length;
}

public String getColumnName(int col)
{
       return columnName[col];
}

public Object getValueAt(int row, int column)
{
    PluginObject plugin = getPluginAtRow(row);

    switch(column)
    {
        case(0):
            return new Boolean(plugin.isAutoStart());
        case(1):
        {
            int status = plugin.getStatus();
            if(status == PluginObject.PLUGIN_NOT_LOADED)
                return NOT_LOADED_PLUGIN;
            else if(status == PluginObject.PLUGIN_LOADED)
                return LOADED_PLUGIN;
            else if(status == PluginObject.PLUGIN_INVALID)
                return INVALID_PLUGIN;
            else if(status == PluginObject.PLUGIN_INITIALIZED)
                return INITIALIZED_PLUGIN;
            else if(status == PluginObject.PLUGIN_STARTED)
                return STARTED_PLUGIN;
            else if(status == PluginObject.PLUGIN_STOPPED)
                return STOPPED_PLUGIN;
        }
        case(2):
            return plugin.getName();
        case(3):
            return plugin.getVersion();
        case(4):
            return plugin.getDescription();
        case(5):
            return plugin.getAuthor();
    }

    return null;
}

public int getPluginRow(Object plugin)
{
    Iterator pluginIterator = PluginManager.getInstance().getPlugins();

    int rowCounter = -1;
    Object currentPlugin = null;

    while((currentPlugin != plugin) && (pluginIterator.hasNext()))
    {
        currentPlugin = pluginIterator.next();
        rowCounter++;
    }

    if(currentPlugin == plugin)
        return rowCounter;
    else
        return -1;
}

public PluginObject getPluginAtRow(int row)
{
    Iterator pluginIterator = PluginManager.getInstance().getPlugins();

    int rowCounter = 0;

    while((rowCounter < row) && (pluginIterator.hasNext()))
    {
        pluginIterator.next();
        rowCounter++;
    }

    return (PluginObject)(pluginIterator.next());
}

public boolean isCellEditable(int rowIndex, int columnIndex)
{
    if(columnIndex == 0)
        return true;
    else
        return false;
}

public Class getColumnClass(int columnIndex)
{
    if(columnIndex == 0)
        return Boolean.class;
    else
        return String.class;
}

};

class TableEventHandler extends MouseAdapter implements KeyListener
{

public void mouseReleased(MouseEvent event)
{
    validateButtons();

    Point point = event.getPoint();

    int row = table.rowAtPoint(point);

    if(row == -1)
        return;

    int column = table.columnAtPoint(point);

    if(column == 0)
    {
        PluginObject plugin = pluginTableModel.getPluginAtRow(row);
        plugin.setAutoStart(!plugin.isAutoStart());
    }
}

public void keyPressed(KeyEvent event)
{
    validateButtons();
}

public void keyTyped(KeyEvent event)
{
}

public void keyReleased(KeyEvent event)
{
}

};

private class PluginCellRenderer extends DefaultTableCellRenderer
{

public PluginCellRenderer()
{
    setHorizontalAlignment(JLabel.CENTER);
}

public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
    boolean hasFocus, int  row, int column)
{
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    return this;
}

};

private PluginTableModel pluginTableModel = null;

private ButtonEventHandler buttonEventHandler = null;

private JPanel contentPane = null;

private JPanel buttonPanel = null;
private JPanel leftButtonPanel = null;
private JPanel rightButtonPanel = null;

private JScrollPane tablePane = null;

private JTable table = null;

private JButton installButton = null;
private JButton uninstallButton = null;
private JButton startButton = null;
private JButton stopButton = null;
private JButton closeButton = null;

private JFileChooser pluginChooser = null;

private final int[] columnWidths = {64, 64, 192, 54, 320, 192};

public PluginManagerDialog(Frame owner)
{
    super(owner);

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setSize(800, 256);
    setTitle("Plugin Manager");

    table = new JTable();

    table.setShowVerticalLines(true);

    pluginTableModel = new PluginTableModel();

    table.setModel(pluginTableModel);

    TableColumn column = null;

    PluginCellRenderer renderer = new PluginCellRenderer();

    for(int counter = 0; counter < table.getColumnCount(); counter++)
    {
        column = table.getColumnModel().getColumn(counter);
        column.setPreferredWidth(columnWidths[counter]);
        if((counter == 1) || (counter == 3))
            column.setCellRenderer(renderer);
    }

    table.setRowHeight(20);

    TableEventHandler tableHandler = new TableEventHandler();

    table.addMouseListener(tableHandler);
    table.addKeyListener(tableHandler);

    tablePane = new JScrollPane(table);

    buttonEventHandler = new ButtonEventHandler();

    installButton = new JButton();
    installButton.setText("Install...");
    installButton.addActionListener(buttonEventHandler);

    uninstallButton = new JButton();
    uninstallButton.setEnabled(false);
    uninstallButton.setText("Uninstall");
    uninstallButton.addActionListener(buttonEventHandler);

    startButton = new JButton();
    startButton.setEnabled(false);
    startButton.setText("Start");
    startButton.addActionListener(buttonEventHandler);

    stopButton = new JButton();
    stopButton.setEnabled(false);
    stopButton.setText("Stop");
    stopButton.addActionListener(buttonEventHandler);

    closeButton = new JButton();
    closeButton.setText("Close");
    closeButton.addActionListener(buttonEventHandler);

    leftButtonPanel = new JPanel();
    leftButtonPanel.setLayout(new GridLayout(1, 3));
    leftButtonPanel.add(startButton);
    leftButtonPanel.add(stopButton);
    leftButtonPanel.add(uninstallButton);

    rightButtonPanel = new JPanel();
    rightButtonPanel.setLayout(new GridLayout(1, 2));
    rightButtonPanel.add(installButton);
    rightButtonPanel.add(closeButton);

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(1, 2, 64, 0));
    buttonPanel.add(leftButtonPanel);
    buttonPanel.add(rightButtonPanel);

    contentPane = new JPanel();
    contentPane.setLayout(new GridBagLayout());

    GridBagConstraints tablePaneConstraints = new GridBagConstraints();
    tablePaneConstraints.gridx = 0;
    tablePaneConstraints.gridy = 0;
    tablePaneConstraints.weightx = 800;
    tablePaneConstraints.weighty = 224;
    tablePaneConstraints.fill = GridBagConstraints.BOTH;

    GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
    buttonPanelConstraints.gridx = 0;
    buttonPanelConstraints.gridy = 1;
    buttonPanelConstraints.weightx = 800;
    buttonPanelConstraints.weighty = 32;

    contentPane.add(tablePane, tablePaneConstraints);
    contentPane.add(buttonPanel, buttonPanelConstraints);

    setContentPane(contentPane);

    PluginManager.getInstance().addPluginListener(this);

    pluginChooser = new JFileChooser();
    UniversalFileFilter pluginFileFilter =
        new UniversalFileFilter(new String("jar"), "Jar File");

    pluginChooser.resetChoosableFileFilters();
    pluginChooser.addChoosableFileFilter(pluginFileFilter);
    pluginChooser.setDialogTitle("Load Plugin");
    pluginChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
}

private void validateButtons()
{
    boolean oneStartablePlugin = false;
    boolean oneStoppablePlugin = false;
    boolean oneUninstallablePlugin = false;

    Iterator pluginIterator = PluginManager.getInstance().getPlugins();
    PluginObject plugin = null;

    int rowCounter = 0;

    while(pluginIterator.hasNext())
    {
        plugin = (PluginObject)(pluginIterator.next());

        if(table.isRowSelected(rowCounter))
        {
            if((plugin.getStatus() == PluginObject.PLUGIN_INITIALIZED)
                || (plugin.getStatus() == PluginObject.PLUGIN_STOPPED))
            {
                oneStartablePlugin = true;
            }

            if(plugin.getStatus() == PluginObject.PLUGIN_STARTED)
                oneStoppablePlugin = true;

            if(!(plugin.getStatus() == PluginObject.PLUGIN_NOT_LOADED))
                oneUninstallablePlugin = true;
        }

        rowCounter++;
    }

    startButton.setEnabled(oneStartablePlugin);
    stopButton.setEnabled(oneStoppablePlugin);
    uninstallButton.setEnabled(oneUninstallablePlugin);
}

public void pluginAdded(PluginObject plugin)
{
    plugin.addPropertyChangeListener(this);
    table.tableChanged(new TableModelEvent(pluginTableModel));
    validateButtons();
}

public void pluginRemoved(PluginObject plugin)
{
    plugin.removePropertyChangeListener(this);
    table.tableChanged(new TableModelEvent(pluginTableModel));
    validateButtons();
}

public void propertyChange(PropertyChangeEvent event)
{
    int row = pluginTableModel.getPluginRow(event.getSource());
    table.tableChanged(new TableModelEvent(pluginTableModel, row));
    validateButtons();
}

}
