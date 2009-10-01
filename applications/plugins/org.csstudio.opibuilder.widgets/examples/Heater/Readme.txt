Example for Water Tank Heater as used in some EPICS
Database introductions.

Contents of the Heater folder must appear in a top-level
Project "Heater". Otherwise, links to images and scripts
need to be adjusted.

Requires an EPICS base installation with a 'softIoc'
command to execute the EPICS database files like this:

  softIoc -m user=test -s -d tank.db -d control.db