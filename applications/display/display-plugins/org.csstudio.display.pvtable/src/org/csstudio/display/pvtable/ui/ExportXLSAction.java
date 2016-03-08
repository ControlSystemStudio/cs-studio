package org.csstudio.display.pvtable.ui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.csstudio.display.pvtable.Messages;
import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.Preferences;
import org.csstudio.display.pvtable.model.Measure;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.TimestampHelper;
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.diirt.util.time.Timestamp;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 *
 * @author A.PHILIPPE, L.PHILIPPE GANIL/FRANCE
 */
public class ExportXLSAction extends PVTableAction {
    public ExportXLSAction(final TableViewer viewer) {
        super(Messages.ExportXLSAction, "icons/exportExcel.png", viewer);
        setToolTipText(Messages.ExportXLSAction_TT);
    }

    @Override
    public void run() {
        final PVTableModel model = (PVTableModel) viewer.getInput();
        if (model.getItemCount() == 0) {
            MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.InformationPopup,
                    Messages.EmptyTable);
            return;
        }

        Workbook wBook = new XSSFWorkbook();
        // Display.getCurrent().getActiveShell().open();
        // Open dialog box
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        dialog.setFilterExtensions(new String[] { ".xlsx" });

        String pvTableTitle = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
                .getTitle();
        String format = "ddMMyyyyHHmmss";
        SimpleDateFormat formater = new SimpleDateFormat(format);
        Date date = new Date();
        String dateFormated = formater.format(date);
        date.getTime();
        String excelDefaultName = pvTableTitle.replace(".pvs", dateFormated);
        dialog.setFileName(excelDefaultName);
        String fileName = dialog.open();
        Sheet sheet0 = wBook.createSheet("Sheet 1");

        CellStyle styleValue;
        DataFormat formatValue = wBook.createDataFormat();
        styleValue = wBook.createCellStyle();
        styleValue.setDataFormat(formatValue.getFormat("#,##0.0000"));

        CellStyle styleDate;
        DataFormat formatDate = wBook.createDataFormat();
        styleDate = wBook.createCellStyle();
        styleDate.setDataFormat(formatDate.getFormat("dd/mm/yyyy hh:mm:ss.000"));

        Row rowItemsNames = sheet0.createRow(0);
        Row rowItemsDateValue = sheet0.createRow(1);
        Font font = wBook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        CellStyle style = wBook.createCellStyle();
        style.setFont(font);

