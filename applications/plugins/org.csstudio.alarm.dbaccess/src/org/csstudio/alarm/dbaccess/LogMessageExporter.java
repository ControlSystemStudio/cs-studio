package org.csstudio.alarm.dbaccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class LogMessageExporter {

	public void exportExcelFile(ArrayList<HashMap<String, String>> messageList,
			File path, String[] columnNames) throws IOException {
		FileOutputStream outputStream = null;
		outputStream = new FileOutputStream(path);
		excelExport(messageList, columnNames, outputStream);
		outputStream.close();
	}

	/**
	 * Exports the given list to Excel. Make sure you properly close the stream.
	 * This method does not close the stream.
	 * 
	 * @param list
	 * @param columnNames
	 * @param fileInputStream
	 * @throws IOException
	 */
	private void excelExport(ArrayList<HashMap<String, String>> list,
			String[] columnNames, OutputStream outputStream) throws IOException {

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

		// make it blue
		// f.setColor( (short)0xc );
		// make it bold
		// arial is the default font
		// f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		csCapital.setFont(fontCapital);

		// set the sheet name
		wb.setSheetName(0, "Alarm,Log Messages",
				HSSFWorkbook.ENCODING_COMPRESSED_UNICODE);

		// setCaption(s, r, c);
		// create a row
		r = s.createRow(rownum);

		// HashMap with column title and column number to ease inserting message
		// properties in the table.
		HashMap<String, Short> columnTitles = new HashMap<String, Short>();

		// Set column titles
		short columnCount = 0;
		for (String columnName : columnNames) {
			String[] columnNameParts = columnName.split(",");
			columnTitles.put(columnNameParts[0], columnCount);
			c = r.createCell(columnCount);
			c.setCellStyle(csCapital);
			c.setEncoding(HSSFCell.ENCODING_COMPRESSED_UNICODE);
			c.setCellValue(columnNameParts[0]);
			s.setColumnWidth(columnCount, (short) 6000);
			columnCount++;
		}

		fontNormal.setFontHeightInPoints((short) 10);

		csNormal.setFont(fontNormal);

		for (HashMap<String, String> message : list) {

			rownum++;
			// create a row
			r = s.createRow(rownum);

			Set<String> propertyNames = message.keySet();

			for (String propertyName : propertyNames) {
				// get the column index for the property of the message. If
				// there is no column for the property, do not insert the value
				Short columnIndex = columnTitles.get(propertyName);
				if(columnIndex != null) {
					c = r.createCell(columnIndex);
					c.setCellValue(message.get(propertyName));
				}
			}
		}

		// write the workbook to the output stream
		// close our file (don't blow out our file handles
		wb.write(outputStream);

	}

}
