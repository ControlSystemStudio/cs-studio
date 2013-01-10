package org.csstudio.display.pvmanager.pvtable.editors;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvmanager.pvtable.PVTableModel;
import org.csstudio.display.pvmanager.pvtable.PVTableStaXParser;
import org.csstudio.display.pvmanager.pvtable.PVTableModel.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PVTableFileReadUnitTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	
	private File file;
	
	@Before
	public void setUp() throws Exception {
		file = temporaryFolder.newFile("test.css-pvtable");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void readWritePVTableFile(){
		List<ProcessVariable> testList = new ArrayList<ProcessVariable>();
		testList.add(new ProcessVariable("sim://noise"));
		testList.add(new ProcessVariable("sim://gaussian"));
		testList.add(new ProcessVariable("sim://String"));
		PVTableModel testModel = new PVTableModel();
		for (ProcessVariable processVariable : testList) {
			testModel.addPVName(processVariable);
		}
		writePVTableFile(Arrays.asList(testModel.getItems()), file.getAbsolutePath());
		
		List<ProcessVariable> list = PVTableStaXParser.readPVTableFile(file.getAbsolutePath());
		
		assertTrue(list.equals(testList));		
	}
	
	public void writePVTableFile(List<Item> list, String fileName) {
		try {
			PVTableStaXParser.createByteBuffer(list).writeTo(new FileOutputStream(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
