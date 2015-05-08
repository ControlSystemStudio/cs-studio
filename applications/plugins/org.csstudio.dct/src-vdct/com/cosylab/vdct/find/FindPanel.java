package com.cosylab.vdct.find;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.ViewState;
import com.cosylab.vdct.graphics.objects.Group;
import com.cosylab.vdct.graphics.objects.Record;
import com.cosylab.vdct.graphics.objects.VisibleObject;

/**
 * A threaded search accessory.
 * <P>
 * Presents tabbed panel interface for specifying file
 * search criteria including search by name and  search by type
 * Search by field content and be implemented also. Finded are performed "in the
 * background" with found files displayed dynamically as they are found. Only
 * one search can be active at a time. FindResults are displayed in a scrolling
 * list within a results tab panel.
 * <P>
 * Findes are performed asynchronously. The user may stop the search at any time.
 * Accepting or closing find window will automatically
 * stop a search in progress.
 * <P>
 * Changing the search options does not affect a search in progress.
 */
public class FindPanel extends JPanel
    implements Runnable, FindProgressCallback {

    /**
     * Label for this accessory.
     */
    static public final String ACCESSORY_NAME = " Find ";

    /**
     * Default max number of found items. Prevents overloading results list.
     */
    static public final int DEFAULT_MAX_SEARCH_HITS = 500;

    /**
     * Find start action name
     */
    static public final String ACTION_START = "Start";

    /**
     * Find stop action name
     */
    static public final String ACTION_STOP = "Stop";

    protected FindAction actionStart = null;

    protected FindAction actionStop = null;

    /**
     * This version of FindAccesory supports only one active search thread
     */
    protected Thread searchThread = null;

    /**
     * Set to true to stop current search
     */
    protected boolean killFind = false;

    /**
     * Displays full path of search base
     */
    protected FindTitle pathPanel = null;

    /**
     * Find options with results list
     */
    protected FindTabs searchTabs = null;

    /**
     * Find controls with progress display
     */
    protected FindControls controlPanel = null;

    /**
     * Number of items inspected by current/last search
     */
    protected int total = 0;

    /**
     * Number of items found by current/last search
     */
    protected int matches = 0;

    /**
     * Max number of found items to prevent overloading the results list.
     */
    protected int maxMatches = DEFAULT_MAX_SEARCH_HITS;

    /**
     * Construct a search panel with start and stop actions, option panes and a
     * results list pane that can display up to DEFAULT_MAX_SEARCH_HITS items.
     */
    public FindPanel() {
        super();

        //setBorder(new TitledBorder(ACCESSORY_NAME));
        setLayout(new BorderLayout());

        actionStart = new FindAction(ACTION_START, null);
        actionStop = new FindAction(ACTION_STOP, null);

        add(pathPanel = new FindTitle(), BorderLayout.NORTH);
        add(searchTabs = new FindTabs(), BorderLayout.CENTER);
        add(controlPanel = new FindControls(actionStart, actionStop, true), BorderLayout.SOUTH);

    }

    /**
     * Sets maximum capacity of the results list. Find stops when max number of
     * items found.
     *
     * @param max
     *            Max capacity of results list.
     */
    public void setMaxFindHits(int max)
    {
        maxMatches = max;
    }

    /**
     * Returns maximum capacity of results list.
     *
     * @return Max capacity of results list.
     */
    public int getMaxFindHits()
    {
        return maxMatches;
    }

    /**
     * Start a search. The path display will show the starting folder of the
     * search. Finds are recursive and will span the entire folder hierarchy
     * below the base folder. The user may continue to browse with JFileChooser.
     */
    public synchronized void start()
    {
        if (searchTabs != null)
            searchTabs.showFindResults();
        killFind = false;
        if (searchThread == null)
        {
            searchThread = new Thread(this);
        }
        if (searchThread != null)
            searchThread.start();
    }

    /**
     * Stop the active search.
     */
    public synchronized void stop()
    {
        killFind = true;
    }

    /**
     * @return true if a search is currently running
     */
    public boolean isRunning()
    {
        if (searchThread == null)
            return false;
        return searchThread.isAlive();
    }

    /**
     * Find thread
     */
    public void run()
    {
        if (searchThread == null)
            return;
        if (Thread.currentThread() != searchThread)
            return;
        try
        {
            actionStart.setEnabled(false);
            actionStop.setEnabled(true);
            runFind(DrawingSurface.getInstance().getViewGroup(), newFind());
        } catch (InterruptedException e)
        {
        } finally
        {
            actionStart.setEnabled(true);
            actionStop.setEnabled(false);
            searchThread = null;
        }
    }

    /**
     * Show selected (in result panel) object.
     * @param selectedObject    object to show
     */
    public void goTo(Object selectedObject)
    {
        if (selectedObject instanceof VisibleObject)
            DrawingSurface.getInstance().centerObject((VisibleObject)selectedObject);
    }

    /**
     * Recursive search beginning for objects
     * matching each filter in the <b>filters </b> array. To interrupt set
     * <b>killFind </b> to true. Also stops when number of search hits (matches)
     * equals <b>maxMatches </b>.
     *
     * @param root    base group where to start search
     * @param filters
     *            matches must pass each filters in array
     * @exception InterruptedException
     *                if thread is interrupted
     */
    // TODO only recursive record search implemented
    protected void runFind(Group base, FindFilter[] filters)
            throws InterruptedException
    {
        if (base == null || filters == null || killFind)
            return;

        Enumeration e = base.getSubObjectsV().elements();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            if (obj instanceof Record)
            {
                total++;

                if (accept(obj, filters))
                {
                    matches++;
                    searchTabs.addMatch(obj);
                }

                updateProgress();
                if (killFind)
                    return;
                Thread.yield();
            }


            // recursive
            if (obj instanceof Group) runFind((Group)obj, filters);

            if ((maxMatches > 0) && (matches >= maxMatches))
                return; // stopgap measure so that we don't overload

        }
    }

    /**
     * Match check.
     * @param candidate
     *            candidate to pass to each filter's accept method
     * @param filters
     *            array of selection criteria
     *
     * @return true if specified candidate matches each filter's selection criteria
     */
    protected boolean accept(Object candidate, FindFilter[] filters)
    {
        if (candidate == null || filters == null)
            return false;

        for (int i = 0; i < filters.length; i++)
        {
            if (!filters[i].accept(candidate, this))
                return false;
        }
        return true;
    }

    /**
     * Called by FindFilter to report progress of a search. Purely a voluntary
     * report. This really should be implemented as a property change listener.
     * Percentage completion = (current/total)*100.
     *
     * @param filter
     *            FindFilter reporting progress
     * @param searchee
     *            object being searched
     * @param current
     *            current "location" of search
     * @param total
     *            expected maximum value of current
     *
     * @return true to continue search, false to abort
     */
    public boolean reportProgress(FindFilter filter, Object searchee, long current,
            long total)
    {
        return !killFind;
    }

    /**
     * Begins a new search by resetting the <b>total </b> and <b>matches </b>
     * progress variables and retrieves the search filter array from the options
     * panel. Each tab in the options panel is responsible for generating a
     * FindFilter based on its current settings.
     *
     * @return Array of search filters from the options panel.
     */
    protected FindFilter[] newFind()
    {

        total = matches = 0;
        updateProgress();

        if (searchTabs != null)
            return searchTabs.newFind();
        return null;
    }

    /**
     * Display progress of running search.
     */
    protected void updateProgress()
    {
        controlPanel.showProgress(matches, total);
    }

    /**
     * Stop the current search and unregister in preparation for parent
     * shutdown.
     */
    public void quit()
    {
        stop();
    }

    /**
     * Invoked by FindAction objects to start and stop searches.
     */
    public void action(String command)
    {
        if (command == null)
            return;
        if (command.equals(ACTION_START))
            start();
        else if (command.equals(ACTION_STOP))
            stop();
    }

    /**
     * Convenience class for adding action objects to the control panel.
     */
    class FindAction extends AbstractAction
    {
        /**
         * Construct a search control action currently implements
         * <code>FindAccesory.ACTION_START</code> and <code>FindPanel.ACTION_STOP</code>.
         *
         * @param text
         *            command
         * @param icon
         *            button icon
         */
        FindAction(String text, Icon icon) {
            super(text, icon);
        }

        /**
         * Invoke FindAction's action() method.
         *
         * @param e
         *            action event
         */
        public void actionPerformed(ActionEvent e)
        {
            action(e.getActionCommand());
        }
    }

    /**
     * Panel displaying title.
     */
    class FindTitle extends JPanel {
        protected JLabel title = null;

        FindTitle() {
            super();
            setLayout(new BorderLayout());

            // Directory
            title = new JLabel();
            title.setForeground(Color.black);
            //title.setFont(new Font("Helvetica", Font.PLAIN, 12));
            add(title);
        }

        /**
         * Set title.
         */
        public void setTitle(String text)
        {
            if (text == null)
                return;
            title.setText(text);
        }

    }

    /**
     * Find controls panel displays default action components for starting and
     * stopping a search. Also displays the search progress in the form of a
     * text display indicating the number of items found and the total number of
     * items encountered in the search.
     */
    class FindControls extends JPanel {

        protected JLabel progress = null;

        /**
         * Construct a simple search control panel with buttons for starting and
         * stopping a search and a simple display for search progress.
         */
        FindControls(FindAction find, FindAction stop, boolean recurse) {
            super();
            setLayout(new BorderLayout());

            JToolBar tools = new JToolBar();
            tools.setFloatable(false);
            tools.add(actionStart = new FindAction(ACTION_START, null));
            tools.add(actionStop = new FindAction(ACTION_STOP, null));
            add(tools, BorderLayout.WEST);

            progress = new JLabel("", SwingConstants.RIGHT);

            // So that frequent updates will appear smooth
            progress.setDoubleBuffered(true);

            progress.setForeground(Color.black);
            progress.setFont(new Font("Helvetica", Font.PLAIN, 12));
            add(progress, BorderLayout.EAST);
        }

        /**
         * Display search progress as a text field "no. of matches / total
         * searched".
         *
         * @param matches
         *            number of items found
         * @param total
         *            number of items investigated
         */
        public void showProgress(int matches, int total)
        {
            if (progress == null)
                return;
            progress.setText(String.valueOf(matches) + "/"
                    + String.valueOf(total));
        }

    }

    /**
     * Contains a collecton of search options displayed as tabbed panes and at
     * least one pane for displaying the search results. Each options tab pane
     * is a user interface for sprecifying the search criteria and a factory for
     * a FindFilter to implement the acceptance function. By making the search
     * option pane responsible for generating a FindFilter object, the
     * programmer can easily extend the search capabilities without modifying
     * the controlling search engine.
     */
    class FindTabs extends JTabbedPane {
        protected String NAME = "Name";

        protected String TYPE = "Type";

        protected String TAB_CRITERIA = "Criteria";
        protected String TAB_RESULTS = "Results";

        protected FindResults resultsPanel = null;

        protected JScrollPane resultsScroller = null;

        private JPanel criteriaPanel;
        private ArrayList components = new ArrayList();
        /**
         * Construct a search tabbed pane with tab panels for seach by filename,
         * search by date, search by content and search results.
         */
        FindTabs() {
            super();

            setForeground(Color.black);
            //setFont(new Font("Helvetica", Font.BOLD, 10));

//            // Add search-by-name panel
//            addTab(NAME, new FindByName(FindPanel.this));
//
//            // Add search-by-type panel
//            addTab(TYPE, new FindByType(FindPanel.this));

            //all criteria on one tab
            addTab(TAB_CRITERIA, getCriteriaPanel());
//             TODO add panels here...

            // Add results panel
            resultsScroller = new JScrollPane(resultsPanel = new FindResults());

            // so that updates will be smooth
            resultsPanel.setDoubleBuffered(true);
            resultsScroller.setDoubleBuffered(true);

            addTab(TAB_RESULTS, resultsScroller);
        }

        private JPanel getCriteriaPanel() {
            criteriaPanel = new JPanel(new GridBagLayout());

            criteriaPanel.add(new JLabel(NAME),new GridBagConstraints(0,0,1,1,1,0.2,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1));

            FindByName namePanel = new FindByName(FindPanel.this);
            components.add(namePanel);
            criteriaPanel.add(namePanel,
                    new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1));

            criteriaPanel.add(new JLabel(TYPE),new GridBagConstraints(0,2,1,1,1,0.2,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1));

            FindByType typePanel = new FindByType(FindPanel.this);
            components.add(typePanel);
            criteriaPanel.add(typePanel,
                    new GridBagConstraints(0,3,1,1,1,1, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0,0,0,0), 1,1));

            return criteriaPanel;
        }
        /**
         * Adds the specified file to the results list.
         *
         * @param match
         *            object to add to results list
         */
        public void addMatch(Object match)
        {
            if (resultsPanel != null)
                resultsPanel.append(match);
        }

        /**
         * Bring the search results tab panel to the front.
         */
        public void showFindResults()
        {
            if (resultsScroller != null)
                setSelectedComponent(resultsScroller);
        }

        private FindFilterFactory getFilterAt(int index) {
            return (FindFilterFactory) components.get(index);
        }

        /**
         * Prepares the panel for a new search by clearing the results list,
         * bringing the results tab panel to the front and generating an array
         * of search filters for each search options pane that implements the
         * FindFilterFactory interface.
         *
         * @return array of FindFilters to be used by the controlling search
         *         engine
         */
        public FindFilter[] newFind()
        {
            // Clear the results display
            if (resultsPanel != null)
                resultsPanel.clear();

            // Fix the width of the scrolling results panel so the layout
            // managers don't try to make it too wide for JFileChooser
            Dimension dim = resultsScroller.getSize();
            resultsScroller.setMaximumSize(dim);
            resultsScroller.setPreferredSize(dim);

            // Return an array of FindFilters
            Vector filters = new Vector();
            for (int i = 0; i < components.size(); i++)
            {
                try
                {
//                    FindFilterFactory fac = (FindFilterFactory) getComponentAt(i);
                    FindFilterFactory fac = getFilterAt(i);
                    FindFilter f = fac.createFindFilter();
                    if (f != null)
                        filters.addElement(f);
                } catch (Throwable e)
                {
                    // The FindResults pane does not implement FindFilterFactory
                }
            }
            if (filters.size() == 0)
                return null;
            FindFilter[] filterArray = new FindFilter[filters.size()];
            for (int i = 0; i < filterArray.length; i++)
            {
                filterArray[i] = (FindFilter) filters.elementAt(i);
            }
            return filterArray;
        }
    }

    /**
     * Appears as a special pane within the FindOptions tabbed panel. The only
     * one that does not generate a FindFilter.
     */
    class FindResults extends JPanel {
        protected DefaultListModel model = null;

        protected JList fileList = null;

        /**
         * Construct a search results pane with a scrollable list of files.
         */
        FindResults() {
            super();
            setLayout(new BorderLayout());

            model = new DefaultListModel();
            fileList = new JList(model);
            fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            fileList.setFont(fileList.getFont().deriveFont(Font.PLAIN));
            add(fileList, BorderLayout.CENTER);

            final JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Select selection in workspace");
            item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            Object[] selected = fileList.getSelectedValues();
                            for (int i = 0; i < selected.length; i++)
                                if (selected[i] instanceof VisibleObject)
                                    ViewState.getInstance().setAsSelected((VisibleObject)selected[i]);
                            if (selected.length > 0)
                                DrawingSurface.getInstance().repaint();
                        }
                    });
            menu.add(item);

            // mouse listener
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e)
                {
                    boolean leftButtonPush = (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0;
                    boolean rightButtonPush = (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0;

                    if (e.getClickCount() == 1 && rightButtonPush) {
                        if (fileList.getSelectedValues().length > 0)
                            menu.show(fileList, e.getX(), e.getY());
                    }
                    else if (e.getClickCount() == 2 && leftButtonPush)
                    {
                        try
                        {
                            int index = fileList.locationToIndex(e.getPoint());
                            goTo(model.elementAt(index));
                        } catch (Throwable err)
                        {
                        }
                    }
                }
            };
            fileList.addMouseListener(mouseListener);
        }

        /**
         * Add a match to the results list.
         *
         * @param match
         *            match found
         */
        public void append(Object match)
        {
            if (match == null)
                return;
            model.addElement(match);
        }

        /**
         * Clear all items from the results list.
         */
        public void clear()
        {
            if (model != null)
            {
                model.removeAllElements();
                invalidate();
                repaint();
            }
        }

    }

}

