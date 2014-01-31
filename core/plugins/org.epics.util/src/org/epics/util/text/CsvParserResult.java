/**
 * Copyright (C) 2012-14 epics-util developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */

package org.epics.util.text;

import java.util.List;

/**
 *
 * @author carcassi
 */
public class CsvParserResult {
    private final List<String> columnNames;
    private final List<Object> columnValues;
    private final List<Class<?>> columnTypes;
    private final int rowCount;
    private final boolean parsingSuccessful;
    private final String message;

    CsvParserResult(List<String> columnNames, List<Object> columnValues, List<Class<?>> columnTypes,
            int rowCount, boolean parsingSuccessful, String message) {
        this.columnNames = columnNames;
        this.columnValues = columnValues;
        this.columnTypes = columnTypes;
        this.rowCount = rowCount;
        this.parsingSuccessful = parsingSuccessful;
        this.message = message;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Object> getColumnValues() {
        return columnValues;
    }

    public List<Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public int getRowCount() {
        return rowCount;
    }

    public boolean isParsingSuccessful() {
        return parsingSuccessful;
    }

    public String getMessage() {
        return message;
    }
    
}
