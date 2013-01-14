/*
 * Copyright (c) ${year} Stiftung Deutsches Elektronen-Synchrotron,
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
 * $$Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $$
 */
package org.csstudio.config.ioconfig.editorparts;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 14.06.2010
 */
public class GSDFileAddListener implements SelectionListener {

    private static final Logger LOG = LoggerFactory.getLogger(GSDFileAddListener.class);

    private final TabFolder _tabFolder;
    private final TableViewer _tableViewer;
    private final Composite _comp;
    private final AbstractNodeEditor<?> _abstractNodeEditor;

    /**
     * Constructor.
     * @param abstractNodeEditor
     * @param tabFolder
     * @param tableViewer
     * @param comp
     */
    protected GSDFileAddListener(@Nonnull final AbstractNodeEditor<?> abstractNodeEditor,@Nonnull final TabFolder tabFolder,
                                 @Nonnull final TableViewer tableViewer,
                                 @Nonnull final Composite comp) {
        _abstractNodeEditor = abstractNodeEditor;
        _tabFolder = tabFolder;
        _tableViewer = tableViewer;
        _comp = comp;
    }

    @Override
    public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
        doFileAdd();
    }

    @Override
    public void widgetSelected(@Nullable final SelectionEvent e) {
        doFileAdd();
    }

    private void doFileAdd() {
        final FileDialog fd = new FileDialog(_comp.getShell(), SWT.MULTI);
        fd.setFilterExtensions(new String[] {"*.gsd;*.gsg", "*.gs?" });
        fd.setFilterNames(new String[] {"GS(GER)", "GS(ALL)" });
        fd.setFilterPath(".");
        if (fd.open() != null) {
            final File path = new File(fd.getFilterPath());
            for (final String fileName : fd.getFileNames()) {
                if (fileNotContain(fileName)) {
                    try {
                        final String text = ConfigHelper.file2String(new File(path, fileName));
                        final File file = new File(path, fileName);
                        final GSDFileDBO gsdFile = new GSDFileDBO(file.getName(), text.toString());
                        _abstractNodeEditor.getGsdFiles().add(gsdFile);
                        _tableViewer.setInput(_abstractNodeEditor.getGsdFiles());
                        Repository.save(gsdFile);
                    } catch (final PersistenceException e) {
                        DeviceDatabaseErrorDialog.open(null, "Can't safe GSD File! Database error", e);
                        LOG.error("Can't safe GSD File! Database error", e);
                    } catch (final IOException e) {
                        DeviceDatabaseErrorDialog.open(null, "Can't safe GSD File! File read error", e);
                        LOG.error("Can't safe GSD File! File read error", e);
                    }
                } else {
                    MessageDialog.openInformation(_tabFolder.getShell(),
                                                  "Double GSD File",
                    "File is already in the DB");
                }
            }
        }
    }

    private boolean fileNotContain(@Nullable final String fileName) {
        boolean add = true;
        final List<GSDFileDBO> gsdFiles = _abstractNodeEditor.getGsdFiles();
        for (final GSDFileDBO file : gsdFiles) {
            add = !file.getName().equals(fileName);
            if (!add) {
                break;
            }
        }
        return add;
    }
}