/**
 * Each search option tab that implements FindFilterFactory defines an inner
 * class that implements FindFilter. When a search is started the search panel
 * invokes createFindFilter() on each panel that implements FindFilterFactory,
 * thus causing the panel to create a FindFilter object that implements its
 * search settings.
 */

interface FindFilter {
    public boolean accept(Object candidate, FindProgressCallback monitor);
}

interface FindProgressCallback {
    /**
     * Should be called by all time-consuming search filters at a reasonable
     * interval. Allows the search controller to report progress and to abort
     * the search in a clean and timely way.
     *
     * @param filter
     *            FindFilter reporting the progress
     * @param searchee
     *            the object being searched
     * @param current
     *            current "location" of search
     * @param total
     *            maximum value
     * @return true if search should continue, false to abort
     */
    public boolean reportProgress(FindFilter filter, Object searchee, long current,
            long total);
}

/**
 * Implemented by each search option panel. Each panel is responsible for
 * creating a FindFilter object that implements the search criteria specified by
 * its user interface.
 */

interface FindFilterFactory {
    public FindFilter createFindFilter();
}

/**
 * Implements user interface and generates FindFilter for selecting files by
 * name.
 */

class FindByName extends JPanel implements FindFilterFactory {
    protected String NAME_CONTAINS = "contains";

