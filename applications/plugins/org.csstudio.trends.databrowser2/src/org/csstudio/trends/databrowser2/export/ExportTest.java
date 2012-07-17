/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.export;

import org.csstudio.data.values.IValue.Format;
import org.csstudio.trends.databrowser2.model.ArchiveDataSource;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the export functionality
 *  <p>
 *  RDB URL, KEY, PVItem names, time range all need to be adjusted
 *  for the site where this test should run.
 *
 *  @author Kay Kasemir
 *  FIXME (kasemir) : remove sysos, use assertions, parameterize DB and PV
 */
@SuppressWarnings("nls")
@Ignore("See FIXME")
public class ExportTest implements ExportErrorHandler
{
    private static final String URL = "jdbc:oracle:thin:sns_reports/sns@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.138)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=172.31.75.141)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=ics_prod_lba)))";
    private static final int KEY = 1;

    /** @return Model for demo */
    private Model getTestModel() throws Exception
    {
        final Model model = new Model();
        model.addItem(createPVItem("DTL_LLRF:IOC1:Load"));
        model.addItem(createPVItem("DTL_Vac:CCG301:Pcalc"));
        model.addItem(createPVItem("SCL_LLRF:IOC01a:Load"));
        model.getItem(0).setDisplayName("CPU Load");
        model.setTimespan(2*60*24);
//        model.write(new OutputStream()
//        {
//            @Override
//            public void write(int b) throws IOException
//            {
//                System.out.write(b);
//            }
//        });
        return model;
    }

    /** @return PV with some archive data source */
    private PVItem createPVItem(final String name) throws Exception
    {
        final PVItem item = new PVItem(name, 1.0);
        item.addArchiveDataSource(new ArchiveDataSource(URL, KEY, "test"));
        return item;
    }

    /** Export channels one-by-one
     *  @throws Exception on error
     */
    @Test
    public void plainExport() throws Exception
    {
        final Model model = getTestModel();
        final ExportJob export = new PlainExportJob(model,
                model.getStartTime(), model.getEndTime(),
                Source.OPTIMIZED_ARCHIVE, 60,
                new ValueWithInfoFormatter(Format.Exponential, 6),
                "/tmp/plain.dat", this);
        export.run(new SysoutProgressMonitor());
    }

    /** Export channels in a spreadsheet
     *  @throws Exception on error
     */
    @Test
    public void speadsheetExport() throws Exception
    {
        final Model model = getTestModel();
        final ExportJob export = new SpreadsheetExportJob(model,
                model.getStartTime(), model.getEndTime(),
                Source.RAW_ARCHIVE, 10,
                new ValueWithInfoFormatter(Format.Exponential, 3),
                "/tmp/sheet.dat", this);
        export.run(new SysoutProgressMonitor());
    }

    /** Export channels to Matlab file */
    @Test
    public void matlabExport() throws Exception
    {
        final Model model = getTestModel();
        final ExportJob export = new MatlabScriptExportJob(model,
                model.getStartTime(), model.getEndTime(),
                Source.RAW_ARCHIVE, 10,
                "/tmp/matlab.m", this);
        export.run(new SysoutProgressMonitor());
    }

    /** @see ExportErrorHandler */
    @Override
    public void handleExportError(final Exception ex)
    {
        ex.printStackTrace();
    }
}
