package org.csstudio.utility.toolbox.framework.template;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.Validate;
import org.csstudio.utility.toolbox.common.Dialogs;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.framework.ColumnCreator;
import org.csstudio.utility.toolbox.framework.GenericTableViewProvider;
import org.csstudio.utility.toolbox.framework.SearchTermCollector;
import org.csstudio.utility.toolbox.framework.TestHelper;
import org.csstudio.utility.toolbox.framework.WidgetFactory;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.builder.Binder;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.controller.SearchController;
import org.csstudio.utility.toolbox.framework.editor.DataChangeSupport;
import org.csstudio.utility.toolbox.framework.editor.EditorMode;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.func.Func2;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPage;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractGuiFormTemplate<T extends BindingEntity> implements TestHelper<T>, DataChangeSupport {

   public static final String CREATE_NEW_FROM_SEARCH_BUTTON = "createNewFromSearchButton";
   public static final String CREATE_NEW_BUTTON = "createNewButton";
   public static final String CREATE_COPY_BUTTON = "createCopyButton";

   public static final String OPEN_LISTENER = "OPEN_LISTENER";

   public static final Property SEARCH_RESULT_TABLE_VIEWER = new Property("searchResultTableViewer");

   @Inject
   protected WidgetFactory<T> wf;

   @Inject
   private SearchTermCollector<T> searchTermCollector;

   @Inject
   private Provider<IWorkbenchPage> pageProvider;

   @Inject
   private Binder<T> binder;

   @Inject
   private DataBindingContext ctx = new DataBindingContext();

   private Option<SearchEventListener> searchEventListener = new None<SearchEventListener>();

   private EditorMode editorMode;

   private TableViewer searchResultTableViewer = null;

   private TableViewer detailTableViewer;

   private Option<CrudController<T>> crudController;

   private Widget focusWidget = null;

   private GenericEditorInput<T> editorInput;

   protected abstract void createEditComposite(Composite composite);

   protected abstract void createSearchComposite(Composite composite);

   protected abstract TableViewer createSearchResultComposite(Composite composite);

   public void createEditPart(Composite composite, GenericEditorInput<T> editorInput,
         final CrudController<T> crudController) {

      Validate.notNull(composite, "composite must not be null");
      Validate.notNull(editorInput, "input must not be null");
      Validate.notNull(crudController, "crudController must not be null");

      Validate.notNull(composite, "Composite must not be null");
      Validate.notNull(editorInput, "Input must not be null");

      this.editorInput = editorInput;
      this.editorMode = EditorMode.CREATE;
      this.crudController = new Some<CrudController<T>>(crudController);

      binder.init(editorInput, this.crudController, isSearchMode());
      wf.init(editorInput, new Some<CrudController<T>>(crudController), isSearchMode(), this.binder);

      createEditComposite(composite);

      Composite buttonsComposite = new Composite(composite, SWT.NONE);
      buttonsComposite.setLayout(new MigLayout("ins 0", "", ""));
      buttonsComposite.setLayoutData("ax right, spanx 7, h 45!, gaptop 10");

      wf.button(buttonsComposite, "delete").hint("w 130!, h 29!").text("Delete")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  crudController.delete();
               }
            }).build();

      if (canCreateButton(new Property(CREATE_COPY_BUTTON))) {

         wf.button(buttonsComposite, "copyNew").hint("w 130!, h 29!").text("Copy")
               .listener(new SimpleSelectionListener() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                     crudController.copy();
                  }
               }).build();
      }

      if (canCreateButton(new Property(CREATE_NEW_BUTTON))) {
         wf.button(buttonsComposite, "CREATE_NEW_BUTTON").hint("w 130!, h 29!").text("Create New")
               .listener(new SimpleSelectionListener() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                     crudController.create();
                  }
               }).build();
      }

      Button save = wf.button(buttonsComposite, "save").hint("gapleft 7, w 130!, h 29!").text("Save").build();

      Button saveAndClose = wf.button(buttonsComposite, "saveAndClose").hint("gapleft 7, w 130!, h 29!")
            .text("Save and Close").build();

      save.addSelectionListener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (searchEventListener.hasValue()) {
               throw new IllegalStateException("searchEventListener is not used for a crudtemplate");
            }
            CanSaveAction canSaveAction = canSave();
            Environment.setLastValidationFailed(false);
            if ((canSaveAction == CanSaveAction.ABORT_SAVE) || (canSaveAction == CanSaveAction.SAVE_HANDLED)) {
               return;
            }
            if (canSaveAction == CanSaveAction.CONTINUE) {
               if (crudController.isValid()) {
                  binder.updateModels();
                  crudController.save();
                  saveComplete();
               } else {
                  crudController.markErrors();
                  Environment.setLastValidationFailed(true);
                  if (!Environment.isTestMode()) {
                     Dialogs.message("Error", "Validation failed. Please check your input.");
                  }
               }
            }
         }
      });

      saveAndClose.addSelectionListener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (searchEventListener.hasValue()) {
               throw new IllegalStateException("searchEventListener is not used for a crudtemplate");
            }
            CanSaveAction canSaveAction = canSave();
            Environment.setLastValidationFailed(false);
            if (canSaveAction == CanSaveAction.ABORT_SAVE) {
               return;
            }
            if (canSaveAction == CanSaveAction.SAVE_HANDLED) {
               IWorkbenchPage page = pageProvider.get();
               page.closeEditor(page.getActiveEditor(), false);
               return;
            }
            if (canSaveAction == CanSaveAction.CONTINUE) {
               if (crudController.isValid()) {
                  binder.updateModels();
                  crudController.save();
                  saveComplete();
                  IWorkbenchPage page = pageProvider.get();
                  page.closeEditor(page.getActiveEditor(), false);
               } else {
                  crudController.markErrors();
                  Environment.setLastValidationFailed(true);
                  if (!Environment.isTestMode()) {
                     Dialogs.message("Error", "Validation failed. Please check your input.");
                  }
               }
            }
         }
      });

   }

   public void createSearchPart(final Composite composite, final GenericEditorInput<T> editorInput,
         final SearchController<T> searchController) {

      Validate.notNull(composite, "composite must not be null");
      Validate.notNull(editorInput, "input must not be null");
      Validate.notNull(searchController, "searchController must not be null");

      this.editorInput = editorInput;

      editorMode = EditorMode.SEARCH;

      this.crudController = new None<CrudController<T>>();

      binder.init(editorInput, crudController, isSearchMode());
      wf.init(editorInput, new None<CrudController<T>>(), isSearchMode(), binder);

      createSearchComposite(composite);

      Composite buttonsComposite = new Composite(composite, SWT.NONE);
      buttonsComposite.setLayout(new MigLayout("ins 0", "", ""));
      buttonsComposite.setLayoutData("ax right, spanx 7,  split 2,h 30!, gaptop 7, wrap");

      if (canCreateButton(new Property(CREATE_NEW_FROM_SEARCH_BUTTON))) {
         wf.button(buttonsComposite, CREATE_NEW_FROM_SEARCH_BUTTON).hint("w 130!, h 29!").text("Create new")
               .listener(new SimpleSelectionListener() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                     searchController.create();
                  }
               }).build();
      }

      wf.button(buttonsComposite, "clearForm").hint("w 130!, h 29!").text("Clear form")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  clearForm();
               }
            }).build();

      wf.button(buttonsComposite, "executeSearch").hint("w 130!, h 29!").text("Execute search")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  List<SearchTerm> searchTerms = searchTermCollector.collect(wf);
                  if (searchEventListener.hasValue()) {
                     searchEventListener.get().beforeExecuteSearch(searchTerms);
                  }
                  searchController.executeSearch(searchTerms);
                  if (searchEventListener.hasValue()) {
                     searchEventListener.get().afterExecuteSearch(searchTerms);
                  }
               }
            }).build();

      wf.label(composite).text("Search Result").fontSize(15).hint("wrap, gapbottom 6").build();

      final TableViewer viewer = createSearchResultComposite(composite);

      IOpenListener openListener = new IOpenListener() {
         @SuppressWarnings("unchecked")
         @Override
         public void open(OpenEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            searchController.openRow((T) selection.getFirstElement());
         }

      };

      viewer.addOpenListener(openListener);
      viewer.setData(OPEN_LISTENER, openListener);

      if ((editorMode == EditorMode.SEARCH) && editorInput.hasGoBack()) {

         Composite selectComposite = new Composite(composite, SWT.NONE);
         selectComposite.setLayout(new MigLayout("ins 0", "", ""));
         selectComposite.setLayoutData("ax right, spanx 7,  h 45!, gaptop 20, wrap");

         wf.button(selectComposite, "select").hint("w 130!, h 29!, gapbottom 20, wrap").text("Choose value")
               .listener(new SimpleSelectionListener() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                     IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                     if (!selection.isEmpty()) {
                        IWorkbenchPage page = pageProvider.get();
                        page.closeEditor(page.getActiveEditor(), false);
                        editorInput.executeGoBack(selection);
                     }
                  }
               }).build();
      }

      if ((editorMode == EditorMode.SEARCH) && !editorInput.hasGoBack()) {
         createAdditionalSearchResultButtons(composite, viewer);
      }

   }

   public DataBindingContext getCtx() {
      return ctx;
   }

   public GenericEditorInput<T> getEditorInput() {
      return editorInput;
   }

   protected boolean canCreateButton(Property propertName) {
      return true;
   }

   protected void createAdditionalSearchResultButtons(Composite composite, TableViewer viewer) {
      // for customization purposes
   }

   protected CanSaveAction canSave() {
      // for customization purposes
      return CanSaveAction.CONTINUE;
   }

   protected void saveComplete() {
      // for customization purposes
   }

   protected boolean searchCellUpdate(ViewerCell cell, String text) {
      return false;
   }

   protected boolean isSearchMode() {
      return editorMode == EditorMode.SEARCH;
   }

   protected boolean isCreateMode() {
      return editorMode == EditorMode.CREATE;
   }

   protected void markErrors(Set<ConstraintViolation<T>> violations) {

      Validate.notNull(violations, "violations must not be null");

      for (ConstraintViolation<T> violation : violations) {
         if (!wf.markError(violation.getPropertyPath(), violation.getMessage())) {
            Option<Property> resolvedPath = resolvePropertyPath(violation.getPropertyPath());
            if (resolvedPath.hasValue()) {
               String errorSrc = violation.getPropertyPath().toString();
               wf.markError(resolvedPath.get(), errorSrc + " : " + violation.getMessage());
            }
         }
      }
   }

   protected Option<Property> resolvePropertyPath(Path propertyPath) {
      return new None<Property>();
   }

   protected void resetErrorMarkers() {
      wf.resetErrorMarkers();
   }

   protected void clearForm() {
      wf.clearWidgetContent();
   }

   protected Option<CrudController<T>> getCrudController() {
      return crudController;
   }

   protected TableViewer getDetailTableViewer() {
      return detailTableViewer;
   }

   protected void setDetailTableViewer(TableViewer detailTableViewer) {
      this.detailTableViewer = detailTableViewer;
   }

   protected TableViewer getSearchResultTableViewer() {
      return searchResultTableViewer;
   }

   protected void setSearchResultTableViewer(TableViewer searchResultTableViewer) {
      this.searchResultTableViewer = searchResultTableViewer;
   }

   public WidgetFactory<T> getWidgetFactory() {
      return wf;
   }

   public Widget getFocusWidget() {
      return focusWidget;
   }

   protected void setFocusWidget(Widget focusWidget) {
      this.focusWidget = focusWidget;
   }

   public void setSearchEventListener(SearchEventListener el) {
      searchEventListener = new Some<SearchEventListener>(el);
   }

   public void createSearchResultTableView(GenericTableViewProvider<T> tableViewProvider, List<T> data,
         List<Property> properties) {
      if (searchResultTableViewer == null) {
         throw new IllegalStateException("searchResultTableViewer must not be null");
      }

      searchResultTableViewer.setContentProvider(tableViewProvider.createStructuredContentProvider(data));
      searchResultTableViewer.setLabelProvider(tableViewProvider.createTableLableProvider(properties,
            new Func2<Boolean, ViewerCell, String>() {
               @Override
               public Boolean apply(ViewerCell viewerCell, String text) {
                  return searchCellUpdate(viewerCell, text);
               }
            }));
      searchResultTableViewer.setInput(data);
      searchResultTableViewer.refresh();
   }

   protected TableViewer createTableViewer(Composite composite, Property property, String[] titles, int[] bounds) {
      return createTableViewer(composite, property, titles, bounds, new ColumnCreator() {
         @Override
         public TableViewerColumn create(TableViewer tableViewer, int colNumber) {
            return new TableViewerColumn(tableViewer, SWT.NONE);
         }
      });
   }

   protected TableViewer createTableViewer(Composite composite, Property property, String[] titles, int[] bounds,
         ColumnCreator columnCreator) {
      TableViewer tableViewer = wf.tableViewer(composite, property.getName()).build();
      createTableViewerWithLayout(tableViewer, titles, bounds, columnCreator);
      return tableViewer;

   }

   protected TableViewer createEditableTableViewer(Composite composite, Property property, String[] titles,
         int[] bounds, ColumnCreator columnCreator) {
      TableViewer tableViewer = wf.tableViewer(composite, property.getName()).enableEditingSupport().build();
      createTableViewerWithLayout(tableViewer, titles, bounds, columnCreator);
      return tableViewer;
   }

   private void createTableViewerWithLayout(TableViewer tableViewer, String[] titles, int[] bounds,
         ColumnCreator columnCreator) {

      TableLayout tableLayout = new TableLayout();

      for (int i = 0; i < bounds.length; i++) {
         tableLayout.addColumnData(new ColumnWeightData(bounds[i], true));
      }

      tableViewer.getTable().setLayout(tableLayout);

      for (int i = 0; i < titles.length; i++) {
         createTableViewerColumn(tableViewer, titles[i], i, columnCreator);
      }
   }

   private TableViewerColumn createTableViewerColumn(TableViewer tableViewer, String title, final int colNumber,
         ColumnCreator columnCreator) {
      final TableViewerColumn viewerColumn = columnCreator.create(tableViewer, colNumber);
      final TableColumn column = viewerColumn.getColumn();
      column.setText(title);
      return viewerColumn;
   }

}