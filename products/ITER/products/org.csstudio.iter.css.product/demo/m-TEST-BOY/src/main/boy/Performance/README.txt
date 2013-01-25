Auto generate opi file.

The script will fill a standard sized display with the widgets specified.
It will try to fill the display with as many instances of the widget it can.

first
chmod a+x auto-generate-OPI

usage

./auto-generate-OPI outputfileName iocName startingPV widgetHeight widgetWidth widgetType

Example: 

./auto-generate-OPI text_update.opi A 0 50 50 text_update
./auto-generate-OPI multistate_symbol.opi A 0 75 75 multistate_symbol 
./auto-generate-OPI boolean_symbol.opi A 0 75 75 boolean_symbol
./auto-generate-OPI gauge.opi A 0 75 75 gauge


outpfileName - better is it end with .opi
iocName - A, B, C or D
startingPV - count added after the IOC name- TEST-BOY1:rndmTEST-BOY1:rndmAx519
widgetHeight
widgetWidth
widgetType - gauge, textUpdate, boolean symbol, multistate symbol currently supported.
