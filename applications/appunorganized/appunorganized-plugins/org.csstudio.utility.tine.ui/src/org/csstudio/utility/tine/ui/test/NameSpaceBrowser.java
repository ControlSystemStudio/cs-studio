/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.utility.tine.ui.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.Collator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class NameSpaceBrowser extends JDialog //implements ActionFrame
{
    //
    public final static int DEFAULT_HEIGHT = 360;
    public final static int START_DRAW = 20;
    public final static int STOP_DRAW = START_DRAW;
    public final static int COL_WIDTH = 150;
    public final static int COL_HEIGHT = 200;
    public final static int COL_SPACE = 5;
    public final static int COL_Y = 20;

    public final static String DEFAULT_SEPARATOR = "/";
    public final static String INVALID_STRING = "No data available";
    public final static String NA = "N/A";

//    private int returnValue = CANCEL_OPTION;
    private int returnValue = 0;

    Object calledFrom = null;
    JList lists[];
    JTextField selectedName;
    JButton okB, cancelB;
    ListSelection ls;
    JPanel mainPanel;

    public NameSpaceBrowser (Component c, String title, String cols[]) {
        super(JOptionPane.getFrameForComponent(c), title+" Browser", true);
        setLocation(200,200);
        setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(BorderLayout.CENTER, mainPanel);

        String[] bnames = {"OK", "Cancel"};
//        ActionButtons buttonsPanel = new ActionButtons(bnames, this);
//        cp.add(BorderLayout.SOUTH,buttonsPanel);
//        okB = buttonsPanel.getButton("OK");
//        cancelB = buttonsPanel.getButton("Cancel");

        mainPanel.setLayout(null);
        //setTitle(title+" Browser");
        int colNum = cols.length;
        setSize(START_DRAW + STOP_DRAW + COL_WIDTH * colNum +
                COL_SPACE * (colNum - 1), DEFAULT_HEIGHT);


        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titled;

        lists = new JList[colNum];
        JScrollPane scrolls[] = new JScrollPane[colNum];
        JPanel panels[] = new JPanel[colNum];
        ls = new ListSelection();
        ListKeyListener kl = new ListKeyListener();
        for(int i = 0; i < colNum; i++) {
            lists[i] = new JList();
            lists[i].addListSelectionListener(ls);
            //lists[i].addKeyListener(kl);
            scrolls[i] = new JScrollPane(lists[i]);
            panels[i] = new JPanel();
            panels[i].setLayout (new BorderLayout());
            mainPanel.add(panels[i]);
            panels[i].setBounds(i * (COL_WIDTH + COL_SPACE) + START_DRAW, COL_Y,
                                 COL_WIDTH, COL_HEIGHT);
            titled = BorderFactory.createTitledBorder(etched, cols[i]+":");
            panels[i].add(scrolls[i]);
            panels[i].setBorder(titled);
        }
        JPanel selectedPanel = new JPanel();
        selectedPanel.setLayout(new BorderLayout());
        selectedPanel.setBounds(START_DRAW, COL_Y + COL_HEIGHT + 10,
                               colNum * COL_WIDTH + (colNum - 1) * COL_SPACE, 50);
        selectedName = new JTextField();
        selectedName.setEditable(false);
        selectedName.setBackground(Color.white);
        titled = BorderFactory.createTitledBorder(etched, "Selected Device:");
        selectedPanel.setBorder(titled);
        selectedPanel.add(selectedName);
        mainPanel.add(selectedPanel);


        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getWidth())/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight())/2;
        setLocation(x, y);
    }
    public void buttonActs(JButton b) {
        if (b.equals(okB))    apply();
        else if (b.equals(cancelB)) closeBrowser();
    }
    public JList[] getLists() {
        return lists;
    }
    public ListSelection getListSelectionListener() {
        return ls;
    }
    public String getSelectedItemAt (int i) {
        if(i > (lists.length - 1)) return "";
        return (String)getLists()[i].getSelectedValue();
    }
    public String getSelectedItemInList (JList list) {
        return (String)list.getSelectedValue();
    }
    public int getSelectedIndexInList (JList list) {
        return list.getSelectedIndex();
    }
    protected void closeBrowser() {
        returnValue = 1;
//        returnValue = CANCEL_OPTION;
        this.setVisible(false);
        this.dispose();
    }

    protected void showDateChooser() {
//        DateChooser dc = new DateChooser(this, null);
    }
    protected void apply() {
//        //if(!selectedName.getText().equals(INVALID_STRING))
//        //    ((JTextField)this.calledFrom).setText(selectedName.getText());
//        returnValue = ActionFrame.APPROVE_OPTION;
//        this.setVisible( false);
//        //
//        // keep the browser for next time
//        //this.dispose();
    }

    protected int getListNumber (JList list) {
        for(int i = 0; i < lists.length; i++)
            if(list.equals(lists[i]))
                return i;
        return -1;
    }
    protected int getListsAmount () {
        return lists.length;
    }

    public String getSelectedName() {
        if(!selectedName.getText().equals(INVALID_STRING)) return selectedName.getText();
        else return "";
    }
    public static Vector parseName(String name) {
        Vector v = new Vector();
        StringTokenizer st = new StringTokenizer(name, getNameSeparator());
        while (st.hasMoreTokens())  v.add(st.nextToken());
        return v;
    }

    //static int st = 0;
    public String createName() {
        //System.out.println((st++)+"----------------------------");
        String s = "";
        String tmp;
        for(int k = 0; k < lists.length; k++) {
            tmp = (String)lists[k].getSelectedValue();
            if(tmp == null || tmp.equals(NA)) {
                s = INVALID_STRING;
                return s;
            }
            s += tmp + getNameSeparator();
        }
        s = s.substring(0,s.length()-1); //last separator should be deleted
        return s;
    }
    public void setList(JList l, String[] s, ListSelectionListener lsl) {
        if(s == null) {
            s = new String[1];
            s[0] = NA;
        }

        if(lsl != null) l.removeListSelectionListener(lsl);
        l.setListData(s);
        if(lsl != null) l.addListSelectionListener(lsl);
        l.setSelectedValue(s[0],true);
    }
    public void setList(int i, String[] s) {
        //if(s != null && s[0].equals("")) s = null;
        if(s == null) {
            s = new String[1];
            s[0] = NA;
        }
        getLists()[i].removeListSelectionListener(ls);
        getLists()[i].setListData(sortStrings(s));
        getLists()[i].addListSelectionListener(ls);
        getLists()[i].setSelectedValue(s[0],true);
    }
    public void setList(int i, Vector s) {
        //if(s != null && s[0].equals("")) s = null;
        if(s == null) {
            s = new Vector(1);
            s.addElement(NA);
        }
        getLists()[i].removeListSelectionListener(ls);
        getLists()[i].setListData(sortStrings(s));
        getLists()[i].addListSelectionListener(ls);
        getLists()[i].setSelectedValue((String)s.elementAt(0),true);
    }
    public static String getNameSeparator() {
        return DEFAULT_SEPARATOR;
    }
    public void setSelectedName(String name) {
        selectedName.setText(name);
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }
    public void setColumn(int i) {
        setSelectedName(createName());
        return;
    }
    public int showBrowser() {
        setVisible(true);
        return returnValue;
    }
    private  String[] sortStrings(String[] sin) {
        Collator defaultCollator = Collator.getInstance();
        String tmp;
        for (int i = 0; i < sin.length; i++) {
            for (int j = i + 1; j < sin.length; j++) {
                if (defaultCollator.compare(sin[i], sin[j]) > 0) {
                    tmp = sin[i];
                    sin[i] = sin[j];
                    sin[j] = tmp;
                }
            }
        }
        return sin;
    }
    private  Vector sortStrings(Vector sin) {
        Collator defaultCollator = Collator.getInstance();
        String tmp;
        for (int i = 0; i < sin.size(); i++) {
            for (int j = i + 1; j < sin.size(); j++) {
                if (defaultCollator.compare(sin.elementAt(i), sin.elementAt(j)) > 0) {
                    tmp = (String)sin.elementAt(i);
                    sin.setElementAt((String)sin.elementAt(j), i);
                    sin.setElementAt(tmp, j);
                }
            }
        }
        return sin;
    }

    class ListSelection implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent event) {

            JList list = (JList) event.getSource();
            int listNum = getListNumber(list);
            //list.setEnabled(false);
            //for(int i = 0; i < (lists.length - listNum - 1); i++)
            setColumn(listNum + 1);
                //if((listNum + 1) <= (lists.length - 1)) {
                    //System.out.println("from col:"+listNum+"("+list.getSelectedValue()+") to col:"+(listNum+1));
                //    setSelectedName(createName());
                //}
            //list.setEnabled(true);
        }
    }
    class ListKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent event) {
            selectListItem (event);
        }
    }

    protected void selectListItem(KeyEvent event) {
        JList list = (JList)event.getSource();
        char ch = event.getKeyChar();
        System.out.println("typed:"+ch);
        //list.clearSelection();
        //list.removeListSelectionListener(ls);
        int posorig = list.getSelectedIndex();
        int pos  = posorig;

        for(int i = 0; i < list.getLastVisibleIndex(); i++) {
            list.setSelectedIndex(i);
            if(ch == ((String)list.getSelectedValue()).toLowerCase().charAt(0)) {
                pos = i;
                break;
            }
        }
        list.setSelectedIndex (pos);
        if (pos != posorig)
            setColumn(getListNumber(list) + 1 );
        list.requestFocus();
    }

}