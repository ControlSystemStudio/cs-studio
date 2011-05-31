/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder;

/** Placeholder for 'TO DO' items
 *  @author Kay Kasemir
 */
public interface TODO
{
    /**
    
 
     From Kay
     
     TODO Externalize GUI Strings, provide translations
     
     From BNL, Ralph Lange v2 Feb 25, 2010 
    Issues found by playing around, trying to recreate some example panels originally created for edm and 
    medm. Tentative importance ratings 1 (minor issue) – 5 (show stopper). 
    The plot widgets were not tested. (I have no experience with plots in panels.) 
    
    ** Runtime **
    TODO Visibility Rules 5 
    Currently the visibility attribute is configurable, but static. 
    Calculated visibility should be added for all widgets (i.e. in one of the base classes). 
    90% of the use cases would be solved with a CALC expression and four Pvs. 
    
    TODO Needed: Well-Defined and Documented Script API 5 
    Currently there is no API for the javascripts attached to widgets: they call widget methods directly. 
    Some properties are set through a generic setPropertyValue() method, others have to be set using 
    setLocation(), setSize() etc. Documentation is sparse except for some general hints in the example 
    scripts. 
    
    TODO Needed: Instantiation of Sub-Panels (Dynamically) 5 
    Panels for modular hardware tend to be repetitive and modular. 
    There needs to be a widget that creates a frame, and shows the content of a group widget in a 
    different .opi file in that frame, applying configurable macro replacements. This should work 
    dynamically, so that one out of a set of different groups in other .opi files is shown in the frame, 
    depending on a PV or local variable value, or a calculation result. 
    
    TODO More General Bar Widget Needed 5 
    Progress Bar and Tank are the only bar type widgets available. 
    There is a need for a general bar widget, that: 
    • shows the value as label (not limited to percentage) 
    • has a configurable direction (up, down, left, right) 
    • has (in additional to “bar”) a “marker only” mode (to e.g. show positions of steppers). 
    
    TODO Boolean Button, Boolean Switch: Modes and Actions Need to Be Decoupled 5 
    The Boolean Button and Boolean Switch widgets currently support two modes: push button, where one 
    “Click Action” can be linked to writing a value to a PV, and toggle button, with two actions (“Push 
    Action” and “Release Action”). 
    To allow for buttons that do something as long as they are pushed (e.g. “jog” buttons for motor 
    control), the Push Button mode needs an optional “Release Action”, or alternatively the widget could 
    have a configuration switch between “Click Action” and “Push/Release Action” modes – independent 
    from push vs. toggle button mode. 
    
    TODO LED: Multi-Color Mode for Alarm Severity 5 
    It should be possible to bind a LED widget to the alarm severity of a channel, so that it gets the alarm 
    color (yellow/red/white) for severities larger than 0, and (configurable) green or dark for severity = 0. 
    Also, the alarm color border does not make much sense for a LED, as it implies showing contradictory 
    information: (E.g. LED green, border red.) Multi-color mode should be the standard way of showing 
    alarm status for the LED widget. 
    
    TODO LED: Needs ENUM Support 5 
    Currently the LED widget can only show one bit of a $(pv_value). 
    For ENUM type data (e.g. mbbi/mbbo records), the LED should be able to be lit for a certain state. For 
    a more general solution, the LED could be configurable via a CALC style calculation expression that 
    compares $(pv_value) to constants. 
    
    TODO Boolean Button: Needs ENUM Support 5 
    Currently the Boolean Button widget can only set/show one bit of a $(pv_value). 
    For ENUM type data (e.g. mbbo records), the widget should be able to set a certain state (and have its 
    LED lit for that state). 
    
    TODO Spinner: Clicks are Sometimes Ignored 4 
    When using the Spinner, some clicks are ignored, and the displayed label jumps back to the previous 
    value. This behavior seems to change with the Step Increment attribute, as if the was a hold-off time of 
    Step Increment seconds. 
    
    TODO Spinner: Bad Behavior with Button Held 4 
    When holding down a button on the Spinner, the behavior is erratic. The value seems to be written in 
    regular intervals of Step Increment seconds, while the displayed label changes rapidly. 
    The time interval has to be a separate attribute, which is separately configurable. 
    
    TODO Slider: Bad Behaviour (Similar to Spinner) 4 
    When dragging a slider that uses the same PV for set and readback, it shows the same erratic jumping 
    behavior as the Spinner. 
    
    TODO Spinner: Needs to Separate Control and Readback Pvs 3 
    The Spinner should use the same separation between Control and Readback as the Slider and Scrollbar. 
    Spinner, Scrollbar: Step Increment (and Page Increment) Runtime Configurable 3 
    It should be possible to change the Step Increment of a Spinner or Scrollbar widget and the Page 
    Increment of a Scrollbar at runtime. 
    
    TODO LED: Script Interface 3 
    Scripts should be able to switch the LED, either through API calls or by writing to $(pv_value). 
    Alarm Color Borders Should Not Change Widget Size 3 
    Currently the widget size changes on transition from NO_ALARM to any other severity, if alarm 
    sensitive borders are selected. 
    The alarm border for NO_ALARM should be invisible (instead of width=0), so that the widget size is 
    constant. 
    
    TODO Bar, Tank, Gauge, Thermometer, Meter: Labels Should Use Fix Precision 3 
    Labels on these widgets use variable length precision, which makes the label numbers jump around on 
    even values, creating a misleading impression of a large value change (as the number of digits 
    changes). 
    The labels should use a fixed precision (PREC based and maybe overridden by configuration) that only 
    changes the number of shown digits when necessary to fit the space or when the value itself changes to 
    a new magnitude. 
    
    TODO Thermometer: Configurable Unit Label 3 
    Currently, the Thermometer widget does only allow Celsius or Fahrenheit as predefined temperature 
    units. 
    This does not work for cryo systems that usually use Kelvin – the unit text should either be freely 
    configurable or have Kelvin as the third option. 
    
    TODO Meter, Gauge: No Out-Of-Range Indication 3 
    The Meter and Gauge widget do not indicate an out-of-range condition, i.e. when the value to be 
    displayed is outside of [Minimum...Maximum]. 
    
    TODO Menu Button and Combo Box: Should Be Renamed 3 
    Combo Box is a misleading name. These widgets should be renamed to something more obvious, e.g. 
    “Action Menu” and “Enum Menu”. 
    
    TODO Progress Bar: Wrong Initial Value 2 
    The Progress Bar shows a wrong initial value in case of Minimum != 0. For all later value updates, the 
    value is correct. 
    
    TODO Bar, Tank, Thermometer, Slider: Swappable Position of Limits and Labels 2 
    These widgets have a fixed side on which labels and limits appear. 
    The position of labels and limits should be configurable (with a swap flag). 
    
    TODO Text Update: Tooltip Does Not Show Value 2 
    In Text Update widgets, the Tooltip does not always show the value, even though the value is being 
    correctly displayed in the widget itself. 
    
    TODO Scrollbar: Needs a Value Label 2 
    The Scrollbar needs a label to make the value visible while moving the bar, probably as a tooltip 
    similar to the Slider. 
    
    TODO Widget Label Text: Optional Border 2 
    For many widgets, the labels are placed in areas which change color. To make the labels more readable, 
    the characters of their text should have an optional border in the background color.
    
    TODO LED, Graphics Widgets: Blinking Attribute Needed 2 
    LEDs and plain graphics widgets (especially Label) should support blinking as an attribute, offering at 
    minimum two blink rates. 
    
    TODO Bar, Tank, Thermometer: Configurable Text for Limits 1 
    These widgets have a fixed texts for the limits (LOLO, LO, HI, HIHI). 
    The limits texts should be configurable. 
    
    TODO Radio Button: Missing 1 
    There is need for a Radio Button widget as an alternative way to select an ENUM state. 
    
    ** Editor **
    TODO Font and Color Selector Boxes Reset Non-Predefined Font/Color Selection 4 
    After you set the font to a non-predefined font (using the font dialog) or a color to a non-predefined 
    color (using the choose color dialog), the font or color selector boxes will always reset the font or color 
    to the first predefined when you open the choose dialog box. 
    Instead, in that case the choose dialog boxes should not highlight any of the predefined options, and the 
    dialog should always start with the widget's current values for font or color. 
    
    TODO Properties List Jumps from Cursor Position 3 
    When changing a property that implies a change of the number of properties displayed in the Properties 
    View (e.g. the Boolean Button's “Toggle Button” property), the list jumps away under the mouse cursor 
    position, so that – after the click – the mouse pointer is over a different property. 
    Instead, properties that are not editable should always be shown (greyed out), so that their position in 
    the properties list never changes. 
    
    TODO Multi-Widget Resizing: Wrong Algorithm 2 
    When selecting multiple widgets and resizing the group, the size change is applied to every member of 
    the selection, while the position is unchanged. This behavior is wrong. 
    Correct would be calculating stretch factors for both axes and move all corner points of each widget 
    according to its position within the selection, so that both the relative widget size and its relative 
    position (with regard to the selection size) remain constant throughout the resize operation. 
    
    TODO Creating Widgets with Minimal Size or Fixed Ratio 1 
    Widgets with restrictions on their size (e.g. Gauge, Meter) should enforce these when they are created 
    and/or resized.
     */
}
