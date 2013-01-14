/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.view.actions;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.siemens.ProfibusConfigWinModGenerator;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: $
 * @since 08.10.2010

 */
public class CreateWinModAction extends Action {

    private static final Logger LOG = LoggerFactory.getLogger(CreateWinModAction.class);

    private final ProfiBusTreeView _pbtv;

    public CreateWinModAction(@Nullable final String text, @Nonnull final ProfiBusTreeView pbtv) {
        super(text);
        _pbtv = pbtv;
    }

    @Override
    public void run() {
        final String filterPathKey = "FilterPath";
        final IEclipsePreferences pref = new DefaultScope().getNode(IOConfigActivator.PLUGIN_ID);
        String filterPath = pref.get(filterPathKey, "");
        final DirectoryDialog dDialog = new DirectoryDialog(_pbtv.getShell());
        dDialog.setFilterPath(filterPath);
        filterPath = dDialog.open();
        final File path = new File(filterPath);
        pref.put(filterPathKey, filterPath);
        final StructuredSelection selectedNodes = _pbtv.getSelectedNodes();
        if(selectedNodes != null && !selectedNodes.isEmpty()) {
            final Object selectedNode = selectedNodes.getFirstElement();
            if(selectedNode instanceof ProfibusSubnetDBO) {
                runProfibusSubnet(path, selectedNode);
            } else if(selectedNode instanceof IocDBO) {
                runIoc(path, selectedNode);
            } else if(selectedNode instanceof FacilityDBO) {
                runFacility(path, selectedNode);
            }
        }
    }

    private void makeFiles(@Nonnull final File path, @Nonnull final ProfibusSubnetDBO subnet) {
        String name = subnet.getName();
        name = name == null ? "" : name;
        final ProfibusConfigWinModGenerator cfg = new ProfibusConfigWinModGenerator();
        cfg.setSubnet(subnet);
        final File xmlFile = new File(path, name + ".cfg");
        final File txtFile = new File(path, name + ".txt");
        makeXMLFile(cfg, xmlFile);
        makeTxtFile(cfg, txtFile);
    }

    private void makeTxtFile(@Nonnull final ProfibusConfigWinModGenerator cfg, @Nonnull final File txtFile) {
        if (txtFile.exists()) {
            final MessageBox box = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_WARNING
                                                  | SWT.YES | SWT.NO);
            box.setMessage("The file " + txtFile.getName() + " exist! Overwrite?");
            final int erg = box.open();
            if (erg == SWT.YES) {
                try {
                    cfg.getTxtFile(txtFile);
                } catch (final IOException e) {
                    openCanCreateFileDialog(txtFile.getName());
                }
            }
        } else {
            try {
                txtFile.createNewFile();
                cfg.getTxtFile(txtFile);
            } catch (final IOException e) {
                openCanCreateFileDialog(txtFile.getName());
            }
        }
    }

    /**
     * @param cfg
     * @param xmlFile
     */
    private void makeXMLFile(@Nonnull final ProfibusConfigWinModGenerator cfg, @Nonnull final File xmlFile) {
        if (xmlFile.exists()) {
            final MessageBox box = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_WARNING
                                                  | SWT.YES | SWT.NO);
            box.setMessage("The file " + xmlFile.getName() + " exist! Overwrite?");
            final int erg = box.open();
            if (erg == SWT.YES) {
                try {
                    cfg.getXmlFile(xmlFile);
                } catch (final IOException e) {
                    openCanCreateFileDialog(xmlFile.getName());
                }
            }
        } else {
            try {
                xmlFile.createNewFile();
                cfg.getXmlFile(xmlFile);
            } catch (final IOException e) {
                openCanCreateFileDialog(xmlFile.getName());
            }
        }
    }

    /**
     * @param name
     */
    private void openCanCreateFileDialog(@Nonnull final String fileName) {
        final MessageBox abortBox = new MessageBox(Display.getDefault().getActiveShell(),
                                                   SWT.ICON_WARNING | SWT.ABORT);
        abortBox.setMessage("The file " + fileName + " can not created!");
        abortBox.open();
    }

    private void runFacility(@Nonnull final File path, @Nonnull final Object selectedNode) {
        final FacilityDBO facility = (FacilityDBO) selectedNode;
        LOG.info("Create XML for Facility: {}", facility);
        for (final IocDBO ioc : facility.getChildren()) {
            for (final ProfibusSubnetDBO subnet : ioc.getChildren()) {
                makeFiles(path, subnet);
            }
        }
    }

    private void runIoc(@Nonnull final File path, @Nonnull final Object selectedNode) {
        final IocDBO ioc = (IocDBO) selectedNode;
        LOG.info("Create XML for Ioc: {}", ioc);
        for (final ProfibusSubnetDBO subnet : ioc.getChildren()) {
            makeFiles(path, subnet);
        }
    }

    private void runProfibusSubnet(@Nonnull final File path, @Nonnull final Object selectedNode) {
        final ProfibusSubnetDBO subnet = (ProfibusSubnetDBO) selectedNode;
        LOG.info("Create XML for Subnet: {}", subnet);
        makeFiles(path, subnet);
    }

}
