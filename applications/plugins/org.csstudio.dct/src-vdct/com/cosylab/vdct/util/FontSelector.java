package com.cosylab.vdct.util;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FontSelector extends JPanel
{
	private static final String nullString = "";
	private static final String exampleText = "The quick brown fox jumps over a lazy dog.";
	private static final String plainStyleString = "Plain";
	private static final String boldStyleString = "Bold";
	private static final String italicStyleString = "Italic";
	private static final String boldAndItalicStyleString = "Bold&Italic";
	

    FontDisplayPanel displayPanel;
    JComboBox fontsBox, fontStylesBox, fontSizesBox;

    String[] fontStyleLabels = {plainStyleString, boldStyleString, italicStyleString, boldAndItalicStyleString};
    int BOLDITALIC = Font.BOLD|Font.ITALIC;
    int[] fontStyles = {Font.PLAIN, Font.BOLD, Font.ITALIC, BOLDITALIC};
    String[] fontSizeLabels = { "8", "9", 
                               "10", "11",
                               "12", "14",
                               "18", "20",
                               "22", "24",
                               "26", "28",
                               "36", "48", 
                               "72"};
                               
	public FontSelector()
	{
		super();
		
		init();
	}

    public void init() {

        // 2. Create a display panel and add it to the applet ....

        displayPanel = new FontDisplayPanel();

		setLayout(new GridLayout(2, 1));

        // 3. Create a control panel with titled border

        JPanel controlPanel = new JPanel();

        add(BorderLayout.NORTH, controlPanel);

        // 4. Create combo boxes for font names, styles, and sizes

        fontsBox = new JComboBox(displayPanel.fontFamilyNames);
        fontsBox.setSelectedItem("Arial"); // default selection
        fontsBox.addActionListener(new ComboBoxListener());

        fontStylesBox = new JComboBox(fontStyleLabels);
        fontStylesBox.addActionListener(new ComboBoxListener());

        fontSizesBox = new JComboBox(fontSizeLabels);
        fontSizesBox.setSelectedItem("12");
        fontSizesBox.addActionListener(new ComboBoxListener());

        // 5. Add the combo boxes to the control panel and add the
        //    panel to the main frame.

        controlPanel.add(fontsBox);
        controlPanel.add(fontStylesBox);
        controlPanel.add(fontSizesBox);

        displayPanel = new FontDisplayPanel();
		JScrollPane scrollPane = new JScrollPane(displayPanel);

        add(BorderLayout.NORTH, controlPanel);
        add(scrollPane);
    }

    // 6. Combo box listener to handle font name, style and size
    //    selections in the respective combo boxes.

    class ComboBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox tempBox = (JComboBox) e.getSource();

            if (tempBox.equals(fontsBox)) {
                displayPanel.fontFamilyName =
                             (String) tempBox.getSelectedItem();
                displayPanel.repaint();
            }
            else if (tempBox.equals(fontStylesBox)) {
                displayPanel.fontStyle =
                             fontStyles[tempBox.getSelectedIndex()];
                displayPanel.repaint();
            }
            else if (tempBox.equals(fontSizesBox)) {
                displayPanel.fontSize =
                             Integer.parseInt((String) tempBox.getSelectedItem());
                displayPanel.repaint();
            }
        }
    }

    // 7. Definition of display panel

    public class FontDisplayPanel extends JPanel {
        String displayText;
        Font currentFont;
        String fontFamilyName;
        int fontStyle;
        int fontSize;

        GraphicsEnvironment ge;
        String[] fontFamilyNames;
        
        // 8. Constructor for display panel....

        public FontDisplayPanel() {

            // Logo to be displayed on the panel

            displayText = exampleText;
            fontFamilyName = "Arial";
            fontStyle = Font.PLAIN;
            fontSize = 12;
            
            // Retrieve all the font family names from the platform

//            System.out.println("Loading Fonts... Please Wait...");
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontFamilyNames = ge.getAvailableFontFamilyNames();
            setBackground(Color.white);  // For canvas background color
        }

        // 9. The update method...

        public void update(Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            paintComponent(g);
        }

        // 10. The painting method...

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 11. Create the graphics context object

            Graphics2D g2D = (Graphics2D) g;
			//Graphics g2D = g;

            // 12. Prepare the current font and apply it to
            //     the display text.

            currentFont = new Font(fontFamilyName,
                                   fontStyle,
                                   fontSize);
            g2D.setFont(currentFont);
            
            g2D.setColor(getForeground());
           
			Dimension dimension = getSize();
			
			if(displayText.compareTo(nullString) == 0)
				displayText = exampleText;

            g2D.drawString(displayText, 16, dimension.height - 16);
        }
        
		public void setDisplayText(String parDisplayText)
		{
			displayText = parDisplayText;
		}
    }

	public void setFont(Font parFont)
	{
		super.setFont(parFont);

		if(fontsBox != null)
			fontsBox.setSelectedItem(parFont.getFamily());

		if(fontStylesBox != null)
		{
			int style = parFont.getStyle();
			
			if(style == Font.PLAIN)
		    	fontStylesBox.setSelectedItem(plainStyleString);

			else if(style == Font.BOLD)
		    	fontStylesBox.setSelectedItem(boldStyleString);

			else if(style == Font.ITALIC)
		    	fontStylesBox.setSelectedItem(italicStyleString);

			else if(style == BOLDITALIC)
		    	fontStylesBox.setSelectedItem(boldAndItalicStyleString);
		}

		if(fontSizesBox != null)
		    fontSizesBox.setSelectedItem(String.valueOf(parFont.getSize()));
			
		if(displayPanel != null)
		{
			displayPanel.currentFont = parFont;
       	
			displayPanel.fontFamilyName = parFont.getFamily();
			displayPanel.fontStyle = parFont.getStyle();
			displayPanel.fontSize = parFont.getSize();
		}
	}
        
	public Font getFont()
	{
		if(displayPanel != null)
			return displayPanel.currentFont;

		return super.getFont();
	}

	public FontDisplayPanel getDisplayPanel()
	{
		return displayPanel;
	}
}