    protected String NAME_IS = "is";

    protected String NAME_STARTS_WITH = "starts with";

    protected String NAME_ENDS_WITH = "ends with";

    protected int NAME_CONTAINS_INDEX = 0;

    protected int NAME_IS_INDEX = 1;

    protected int NAME_STARTS_WITH_INDEX = 2;

    protected int NAME_ENDS_WITH_INDEX = 3;

    protected String[] criteria = { NAME_CONTAINS, NAME_IS, NAME_STARTS_WITH,
            NAME_ENDS_WITH };

    protected JTextField nameField = null;

    protected JComboBox combo = null;

    protected JCheckBox ignoreCaseCheck = null;

    private FindPanel parent;
    FindByName(FindPanel pan) {
        super();
        setLayout(new BorderLayout());
        this.parent = pan;

        // Grid Layout
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0, 2, 2, 2));

        // Name
        combo = new JComboBox(criteria);
        //combo.setFont(new Font("Helvetica", Font.PLAIN, 10));
        combo.setPreferredSize(combo.getPreferredSize());
        p.add(combo);

        nameField = new JTextField(12);
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               parent.start();
            }
        });
        //nameField.setFont(new Font("Helvetica", Font.PLAIN, 10));
        p.add(nameField);

        // ignore case
        p.add(new JLabel("", SwingConstants.RIGHT));

        ignoreCaseCheck = new JCheckBox("ignore case", true);
        ignoreCaseCheck.setForeground(Color.black);
        //ignoreCaseCheck.setFont(new Font("Helvetica", Font.PLAIN, 10));
        p.add(ignoreCaseCheck);

        add(p, BorderLayout.NORTH);
    }

    public FindFilter createFindFilter()
    {
        return new NameFilter(nameField.getText(), combo.getSelectedIndex(),
                ignoreCaseCheck.isSelected());
    }

    /**
     * Filter object for selecting files by name.
     */
    class NameFilter implements FindFilter {
        protected String match = null;

        protected int howToMatch = -1;

        protected boolean ignoreCase = true;

        NameFilter(String name, int how, boolean ignore) {
            match = name;
            howToMatch = how;
            ignoreCase = ignore;
        }

        public boolean accept(Object candidate, FindProgressCallback callback)
        {
            if (!(candidate instanceof Record))
                return false;

            if ((match == null) || (match.length() == 0))
                return true;
            if (howToMatch < 0)
                return true;

            String name = ((Record)candidate).getName();

            if (howToMatch == NAME_CONTAINS_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.toLowerCase().indexOf(match.toLowerCase()) >= 0)
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.indexOf(match) >= 0)
                        return true;
                    else
                        return false;
                }
            } else if (howToMatch == NAME_IS_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.equalsIgnoreCase(match))
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.equals(match))
                        return true;
                    else
                        return false;
                }
            } else if (howToMatch == NAME_STARTS_WITH_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.toLowerCase().startsWith(match.toLowerCase()))
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.startsWith(match))
                        return true;
                    else
                        return false;
                }
            } else if (howToMatch == NAME_ENDS_WITH_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.toLowerCase().endsWith(match.toLowerCase()))
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.endsWith(match))
                        return true;
                    else
                        return false;
                }
            }

            return true;
        }
    }

}

