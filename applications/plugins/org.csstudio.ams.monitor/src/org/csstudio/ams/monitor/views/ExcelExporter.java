
package org.csstudio.ams.monitor.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.csstudio.ams.dbAccess.configdb.HistoryTObject;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Exporting the table content of ams monitor into an excel file.
 * 
 * @author jhatje
 * 
 */
public class ExcelExporter extends Job {

	/**
	 * Final data structure of strings that can directly written to POI lib.
	 */
	private List<List<String>> _exportTable = new ArrayList<List<String>>();

	private File _path;

	public ExcelExporter(File path) {
		super("Excel Export");
		_path = path;
	}

	public void setData(ArrayList<HistoryTObject> historyList,
			TableColumn[] tableColumns) {
		List<String> tableRow = new ArrayList<String>();
		for (TableColumn tableColumn : tableColumns) {
			tableRow.add(tableColumn.getText());
		}
		_exportTable.add(tableRow);
		for (HistoryTObject historyObject : historyList) {
			tableRow = new ArrayList<String>();
			tableRow.add(Integer.toString(historyObject.getHistoryID()));
			tableRow.add(historyObject.getTimeNew().toString());
			tableRow.add(historyObject.getType());
			tableRow.add(historyObject.getMsgHost());
			tableRow.add(historyObject.getMsgName());
			tableRow.add(historyObject.getMsgEventtime());
			tableRow.add(historyObject.getDescription());
			if (historyObject.getActionType() == null
					|| historyObject.getActionType().length() == 0) {
				tableRow.add("");
			} else {
				tableRow.add(historyObject.getActionType());
			}
			if ((historyObject.getActionType() == null || historyObject
					.getActionType().length() == 0)
					&& (!historyObject.getType().equals("Announce"))) {
				tableRow.add("");
			} else {
				tableRow.add(Integer.toString(historyObject.getUserRef()));
			}
			tableRow.add(historyObject.getUserName());
			tableRow.add(historyObject.getDestType());
			tableRow.add(historyObject.getDestAdress());
			if ((historyObject.getActionType() == null || historyObject
					.getActionType().length() == 0)
					&& (!historyObject.getType().equals("Announce"))) {
				tableRow.add("");
			} else if (historyObject.getGroupRef() == 0) {
				tableRow.add("-");
			} else {
				tableRow.add(Integer.toString(historyObject.getGroupRef()));
			}
			if ((historyObject.getActionType() == null || historyObject
					.getActionType().length() == 0)
					&& (!historyObject.getType().equals("Announce"))) {
				tableRow.add("");
			} else if (historyObject.getGroupRef() == 0) {
				tableRow.add("-");
			} else {
				tableRow.add(historyObject.getGroupName());
			}
			if ((historyObject.getActionType() == null || historyObject
					.getActionType().length() == 0)
					&& (!historyObject.getType().equals("Announce"))) {
				tableRow.add("");
			} else if (historyObject.getReceiverPos() == 0) {
				tableRow.add("-");
			} else {
				tableRow.add(Integer.toString(historyObject.getReceiverPos()));
			}
			_exportTable.add(tableRow);
		}
	}

	/**
	 * Exports the given list to Excel. Make sure you properly close the stream.
	 * This method does not close the stream.
	 * 
	 * @throws IOException
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(_path);

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

			csCapital.setFont(fontCapital);

			// set the sheet name
			wb.setSheetName(0, "Alarm Monitor Messages",
					HSSFWorkbook.ENCODING_COMPRESSED_UNICODE);

			// create a row
			r = s.createRow(rownum);

			// Set column titles
			short columnCount = 0;
			List<String> columnTitles = _exportTable.get(0);
			for (String title : columnTitles) {
				c = r.createCell(columnCount);
				c.setCellStyle(csCapital);
				c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
				c.setCellValue(title);
				s.setColumnWidth(columnCount, (short) 6000);
				columnCount++;
			}
			_exportTable.remove(columnTitles);

			fontNormal.setFontHeightInPoints((short) 10);

			csNormal.setFont(fontNormal);

			for (List<String> rowContent : _exportTable) {

				rownum++;
				columnCount = 0;
				// create a row
				r = s.createRow(rownum);
				for (String cellContent : rowContent) {
					c = r.createCell(columnCount);
					c.setCellValue(cellContent);
					columnCount++;
				}
			}


			// write the workbook to the output stream
			// close our file (don't blow out our file handles
			wb.write(outputStream);
			outputStream.close();
			_exportTable.clear();
		} catch (FileNotFoundException e) {
			CentralLogger.getInstance().error(this,
					"File not found " + e.toString());
			return Status.CANCEL_STATUS;
		} catch (IOException e) {
			CentralLogger.getInstance().error(this,
					"IO Exception " + e.toString());
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
}
