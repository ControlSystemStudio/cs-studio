package org.csstudio.askap.sb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class PersonEditor extends ApplicationWindow {
	  // Table column names/properties
	  public static final String NAME = "Name";

	  public static final String MALE = "Male?";

	  public static final String AGE = "Age Range";

	  public static final String SHIRT_COLOR = "Shirt Color";

	  public static final String[] PROPS = { NAME, MALE, AGE, SHIRT_COLOR };

	  // The data model
	  private java.util.List people;

	  /**
	   * Constructs a PersonEditor
	   */
	  public PersonEditor() {
	    super(null);
	    people = new ArrayList();
	  }

	  /**
	   * Runs the application
	   */
	  public void run() {
	    // Don't return from open() until window closes
	    setBlockOnOpen(true);

	    // Open the main window
	    open();

	    // Dispose the display
	    Display.getCurrent().dispose();
	  }

	  /**
	   * Configures the shell
	   * 
	   * @param shell
	   *            the shell
	   */
	  protected void configureShell(Shell shell) {
	    super.configureShell(shell);
	    shell.setText("Person Editor");
	    shell.setSize(400, 400);
	  }

	  /**
	   * Creates the main window's contents
	   * 
	   * @param parent
	   *            the main window
	   * @return Control
	   */
	  protected Control createContents(Composite parent) {
	    Composite composite = new Composite(parent, SWT.NONE);
	    composite.setLayout(new GridLayout(1, false));

	    // Add a button to create the new person
	    Button newPerson = new Button(composite, SWT.PUSH);
	    newPerson.setText("Create New Person");

	    // Add the TableViewer
	    final TableViewer tv = new TableViewer(composite, SWT.FULL_SELECTION);
	    tv.setContentProvider(new PersonContentProvider());
	    tv.setLabelProvider(new PersonLabelProvider());
	    tv.setInput(people);

	    // Set up the table
	    Table table = tv.getTable();
	    table.setLayoutData(new GridData(GridData.FILL_BOTH));

	    new TableColumn(table, SWT.CENTER).setText(NAME);
	    new TableColumn(table, SWT.CENTER).setText(MALE);
	    new TableColumn(table, SWT.CENTER).setText(AGE);
	    new TableColumn(table, SWT.CENTER).setText(SHIRT_COLOR);

	    for (int i = 0, n = table.getColumnCount(); i < n; i++) {
	      table.getColumn(i).pack();
	    }

	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    // Add a new person when the user clicks button
	    newPerson.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        Person p = new Person();
	        p.setName("Name");
	        p.setMale(true);
	        p.setAgeRange(Integer.valueOf("0"));
	        p.setShirtColor(new RGB(255, 0, 0));
	        people.add(p);
	        tv.refresh();
	      }
	    });

	    // Create the cell editors
	    CellEditor[] editors = new CellEditor[4];
	    editors[0] = new TextCellEditor(table);
	    editors[1] = new CheckboxCellEditor(table);
	    editors[2] = new ComboBoxCellEditor(table, AgeRange.INSTANCES,
	        SWT.READ_ONLY);
	    editors[3] = new ColorCellEditor(table);

	    // Set the editors, cell modifier, and column properties
	    tv.setColumnProperties(PROPS);
	    tv.setCellModifier(new PersonCellModifier(tv));
	    tv.setCellEditors(editors);

	    return composite;
	  }

	  /**
	   * The application entry point
	   * 
	   * @param args
	   *            the command line arguments
	   */
	  public static void main(String[] args) {
	    new PersonEditor().run();
	  }
	}

	/**
	 * This class provides the content for the person table
	 */

	class PersonContentProvider implements IStructuredContentProvider {
	  /**
	   * Returns the Person objects
	   */
	  public Object[] getElements(Object inputElement) {
	    return ((List) inputElement).toArray();
	  }

	  /**
	   * Disposes any created resources
	   */
	  public void dispose() {
	    // Do nothing
	  }

	  /**
	   * Called when the input changes
	   */
	  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    // Ignore
	  }
	}

	/**
	 * This class provides the content for the FoodList application
	 */

	class FoodContentProvider implements IStructuredContentProvider {
	  /**
	   * Gets the food items for the list
	   * 
	   * @param arg0
	   *            the data model
	   * @return Object[]
	   */
	  public Object[] getElements(Object arg0) {
	    return ((GroceryList) arg0).getFoods().toArray();
	  }

	  /**
	   * Disposes any created resources
	   */
	  public void dispose() {
	    // Do nothing
	  }

	  /**
	   * Called when the input changes
	   * 
	   * @param arg0
	   *            the viewer
	   * @param arg1
	   *            the old input
	   * @param arg2
	   *            the new input
	   */
	  public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	    // Do nothing
	  }
	}

	/**
	 * This class represents the cell modifier for the PersonEditor program
	 */

	class PersonCellModifier implements ICellModifier {
	  private Viewer viewer;

	  public PersonCellModifier(Viewer viewer) {
	    this.viewer = viewer;
	  }

	  /**
	   * Returns whether the property can be modified
	   * 
	   * @param element
	   *            the element
	   * @param property
	   *            the property
	   * @return boolean
	   */
	  public boolean canModify(Object element, String property) {
	    // Allow editing of all values
	    return true;
	  }

	  /**
	   * Returns the value for the property
	   * 
	   * @param element
	   *            the element
	   * @param property
	   *            the property
	   * @return Object
	   */
	  public Object getValue(Object element, String property) {
	    Person p = (Person) element;
	    if (PersonEditor.NAME.equals(property))
	      return p.getName();
	    else if (PersonEditor.MALE.equals(property))
	      return Boolean.valueOf(p.isMale());
	    else if (PersonEditor.AGE.equals(property))
	      return p.getAgeRange();
	    else if (PersonEditor.SHIRT_COLOR.equals(property))
	      return p.getShirtColor();
	    else
	      return null;
	  }

	  /**
	   * Modifies the element
	   * 
	   * @param element
	   *            the element
	   * @param property
	   *            the property
	   * @param value
	   *            the value
	   */
	  public void modify(Object element, String property, Object value) {
	    if (element instanceof Item)
	      element = ((Item) element).getData();

	    Person p = (Person) element;
	    if (PersonEditor.NAME.equals(property))
	      p.setName((String) value);
	    else if (PersonEditor.MALE.equals(property))
	      p.setMale(((Boolean) value).booleanValue());
	    else if (PersonEditor.AGE.equals(property))
	      p.setAgeRange((Integer) value);
	    else if (PersonEditor.SHIRT_COLOR.equals(property))
	      p.setShirtColor((RGB) value);

	    // Force the viewer to refresh
	    viewer.refresh();
	  }
	}

	/**
	 * This class represents a person
	 */

	class Person {
	  private String name;

	  private boolean male;

	  private Integer ageRange;

	  private RGB shirtColor;

	  /**
	   * @return Returns the ageRange.
	   */
	  public Integer getAgeRange() {
	    return ageRange;
	  }

	  /**
	   * @param ageRange
	   *            The ageRange to set.
	   */
	  public void setAgeRange(Integer ageRange) {
	    this.ageRange = ageRange;
	  }

	  /**
	   * @return Returns the male.
	   */
	  public boolean isMale() {
	    return male;
	  }

	  /**
	   * @param male
	   *            The male to set.
	   */
	  public void setMale(boolean male) {
	    this.male = male;
	  }

	  /**
	   * @return Returns the name.
	   */
	  public String getName() {
	    return name;
	  }

	  /**
	   * @param name
	   *            The name to set.
	   */
	  public void setName(String name) {
	    this.name = name;
	  }

	  /**
	   * @return Returns the shirtColor.
	   */
	  public RGB getShirtColor() {
	    return shirtColor;
	  }

	  /**
	   * @param shirtColor
	   *            The shirtColor to set.
	   */
	  public void setShirtColor(RGB shirtColor) {
	    this.shirtColor = shirtColor;
	  }
	}

	/**
	 * This class contains all the foods on the "grocery list"
	 */

	class GroceryList {
	  // Holds the foods
	  private List foods;

	  /**
	   * Constructs a grocery list
	   */
	  public GroceryList() {
	    foods = new ArrayList();

	    // Add some foods
	    foods.add(new Food("Broccoli", true));
	    foods.add(new Food("Bundt Cake", false));
	    foods.add(new Food("Cabbage", true));
	    foods.add(new Food("Candy Canes", false));
	    foods.add(new Food("Eggs", true));
	    foods.add(new Food("Potato Chips", false));
	    foods.add(new Food("Milk", true));
	    foods.add(new Food("Soda", false));
	    foods.add(new Food("Chicken", true));
	    foods.add(new Food("Cinnamon Rolls", false));
	  }

	  /**
	   * Returns the foods in this grocery list
	   * 
	   * @return List
	   */
	  public List getFoods() {
	    return Collections.unmodifiableList(foods);
	  }
	}

	/**
	 * This class represents a type of food
	 */

	class Food {
	  // The name of the food
	  private String name;

	  // Is it healthy?
	  private boolean healthy;

	  /**
	   * Food constructor
	   * 
	   * @param name
	   *            the name
	   * @param healthy
	   *            whether or not it's healthy
	   */
	  public Food(String name, boolean healthy) {
	    this.name = name;
	    this.healthy = healthy;
	  }

	  /**
	   * Gets whether this is healthy
	   * 
	   * @return boolean
	   */
	  public boolean isHealthy() {
	    return healthy;
	  }

	  /**
	   * Gets the name
	   * 
	   * @return String
	   */
	  public String getName() {
	    return name;
	  }
	}

	/**
	 * This class provides the labels for the person table
	 */

	class PersonLabelProvider implements ITableLabelProvider {
	  /**
	   * Returns the image
	   * 
	   * @param element
	   *            the element
	   * @param columnIndex
	   *            the column index
	   * @return Image
	   */
	  public Image getColumnImage(Object element, int columnIndex) {
	    return null;
	  }

	  /**
	   * Returns the column text
	   * 
	   * @param element
	   *            the element
	   * @param columnIndex
	   *            the column index
	   * @return String
	   */
	  public String getColumnText(Object element, int columnIndex) {
	    Person person = (Person) element;
	    switch (columnIndex) {
	    case 0:
	      return person.getName();
	    case 1:
	      return Boolean.toString(person.isMale());
	    case 2:
	      return AgeRange.INSTANCES[person.getAgeRange().intValue()];
	    case 3:
	      return person.getShirtColor().toString();
	    }
	    return null;
	  }

	  /**
	   * Adds a listener
	   * 
	   * @param listener
	   *            the listener
	   */
	  public void addListener(ILabelProviderListener listener) {
	    // Ignore it
	  }

	  /**
	   * Disposes any created resources
	   */
	  public void dispose() {
	    // Nothing to dispose
	  }

	  /**
	   * Returns whether altering this property on this element will affect the
	   * label
	   * 
	   * @param element
	   *            the element
	   * @param property
	   *            the property
	   * @return boolean
	   */
	  public boolean isLabelProperty(Object element, String property) {
	    return false;
	  }

	  /**
	   * Removes a listener
	   * 
	   * @param listener
	   *            the listener
	   */
	  public void removeListener(ILabelProviderListener listener) {
	    // Ignore
	  }
	}


	/**
	 * This class encapsulates age ranges
	 */

	class AgeRange {
	  public static final String NONE = "";

	  public static final String BABY = "0 - 3";

	  public static final String TODDLER = "4 - 7";

	  public static final String CHILD = "8 - 12";

	  public static final String TEENAGER = "13 - 19";

	  public static final String ADULT = "20 - ?";

	  public static final String[] INSTANCES = { NONE, BABY, TODDLER, CHILD,
	      TEENAGER, ADULT };
	}

	           