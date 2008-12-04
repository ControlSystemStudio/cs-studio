package org.csstudio.dct.metamodel;

import org.csstudio.dct.metamodel.internal.Choice;
import org.csstudio.dct.metamodel.internal.DatabaseDefinition;
import org.csstudio.dct.metamodel.internal.FieldDefinition;
import org.csstudio.dct.metamodel.internal.MenuDefinition;
import org.csstudio.dct.metamodel.internal.RecordDefinition;

public class Factory {
	public static DatabaseDefinition createSampleDatabaseDefinition() {
		DatabaseDefinition databaseDefinition = new DatabaseDefinition("1.0");

		IRecordDefinition recordDefinition = new RecordDefinition("ai");
		databaseDefinition.addRecordDefinition(recordDefinition);

		recordDefinition.addFieldDefinition(new FieldDefinition("NAME", "DBF_STRING"));
		recordDefinition.addFieldDefinition(new FieldDefinition("DESC", "DBF_STRING"));
		recordDefinition.addFieldDefinition(new FieldDefinition("ASG", "DBF_STRING"));
		recordDefinition.addFieldDefinition(new FieldDefinition("SCAN", "DBF_MENU"));
		recordDefinition.addFieldDefinition(new FieldDefinition("PINI", "DBF_MENU"));
		recordDefinition.addFieldDefinition(new FieldDefinition("PHAS", "DBF_SHORT"));
		recordDefinition.addFieldDefinition(new FieldDefinition("EVNT", "DBF_SHORT"));
		recordDefinition.addFieldDefinition(new FieldDefinition("TSE", "DBF_SHORT"));

		IMenuDefinition menuDefinition = new MenuDefinition("menuAlarmStat");
		menuDefinition.addChoice(new Choice("menuAlarmStatNO_ALARM", "NO_ALARM"));
		menuDefinition.addChoice(new Choice("menuAlarmStatREAD", "READ"));
		menuDefinition.addChoice(new Choice("menuAlarmStatWRITE", "WRITE"));
		menuDefinition.addChoice(new Choice("menuAlarmStatHIHI", "HIHI"));
		menuDefinition.addChoice(new Choice("menuAlarmStatHIGH", "HIGH"));
		menuDefinition.addChoice(new Choice("menuAlarmStatLOLO", "LOLO"));
		menuDefinition.addChoice(new Choice("menuAlarmStatLOW", "LOW"));
		menuDefinition.addChoice(new Choice("menuAlarmStatSTATE", "STATE"));
		menuDefinition.addChoice(new Choice("menuAlarmStatCOS", "COS"));
		menuDefinition.addChoice(new Choice("menuAlarmStatCOMM", "COMM"));
		menuDefinition.addChoice(new Choice("menuAlarmStatTIMEOUT", "TIMEOUT"));
		menuDefinition.addChoice(new Choice("menuAlarmStatHWLIMIT", "HWLIMIT"));
		menuDefinition.addChoice(new Choice("menuAlarmStatCALC", "CALC"));
		menuDefinition.addChoice(new Choice("menuAlarmStatSCAN", "SCAN"));
		menuDefinition.addChoice(new Choice("menuAlarmStatLINK", "LINK"));
		menuDefinition.addChoice(new Choice("menuAlarmStatSOFT", "SOFT"));
		menuDefinition.addChoice(new Choice("menuAlarmStatBAD_SUB", "BAD_SUB"));
		menuDefinition.addChoice(new Choice("menuAlarmStatUDF", "UDF"));
		menuDefinition.addChoice(new Choice("menuAlarmStatDISABLE", "DISABLE"));
		menuDefinition.addChoice(new Choice("menuAlarmStatSIMM", "SIMM"));
		menuDefinition.addChoice(new Choice("menuAlarmStatREAD_ACCESS", "READ_ACCESS"));
		menuDefinition.addChoice(new Choice("menuAlarmStatWRITE_ACCESS", "WRITE_ACCESS"));
		
		FieldDefinition def = new FieldDefinition("MMENU",  "DBF_MENU");
		def.setMenuDefinition(menuDefinition);
		recordDefinition.addFieldDefinition(def);

		return databaseDefinition;
	}

}
