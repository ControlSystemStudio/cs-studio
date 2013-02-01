from org.csstudio.opibuilder.scriptUtil import PVUtil
from org.csstudio.opibuilder.scriptUtil import DataUtil
from org.csstudio.opibuilder.scriptUtil import ScriptUtil
from org.csstudio.swt.widgets.natives.SpreadSheetTable  import ITableSelectionChangedListener
from java.util import Arrays

table = widget.getTable()
fct_name=display.getPropertyValue("name")
class SelectionListener(ITableSelectionChangedListener):
    def selectionChanged(self, selection):
    	cuName=""
    	phyName=""
        plcIocHlts =""
        for row in selection:
        	phyName=row[1]
        	cuName=row[0]
        	plcIocHlts=row[6];
        # change $(CU) substitution
        macroInput = DataUtil.createMacrosInput(True)
        macroInput.put("CU", cuName)
        macroInput.put("PHY_NAME", phyName)
        macroInput.put("FCT_NAME", fct_name)
        if plcIocHlts == "":
        	macroInput.put("SHOW_PLC_IOC", "false")
        else:
        	macroInput.put("SHOW_PLC_IOC", "true")
        # open OPI
        # see https://svnpub.iter.org/codac/iter/codac/dev/units/m-css-boy/trunk/org.csstudio.opibuilder/src/org/csstudio/opibuilder/scriptUtil/ScriptUtil.java
        ScriptUtil.openOPI(display.getWidget("Table"), fct_name+"-CtrlUnitDetails.opi", 1, macroInput)
table.addSelectionChangedListener(SelectionListener())
