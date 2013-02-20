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
        for row in selection:
        	phyName=row[1]
        	cuName=row[0];
        # change $(CU) substitution
        macroInput = DataUtil.createMacrosInput(True)
        macroInput.put("CU", cuName)
        macroInput.put("PHY_NAME", phyName)
        macroInput.put("FCT_NAME", fct_name)
        # open OPI
        # see https://svnpub.iter.org/codac/iter/codac/dev/units/m-css-boy/trunk/org.csstudio.opibuilder/src/org/csstudio/opibuilder/scriptUtil/ScriptUtil.java
        if cuName.startswith('P'):
        	ScriptUtil.openOPI(display.getWidget("Table"), fct_name+"-PLCDetails.opi", 1, macroInput)
        else:
        	ScriptUtil.openOPI(display.getWidget("Table"), fct_name+"-CubiclePLCDetails.opi", 0, macroInput)
table.addSelectionChangedListener(SelectionListener())