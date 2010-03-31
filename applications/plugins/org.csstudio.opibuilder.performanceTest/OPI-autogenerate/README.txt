Auto generate opi file.

The script will fill a standard sized display with the widgets specified.
It will try to fill the display with as many instances of the widget it can.

first
chmod a+x auto-generate-OPI

usage

./auto-genrate-OPI outputfileName startingPV widgetHeight widgetWidth widgetType

outpfileName - better is it end with .opi
startingPV - the current script is hardcoded for the PV nomenclature used for 4K test IOC, this can be easily changed byh modifying the bash script.
widgetHeight
widgetWidth
widgetType - gauge, meter, tank, textUpdate, thermometer currently supported.
