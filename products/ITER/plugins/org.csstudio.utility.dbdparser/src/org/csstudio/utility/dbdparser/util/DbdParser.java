/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.csstudio.utility.dbdparser.antlr.DbdFileLexer;
import org.csstudio.utility.dbdparser.antlr.DbdFileParser;
import org.csstudio.utility.dbdparser.data.Breaktable;
import org.csstudio.utility.dbdparser.data.Device;
import org.csstudio.utility.dbdparser.data.Driver;
import org.csstudio.utility.dbdparser.data.Field;
import org.csstudio.utility.dbdparser.data.Function;
import org.csstudio.utility.dbdparser.data.Include;
import org.csstudio.utility.dbdparser.data.Menu;
import org.csstudio.utility.dbdparser.data.Path;
import org.csstudio.utility.dbdparser.data.RecordType;
import org.csstudio.utility.dbdparser.data.Registrar;
import org.csstudio.utility.dbdparser.data.Template;
import org.csstudio.utility.dbdparser.data.Variable;
import org.csstudio.utility.dbdparser.exception.DbdParsingException;

/**
 * Create {@link Template} from ANTLR {@link Tree}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbdParser {

	private final TreeAdaptor adaptor;

	private CommonTree commonTree;

	private Template currentTemplate;

	public DbdParser() {
		adaptor = new CommonTreeAdaptor() {
			public Object create(Token payload) {
				return new CommonTree(payload);
			}
		};
	}

	/**
	 * Parse tokens from {@link DbdFileLexer}.
	 * 
	 * @param tokens
	 */
	public void parse(CommonTokenStream tokens) {
		try {
			DbdFileParser parser = new DbdFileParser(tokens);
			parser.setTreeAdaptor(adaptor);
			DbdFileParser.top_return r = parser.top();
			this.commonTree = (CommonTree) r.getTree();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the tree result of parsing.
	 */
	public String print() {
		return printTree(this.commonTree, 0);
	}

	private String printTree(Tree t, int indent) {
		StringBuffer sb = new StringBuffer();
		if (t != null) {
			for (int i = 0; i < indent; i++)
				sb.append("\t");
			if (t.getText() != null) {
				sb.append(t.getText().toString() + "\n");
			} else {
				sb.append("root\n");
			}
			for (int i = 0; i < t.getChildCount(); i++)
				sb.append(printTree(t.getChild(i), indent + 1));
		}
		return sb.toString();
	}

	/**
	 * Generates {@link Template} from {@link Tree}.
	 * 
	 * @throws DbdParsingException
	 */
	public void transform() throws DbdParsingException {
		this.currentTemplate = new Template();
		if (this.commonTree != null) {
			if (this.commonTree.getText() == null) { // root
				final int childrenNum = this.commonTree.getChildCount();
				for (int i = 0; i < childrenNum; i++) {
					Tree child = this.commonTree.getChild(i);
					if (child != null)
						visitTree(child);
				}
			} else { // no root
				visitTree(this.commonTree);
			}
		} else {
			throw new DbdParsingException("Syntax error");
		}
	}

	private void visitTree(final Tree _tree) throws DbdParsingException {
		final int type = _tree.getType();
		switch (type) {
		case DbdFileParser.PATH:
			this.currentTemplate.getPaths().add(
					new Path(getText(_tree)));
			break;
		case DbdFileParser.INCLUDE:
			this.currentTemplate.getIncludes().add(
					new Include(getText(_tree)));
			break;
		case DbdFileParser.MENU:
			visitMenu(_tree);
			break;
		case DbdFileParser.RECORDTYPE:
			visitRecordType(_tree);
			break;
		case DbdFileParser.DEVICE:
			visitDevice(_tree);
			break;
		case DbdFileParser.DRIVER:
			this.currentTemplate.getDrivers().add(
					new Driver(getText(_tree)));
			break;
		case DbdFileParser.REGISTRAR:
			this.currentTemplate.getRegistrars().add(
					new Registrar(getText(_tree)));
			break;
		case DbdFileParser.VARIABLE:
			this.currentTemplate.getVariables().add(
					new Variable(getText(_tree)));
			break;
		case DbdFileParser.FUNCTION:
			this.currentTemplate.getFunctions().add(
					new Function(getText(_tree)));
			break;
		case DbdFileParser.BREAKTABLE:
			visitBreaktable(_tree);
			break;
		default:
			throw new DbdParsingException("Unknown tree type: "
					+ _tree.getText());
		}
	}

	private void visitMenu(final Tree _tree) throws DbdParsingException {
		Menu menu = null;
		final int childrenNum = _tree.getChildCount();
		for (int i = 0; i < childrenNum; i++) {
			Tree menuChild = _tree.getChild(i);
			if (menuChild != null) {
				final int type = menuChild.getType();
				switch (type) {
				case DbdFileParser.NAME:
					menu = new Menu(getText(menuChild));
					break;
				case DbdFileParser.CHOICE:
					String name = getText(menuChild.getChild(0));
					String value = getText(menuChild.getChild(1));
					if (menu != null)
						menu.addChoice(name, value);
					break;
				case DbdFileParser.INCLUDE:
					if (menu != null)
						menu.addInclude(new Include(getText(menuChild)));
					break;
				default:
					throw new DbdParsingException("Unknown MENU tree type: "
							+ menuChild.getText());
				}
			}
		}
		if (menu != null)
			this.currentTemplate.getMenus().add(menu);
	}

	private void visitRecordType(final Tree _tree) throws DbdParsingException {
		RecordType recordType = null;
		final int childrenNum = _tree.getChildCount();
		for (int i = 0; i < childrenNum; i++) {
			Tree recordTypeChild = _tree.getChild(i);
			if (recordTypeChild != null) {
				final int type = recordTypeChild.getType();
				switch (type) {
				case DbdFileParser.NAME:
					recordType = new RecordType(getText(recordTypeChild));
					break;
				case DbdFileParser.FIELD:
					if (recordType != null)
						visitField(recordTypeChild, recordType);
					break;
				case DbdFileParser.INCLUDE:
					if (recordType != null)
						recordType.addInclude(new Include(
								getText(recordTypeChild)));
					break;
				default:
					throw new DbdParsingException(
							"Unknown RECORDTYPE tree type: "
									+ recordTypeChild.getText());
				}
			}
		}
		if (recordType != null)
			this.currentTemplate.getRecordTypes().add(recordType);
	}

	private void visitField(final Tree _tree, RecordType recordType)
			throws DbdParsingException {
		String fieldName = null, fieldType = null;
		Map<String, Object> rules = new TreeMap<String, Object>();
		final int childrenNum = _tree.getChildCount();
		for (int i = 0; i < childrenNum; i++) {
			Tree fieldChild = _tree.getChild(i);
			if (fieldChild != null) {
				final int type = fieldChild.getType();
				switch (type) {
				case DbdFileParser.NAME:
					fieldName = getText(fieldChild);
					break;
				case DbdFileParser.TYPE:
					fieldType = getText(fieldChild);
					break;
				case DbdFileParser.RULE:
					String name = getText(fieldChild.getChild(0));
					String value = getText(fieldChild.getChild(1));
					rules.put(name, value);
					break;
				default:
					throw new DbdParsingException("Unknown FIELD tree type: "
							+ fieldChild.getText());
				}
			}
		}
		if (fieldName != null && fieldType != null) {
			Field field = new Field(fieldName, fieldType);
			field.setRules(rules);
			recordType.addField(field);
		}
	}

	private void visitDevice(final Tree _tree) throws DbdParsingException {
		final int childrenNum = _tree.getChildCount();
		if (childrenNum < 4)
			throw new DbdParsingException("Missing text child in DEVICE node: "
					+ childrenNum);
		String recordType = _tree.getChild(0).getText();
		String linkType = _tree.getChild(1).getText();
		String dsetName = _tree.getChild(2).getText();
		String choiceString = _tree.getChild(3).getText();
		this.currentTemplate.getDevices().add(
				new Device(recordType, linkType, dsetName, choiceString));
	}

	private void visitBreaktable(final Tree _tree) throws DbdParsingException {
		Breaktable bt = null;
		final int childrenNum = _tree.getChildCount();
		for (int i = 0; i < childrenNum; i++) {
			Tree btChild = _tree.getChild(i);
			if (btChild != null) {
				final int type = btChild.getType();
				switch (type) {
				case DbdFileParser.NAME:
					bt = new Breaktable(getText(btChild), childrenNum - 1, 2);
					break;
				case DbdFileParser.VALUE:
					Float raw = Float.valueOf(getText(btChild.getChild(0)));
					Float eng = Float.valueOf(getText(btChild.getChild(1)));
					if (bt != null) {
						bt.addValue(i, 0, raw);
						bt.addValue(i, 1, eng);
					}
					break;
				default:
					throw new DbdParsingException(
							"Unknown BREAKTABLE tree type: "
									+ btChild.getText());
				}
			}
		}
		if (bt != null)
			this.currentTemplate.getBreaktables().add(bt);
	}

	private String getText(final Tree _tree) throws DbdParsingException {
		if (_tree.getChild(0) == null)
			throw new DbdParsingException("Missing text child in node: "
					+ _tree.getText());
		return _tree.getChild(0).getText();
	}

	public CommonTree getCommonTree() {
		return commonTree;
	}

	public Template getCurrentTemplate() {
		return currentTemplate;
	}

}
