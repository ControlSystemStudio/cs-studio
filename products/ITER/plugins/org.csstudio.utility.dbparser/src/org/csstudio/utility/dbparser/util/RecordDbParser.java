/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.utility.dbparser.util;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.csstudio.utility.dbparser.antlr.DbRecordParser;
import org.csstudio.utility.dbparser.data.Field;
import org.csstudio.utility.dbparser.data.Record;
import org.csstudio.utility.dbparser.data.Template;
import org.csstudio.utility.dbparser.exception.DbParsingException;

public class RecordDbParser {

	private CommonTree commonTree;

	private Template epicsDb;

	private List<Record> recordBases;

	private Record localRecord;

	private Field localField;

	private List<Field> recordFields;

	private final TreeAdaptor adaptor;

	// -----------------------------------------------------------------------//

	public RecordDbParser() {
		epicsDb = new Template();
		recordBases = new ArrayList<Record>();
		resetRecordFields();
		adaptor = new CommonTreeAdaptor() {
			public Object create(Token payload) {
				return new CommonTree(payload);
			}
		};
	}

	// -----------------------------------------------------------------------//

	public void parse(CommonTokenStream tokens) {
		try {
			DbRecordParser parser = new DbRecordParser(tokens);
			parser.setTreeAdaptor(adaptor);
			DbRecordParser.top_return r = parser.top();
			CommonTree tree = (CommonTree) r.getTree();
			this.setCommonTree(tree);
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
	}

	// -----------------------------------------------------------------------//

	public void printTree(CommonTree t, int indent) {
		if (t != null) {
			StringBuffer sb = new StringBuffer(indent);
			if (t.getParent() == null && (sb != null && t.getText() != null)) {
				System.out.println(sb.toString() + t.getText().toString());
			}
			for (int i = 0; i < indent; i++)
				sb = sb.append("   ");
			for (int i = 0; i < t.getChildCount(); i++) {
				System.out.println(sb.toString() + t.getChild(i).toString());
				printTree((CommonTree) t.getChild(i), indent + 1);
			}
		}
	}

	// -----------------------------------------------------------------------//

	public void transform() throws DbParsingException {
		this.transform(this.getCommonTree());
	}

	// -----------------------------------------------------------------------//

	public void transform(final CommonTree recordInstanceTree_)
			throws DbParsingException {
		if (recordInstanceTree_ != null) {
			final int childrenNum = recordInstanceTree_.getChildCount();
			for (int i = 0; i < childrenNum; i++) {
				Tree child = recordInstanceTree_.getChild(i);
				if (child != null) {
					visitTree(child);
					this.transform((CommonTree) child);
				}
			}
		} else {
			throw new DbParsingException("Syntax Error");
		}
		this.getEpicsDb().setEPICSRecords(recordBases);
	}

	// -----------------------------------------------------------------------//

	private void visitTree(final Tree tree_) throws DbParsingException {
		final int type = tree_.getType();
		switch (type) {
		case DbRecordParser.RECORD_INSTANCE:
			visitRecordInstance(tree_);
			break;
		case DbRecordParser.RECORD:
			visitRecord(tree_);
			break;
		case DbRecordParser.RECORD_BODY:
			visitRecordBody(tree_);
			break;
		case DbRecordParser.FIELD:
			visitField(tree_);
			break;
		case DbRecordParser.INFO:
			visitInfo(tree_);
			break;
		case DbRecordParser.ALIAS:
			visitAlias(tree_);
			break;
		case DbRecordParser.TYPE:
			visitType(tree_);
			break;
		case DbRecordParser.VALUE:
			visitValue(tree_);
			break;
		case DbRecordParser.ID:
		case DbRecordParser.String:
		case DbRecordParser.NonQuotedString:
			break;
		default:
			throw new DbParsingException("Unknown tree type: " + type);
		}
	}

	// -----------------------------------------------------------------------//

	private void visitValue(final Tree tree_) throws DbParsingException {
		String value = null;
		if (tree_.getChild(0) != null) {
			value = tree_.getChild(0).getText();
		}

		if (value != null) {
			if (value.startsWith("\"")) {
				value = value.substring(1, (value.length() - 1));
			}
			switch (tree_.getParent().getType()) {
			case DbRecordParser.RECORD:
				this.getLocalRecord().setName(value);
				break;
			case DbRecordParser.FIELD:
				this.getLocalField().setValue(value);
				break;
			default:
				throw new DbParsingException("Unknown tree type");
			}
		} else {
			System.out.println("Ignores ... ");
		}

	}

	// -----------------------------------------------------------------------//

	private void visitType(final Tree tree_) throws DbParsingException {
		String type = null;
		if (tree_.getChild(0) != null) {
			type = tree_.getChild(0).getText();
		}

		if (type != null) {
			switch (tree_.getParent().getType()) {
			case DbRecordParser.RECORD:
				this.getLocalRecord().setType(type);
				break;
			case DbRecordParser.FIELD:
				this.getLocalField().setType(type);
				break;
			default:
				throw new DbParsingException("Unknown tree type");
			}
		}

	}

	// -----------------------------------------------------------------------//

	private void visitAlias(final Tree tree_) {
		// NOPMD
	}

	// -----------------------------------------------------------------------//

	private void visitInfo(final Tree tree_) {
		// NOPMD
	}

	// -----------------------------------------------------------------------//

	private void visitField(final Tree tree_) {
		final Field epicsRecordField = new Field();
		this.setLocalField(epicsRecordField);
		recordFields.add(epicsRecordField);
		this.getLocalRecord().setFields(recordFields);
	}

	// -----------------------------------------------------------------------//

	private void visitRecordBody(final Tree tree_) {
		// NOPMD
	}

	// -----------------------------------------------------------------------//

	private void visitRecord(final Tree tree_) {
		final Record recBase = new Record();
		this.setLocalRecord(recBase);
		recordBases.add(recBase);
	}

	// -----------------------------------------------------------------------//

	private void visitRecordInstance(final Tree tree_) {
		this.resetRecordFields();
	}

	// -----------------------------------------------------------------------//

	private void resetRecordFields() {
		recordFields = new ArrayList<Field>();
	}

	// -----------------------------------------------------------------------//

	public CommonTree getCommonTree() {
		return commonTree;
	}

	// -----------------------------------------------------------------------//

	public void setCommonTree(CommonTree commonTree) {
		this.commonTree = commonTree;
	}

	// -----------------------------------------------------------------------//

	public Template getEpicsDb() {
		return epicsDb;
	}

	// -----------------------------------------------------------------------//

	public Record getLocalRecord() {
		return localRecord;
	}

	// -----------------------------------------------------------------------//

	public void setLocalRecord(Record localRecord) {
		this.localRecord = localRecord;
	}

	// -----------------------------------------------------------------------//

	public Field getLocalField() {
		return localField;
	}

	// -----------------------------------------------------------------------//

	public void setLocalField(Field localField) {
		this.localField = localField;
	}
}
