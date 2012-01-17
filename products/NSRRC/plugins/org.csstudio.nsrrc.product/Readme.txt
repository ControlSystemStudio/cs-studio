Example for an NSRRC Product
============================

Originally based on the 'Basic EPICS' product,
but including preferences for NSRRC as well as
alarm GUI and (dummy) NSRRC logbook support.



Exporting the product:

Open CSS.product, use the "Eclipse Product Export Wizard"
to export it into for example /opt/CSSAP/bin/NSRRC_CSS.

Remember to delete that target directory before exporting
an updated version!

Finally, patch the generated product:

To configuration/config.ini, add a line

osgi.instance.area.default=@user.home/CSS-Workspaces/Default