/**
 * Implements user interface and generates FindFilter for selecting files by
 * type.
 */
class FindByType extends JPanel implements FindFilterFactory {
    protected String NAME_CONTAINS = "contains";

    protected String NAME_IS = "is";

    protected String NAME_STARTS_WITH = "starts with";

    protected String NAME_ENDS_WITH = "ends with";

    protected int NAME_CONTAINS_INDEX = 0;

    protected int NAME_IS_INDEX = 1;

    protected int NAME_STARTS_WITH_INDEX = 2;

    protected int NAME_ENDS_WITH_INDEX = 3;

    protected String[] criteria = { NAME_CONTAINS, NAME_IS, NAME_STARTS_WITH,
            NAME_ENDS_WITH };

    protected JTextField nameField = null;

    protected JComboBox combo = null;

    protected JCheckBox ignoreCaseCheck = null;

    private FindPanel parent;

    FindByType(FindPanel pan) {
        super();
        this.parent = pan;
        setLayout(new BorderLayout());

        // Grid Layout
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0, 2, 2, 2));

        // Name
        combo = new JComboBox(criteria);
        //combo.setFont(new Font("Helvetica", Font.PLAIN, 10));
        combo.setPreferredSize(combo.getPreferredSize());
        p.add(combo);

        nameField = new JTextField(12);
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               parent.start();
            }
        });

        //nameField.setFont(new Font("Helvetica", Font.PLAIN, 10));
        p.add(nameField);

        // ignore case
        p.add(new JLabel("", SwingConstants.RIGHT));

        ignoreCaseCheck = new JCheckBox("ignore case", true);
        ignoreCaseCheck.setForeground(Color.black);
        //ignoreCaseCheck.setFont(new Font("Helvetica", Font.PLAIN, 10));
        p.add(ignoreCaseCheck);

        add(p, BorderLayout.NORTH);
    }

    public FindFilter createFindFilter()
    {
        return new TypeFilter(nameField.getText(), combo.getSelectedIndex(),
                ignoreCaseCheck.isSelected());
    }



    /**
     * Filter object for selecting files by name.
     */
    class TypeFilter implements FindFilter {
        protected String match = null;

        protected int howToMatch = -1;

        protected boolean ignoreCase = true;

        TypeFilter(String name, int how, boolean ignore) {
            match = name;
            howToMatch = how;
            ignoreCase = ignore;
        }

        public boolean accept(Object candidate, FindProgressCallback callback)
        {
            if (!(candidate instanceof Record))
                return false;

            if ((match == null) || (match.length() == 0))
                return true;
            if (howToMatch < 0)
                return true;

            String name = ((Record)candidate).getType();

            if (howToMatch == NAME_CONTAINS_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.toLowerCase().indexOf(match.toLowerCase()) >= 0)
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.indexOf(match) >= 0)
                        return true;
                    else
                        return false;
                }
            } else if (howToMatch == NAME_IS_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.equalsIgnoreCase(match))
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.equals(match))
                        return true;
                    else
                        return false;
                }
            } else if (howToMatch == NAME_STARTS_WITH_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.toLowerCase().startsWith(match.toLowerCase()))
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.startsWith(match))
                        return true;
                    else
                        return false;
                }
            } else if (howToMatch == NAME_ENDS_WITH_INDEX)
            {
                if (ignoreCase)
                {
                    if (name.toLowerCase().endsWith(match.toLowerCase()))
                        return true;
                    else
                        return false;
                } else
                {
                    if (name.endsWith(match))
                        return true;
                    else
                        return false;
                }
            }

            return true;
        }
    }

}

