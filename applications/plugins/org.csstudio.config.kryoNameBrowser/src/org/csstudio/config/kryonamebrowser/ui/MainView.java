package org.csstudio.config.kryonamebrowser.ui;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * View for combo boxes to select name components, table with all
 * kryo names, buttons for save, delete...
 * 
 * @author jhatje
 *
 */
public class MainView extends ViewPart {

    public static final String ID = MainView.class.getName();
	
    /** Composite for combo boxes with kryo name parts */
    Composite compositeKryoNameParts;
    
    /** Composite for name field and control buttons */
    Composite compositeNameField;
    
    /** Composite for table with kryo names */
    Composite compositeNameTable;
    
    /** Combo box for plant name */
    ComboViewer comboPlantName;
    
    /** Combo box for sub 1 plant name */
    ComboViewer comboSub1PlantName;
    
    /** Combo box for sub 2 plant name */
    ComboViewer comboSub2PlantName;
    
    /** Combo box for sub 3 plant name */
    ComboViewer comboSub3PlantName;
    
    /** Text field for plant number */
    Text textPlantNumber; 
    
    /** Text field for sub 1 plant number */
    Text textSub1PlantNumber; 
    
    /** Text field for sub 2 plant number */
    Text textSub2PlantNumber; 
    
    /** Text field for sub 3 plant number */
    Text textSub3PlantNumber; 
    
	public MainView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		createCompositeKryoNameLayout(parent);
	}

	private void createCompositeKryoNameLayout(Composite parent) {
		compositeKryoNameParts = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		compositeKryoNameParts.setLayout(rowLayout);
		FillLayout fillLayout = new FillLayout();
	    fillLayout.type = SWT.VERTICAL;
	    
	    Composite compositePlantName = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositePlantName.setLayout(fillLayout);
	    Label labelPlantTitle = new Label(compositePlantName, SWT.NONE);
	    labelPlantTitle.setText("Plant Name");
		comboPlantName = new ComboViewer(compositePlantName);
	    
		Composite compositePlantNameNo = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositePlantNameNo.setLayout(fillLayout);
		compositePlantNameNo.setLayoutData(new RowData(28, 42));
		Label labelPlantNoTitle = new Label(compositePlantNameNo, SWT.NONE);
	    labelPlantNoTitle.setText("No.");
		textPlantNumber = new Text(compositePlantNameNo, SWT.BORDER);
	    
		Composite compositeSub1PlantName = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositeSub1PlantName.setLayout(fillLayout);
	    Label labelSub1PlantTitle = new Label(compositeSub1PlantName, SWT.NONE);
	    labelSub1PlantTitle.setText("Sub Plant 1");
		comboSub1PlantName = new ComboViewer(compositeSub1PlantName);
		
		Composite compositeSub1PlantNameNo = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositeSub1PlantNameNo.setLayout(fillLayout);
		compositeSub1PlantNameNo.setLayoutData(new RowData(28, 42));
	    Label labelSub1PlantNoTitle = new Label(compositeSub1PlantNameNo, SWT.NONE);
	    labelSub1PlantNoTitle.setText("No.");
		textSub1PlantNumber = new Text(compositeSub1PlantNameNo, SWT.BORDER);
		
		Composite compositeSub2PlantName = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositeSub2PlantName.setLayout(fillLayout);
	    Label labelSub2PlantTitle = new Label(compositeSub2PlantName, SWT.NONE);
	    labelSub2PlantTitle.setText("Sub Plant 2");
		comboSub2PlantName = new ComboViewer(compositeSub2PlantName);

		Composite compositeSub2PlantNameNo = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositeSub2PlantNameNo.setLayout(fillLayout);
		compositeSub2PlantNameNo.setLayoutData(new RowData(28, 42));
	    Label labelSub2PlantNoTitle = new Label(compositeSub2PlantNameNo, SWT.NONE);
	    labelSub2PlantNoTitle.setText("No.");
	    textSub2PlantNumber = new Text(compositeSub2PlantNameNo, SWT.BORDER);
	    
	    Composite compositeSub3PlantName = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositeSub3PlantName.setLayout(fillLayout);
	    Label labelSub3PlantTitle = new Label(compositeSub3PlantName, SWT.NONE);
	    labelSub3PlantTitle.setText("Sub Plant 3");
		comboSub3PlantName = new ComboViewer(compositeSub3PlantName);

		Composite compositeSub3PlantNameNo = new Composite(compositeKryoNameParts, SWT.NONE);
	    compositeSub3PlantNameNo.setLayout(fillLayout);
		compositeSub3PlantNameNo.setLayoutData(new RowData(28, 42));
	    Label labelSub3PlantNoTitle = new Label(compositeSub3PlantNameNo, SWT.NONE);
	    labelSub3PlantNoTitle.setText("No.");
		textSub3PlantNumber = new Text(compositeSub3PlantNameNo, SWT.BORDER);

		compositeKryoNameParts.pack();
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
