/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */
package com.cosylab.vdct;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <code>PrinterSelector</code> is a dialog window that displays all available PrintServices.
 * It enables selection of a certain PrintService and its distribution to its requestor.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 * 
 * @since VERSION
 */
public class PrinterSelector extends JDialog implements ActionListener {

    private JPanel canvas;
    private JComboBox servicesCombo;
    private JLabel servicesLabel;
    private JButton okButton;
    private JButton cancelButton;
    private static final String OK_COMMAND = "Ok";
    private static final String CANCEL_COMMAND = "Cancel";
    
    private PrintService service = null;
    
    private static PrinterSelector selector = new PrinterSelector();
    
    /**
     * 
     * Return the instance of the PrinterSelector.
     * 
     * @param parent the parent component of this dialog
     * @return selector
     */
    public static PrinterSelector getPrinterSelector(Component parent) {
        selector.setLocationRelativeTo(parent);
        return selector;
    }
    
    private PrinterSelector() {
        this.setTitle("Select printer");
        this.setSize(430,120);
        this.setModal(true);
        this.setResizable(false);
        this.setContentPane(getCanvas());
    }
    
    private JPanel getCanvas() {
        if (canvas == null) {
            canvas = new JPanel();
            canvas.setLayout(new GridBagLayout());
            
            servicesLabel = new JLabel("Available printers: ");
            canvas.add(servicesLabel, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1,1));
            servicesCombo = new JComboBox();
            canvas.add(servicesCombo, new GridBagConstraints(1,0,3,1,0.8,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1,1));
            
            okButton = new JButton("OK");
            canvas.add(okButton, new GridBagConstraints(2,1,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1,1));
            okButton.addActionListener(this);
            okButton.setActionCommand(OK_COMMAND);
            cancelButton = new JButton("Cancel");
            canvas.add(cancelButton, new GridBagConstraints(3,1,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 1,1));
            cancelButton.addActionListener(this);
            cancelButton.setActionCommand(CANCEL_COMMAND);
            
        }
        return canvas;
    }
    
    /**
     * 
     * Shows the PrinterSelector and return the selected PrintService if SAVE button is pressed.
     * If the SKIP button is pressed method returns null.
     * @return selected PrintService.
     */
    public PrintService getPrintService(PrintService previousService) {
        refresh(previousService);
        this.setVisible(true);
        return service;
    }
    
    /*
     *  (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(CANCEL_COMMAND)) {
            service = null;
            this.dispose();
        } else if (command.equals(OK_COMMAND)) {
            service = (PrintService) servicesCombo.getSelectedItem();
            this.dispose();
        }
    }
    
    private void refresh(PrintService previousService) {
        service = null;
        servicesCombo.removeAllItems();
        PrintService[] ps = PrinterJob.lookupPrintServices();
        for (int i = 0; i < ps.length; i++) {
            servicesCombo.addItem(ps[i]);
        }
        if (previousService != null) {
            servicesCombo.setSelectedItem(previousService);
        }
        
    }
}