        // If no config -> exit
        if (model.getConfig() != null && !model.getConfig().getMeasures().isEmpty()) {

            List<Measure> measures = model.getConfig().getMeasures();

            // Map key pv name, value column index
            Map<String, Integer> measureMap = new HashMap<String, Integer>();
            int indexMapPV = 0;

            // Fill table
            for (int a = 2, b = 0; b < measures.size(); a++, b++) {
                Row row = sheet0.createRow(a);
                Cell measureHeaderCell = row.createCell(0);
                measureHeaderCell.setCellValue(measures.get(b).getItems().get(0).getMeasureHeader());
                measureHeaderCell.setCellStyle(style);
                Cell dateMeasureCell = row.createCell(1);
                Date dateMC = null;
                Timestamp timeMC = null;
                timeMC = VTypeHelper.getTimestamp(measures.get(b).getItems().get(0).getValue());
                dateMC = timeMC.toDate();
                dateMeasureCell.setCellValue(dateMC);
                dateMeasureCell.setCellStyle(styleDate);
                // dateMeasureCell.setCellValue(TimestampHelper.format(VTypeHelper.getTimestamp(measures.get(b).getItems().get(0).getValue())));
                // Parcours des items
                for (int d = 1; d < measures.get(b).getItems().size(); d++) {
                    // If the item doesn't exist in the map
                    if (measureMap.get(measures.get(b).getItems().get(d).getName()) == null) {
                        // On ajoute les données de la PV non présente dans la
                        // map dans les 2 premières lignes.
                        measureMap.put(measures.get(b).getItems().get(d).getName(), indexMapPV += 2);
                        Cell cellIN = rowItemsNames.createCell(indexMapPV);
                        cellIN.setCellValue(measures.get(b).getItems().get(d).getName());
                        cellIN.setCellStyle(style);
                        Cell cellINs = rowItemsDateValue.createCell(indexMapPV);
                        cellINs.setCellValue("Date");
                        cellINs.setCellStyle(style);
                        Cell cellDVs = rowItemsDateValue.createCell(indexMapPV + 1);
                        cellDVs.setCellValue("Value");
                        cellDVs.setCellStyle(style);
                        sheet0.addMergedRegion(new CellRangeAddress(0, 0, indexMapPV, indexMapPV + 1));
                    }
                    // On ajoute les données dans la nouvelle lignes
                    Cell cellD = row.createCell((int) measureMap.get(measures.get(b).getItems().get(d).getName()));
                    Date dateC = null;
                    Timestamp timeC = null;
                    String timeS = null;
                    try {
                        timeC = TimestampHelper.parse(measures.get(b).getItems().get(d).getTime_saved());
                        dateC = timeC.toDate();
                        cellD.setCellValue(dateC);
                        cellD.setCellStyle(styleDate);
                    } catch (ParseException e) {
                        timeS = measures.get(b).getItems().get(d).getTime_saved();
                        cellD.setCellValue(timeS);
                        cellD.setCellStyle(styleDate);
                    }
                    Cell cellV = row.createCell((int) measureMap.get(measures.get(b).getItems().get(d).getName()) + 1);
                    NumberFormat formatNumbI = NumberFormat.getInstance(Locale.US);
                    Number numbI = null;
                    try {
                        numbI = formatNumbI.parse(measures.get(b).getItems().get(d).getSavedValue().get().toString());
                        cellV.setCellValue(numbI.doubleValue());
                    } catch (ParseException e) {
                        cellV.setCellValue(measures.get(b).getItems().get(d).getSavedValue().get().toString());
                    }
                    cellV.setCellStyle(styleValue);
                }
            }
            for (int i = 0; i <= indexMapPV; i++) {
                sheet0.autoSizeColumn(i);
            }
        } else {

            // Nb lines in table
            int nbItems = model.getItemCount();

            // Nb columns
            int nbColumns = 2;

            if (Preferences.showDescription()) {
                nbColumns++;
            }

            if (Preferences.showSaveTimestamp()) {
                nbColumns++;
            }

            // Header line
            Row headerRow = sheet0.createRow(0);

            Cell pvHeaderNameCell = headerRow.createCell(0);
            pvHeaderNameCell.setCellValue(Messages.PV);
            pvHeaderNameCell.setCellStyle(style);

            if (Preferences.showDescription()) {
                Cell descriptionHeaderCell = headerRow.createCell(1);
                descriptionHeaderCell.setCellValue(Messages.Description);
                descriptionHeaderCell.setCellStyle(style);

                Cell savedHeaderValueCell = headerRow.createCell(2);
                savedHeaderValueCell.setCellValue(Messages.Saved);
                savedHeaderValueCell.setCellStyle(style);
            } else {
                Cell savedHeaderValueCell = headerRow.createCell(1);
                savedHeaderValueCell.setCellValue(Messages.Saved);
                savedHeaderValueCell.setCellStyle(style);
            }

            if (Preferences.showSaveTimestamp()) {
                if (Preferences.showDescription()) {
                    Cell savedTimeStampHeadereCell = headerRow.createCell(3);
                    savedTimeStampHeadereCell.setCellValue(Messages.Saved_Value_TimeStamp);
                    savedTimeStampHeadereCell.setCellStyle(style);
                } else {
                    Cell savedTimeStampHeadereCell = headerRow.createCell(2);
                    savedTimeStampHeadereCell.setCellValue(Messages.Saved_Value_TimeStamp);
                    savedTimeStampHeadereCell.setCellStyle(style);
                }
            }

            for (int i = 0, j = 1; i < nbItems; i++, j++) {

                Row row = sheet0.createRow(j);

                Cell pvNameCell = row.createCell(0);

                if (model.getItem(i).isComment()) {
                    pvNameCell.setCellValue(model.getItem(i).getComment());
                } else {
                    pvNameCell.setCellValue(model.getItem(i).getName());
                }
                if (Preferences.showDescription()) {
                    // Description
                    Cell descriptionCell = row.createCell(1);
                    if (model.getItem(i).getDescription().isEmpty()) {
                        descriptionCell.setCellValue("");
                    } else {
                        descriptionCell.setCellValue(model.getItem(i).getDescription());
                    }

                    // Snapshot value
                    Cell savedValueCell = row.createCell(2);

                    if (model.getItem(i).getSavedValue().isPresent()) {
                        NumberFormat formatNumbI = NumberFormat.getInstance(Locale.US);
                        Number numbI = null;
                        try {
                            numbI = formatNumbI.parse(model.getItem(i).getSavedValue().get().toString());
                            savedValueCell.setCellValue(numbI.doubleValue());
                        } catch (ParseException e) {
                            savedValueCell.setCellValue(model.getItem(i).getSavedValue().get().toString());
                        }
                        savedValueCell.setCellStyle(styleValue);
                    }
                } else {
                    // Snapshot value
                    Cell savedValueCell = row.createCell(1);

                    if (model.getItem(i).getSavedValue().isPresent()) {
                        NumberFormat formatNumbI = NumberFormat.getInstance(Locale.US);
                        Number numbI = null;
                        try {
                            numbI = formatNumbI.parse(model.getItem(i).getSavedValue().get().toString());
                            savedValueCell.setCellValue(numbI.doubleValue());
                        } catch (ParseException e) {
                            savedValueCell.setCellValue(model.getItem(i).getSavedValue().get().toString());
                        }
                        savedValueCell.setCellStyle(styleValue);
                    }
                }
                if (Preferences.showSaveTimestamp()) {
                    if (Preferences.showDescription()) {
                        // Save Timestamp
                        Cell savedTimestamp = row.createCell(3);
                        if (model.getItem(i).getTime_saved().isEmpty()) {
                            savedTimestamp.setCellValue("");
                        } else {
                            Date dateTS = null;
                            Timestamp timeTS = null;
                            String timeS = null;
                            try {
                                timeTS = TimestampHelper.parse(model.getItem(i).getTime_saved());
                            } catch (ParseException e) {
                                timeS = model.getItem(i).getTime_saved();
                            }
                            dateTS = timeTS.toDate();
                            savedTimestamp.setCellValue(dateTS);
                            savedTimestamp.setCellStyle(styleDate);
                        }
                    } else {
                        // Save Timestamp
                        Cell savedTimestamp = row.createCell(2);
                        if (model.getItem(i).getTime_saved().isEmpty()) {
                            savedTimestamp.setCellValue("");
                        } else {
                            Date dateTS = null;
                            Timestamp timeTS = null;
                            String timeS = null;
                            try {
                                timeTS = TimestampHelper.parse(model.getItem(i).getTime_saved());
                                dateTS = timeTS.toDate();
                                savedTimestamp.setCellValue(dateTS);
                            } catch (ParseException e) {
                                timeS = model.getItem(i).getTime_saved();
                                savedTimestamp.setCellValue(timeS);
                            }
                            savedTimestamp.setCellStyle(styleDate);
                        }
                    }
                }
            }
            for (int i = 0; i < nbColumns; i++) {
                sheet0.autoSizeColumn(i);
            }
        }
        try {
            FileOutputStream fileOut;
            if (fileName != null) {
                fileOut = new FileOutputStream(fileName);
                wBook.write(fileOut);
                if (fileOut != null) {
                    fileOut.close();
                    wBook.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Plugin.getLogger().log(Level.WARNING, "Cannot create a new file xlsx.", e);
        }
    }
}