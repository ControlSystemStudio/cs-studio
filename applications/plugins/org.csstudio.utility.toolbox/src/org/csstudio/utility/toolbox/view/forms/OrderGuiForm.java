package org.csstudio.utility.toolbox.view.forms;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.validation.Path;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.common.Dialogs;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.entities.OrderType;
import org.csstudio.utility.toolbox.framework.ColumnCreator;
import org.csstudio.utility.toolbox.framework.celleditors.CustomDialogCellEditor;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.csstudio.utility.toolbox.framework.template.AbstractGuiFormTemplate;
import org.csstudio.utility.toolbox.framework.template.CanSaveAction;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Func2;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.FirmaService;
import org.csstudio.utility.toolbox.services.LogGroupService;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.csstudio.utility.toolbox.services.OrderPosService;
import org.csstudio.utility.toolbox.services.OrderService;
import org.csstudio.utility.toolbox.view.support.ArticleDescriptionEditingSupport;
import org.csstudio.utility.toolbox.view.support.OrderPosBestellmengeEditingSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class OrderGuiForm extends AbstractGuiFormTemplate<Order> {

   private static final String DETAIL_TABLE_VIEWER = "detailTableViewer";

   @Inject
   private FirmaService firmaService;

   @Inject
   private LogUserService logUserService;

   @Inject
   private LogGroupService logGroupService;

   @Inject
   private OrderService orderService;

   @Inject
   private OrderPosService orderPosService;

   @Inject
   private Environment env;

   @Inject
   private Provider<SimpleDateFormat> sd;

   @Inject
   private OrderGuiFormActionHandler orderGuiFormActionHandler;
   
   protected Option<Property> resolvePropertyPath(Path propertyPath) {
      return new Some<Property>(new Property(DETAIL_TABLE_VIEWER));
   }

   @Override
   protected void createEditComposite(Composite composite) {
      createPart(composite);
   }

   @Override
   protected void createSearchComposite(Composite composite) {
      createPart(composite);
   }

   @Override
   protected TableViewer createSearchResultComposite(Composite composite) {

      String[] titles = { "BA-Numer", "Company", "Description" };
      int[] bounds = { 20, 30, 40 };

      setSearchResultTableViewer(createTableViewer(composite, SEARCH_RESULT_TABLE_VIEWER, titles, bounds));

      final Table table = getSearchResultTableViewer().getTable();

      table.setLayoutData("spanx 7, ay top, growy, growx, height 220:220:1250, width 500:800:2000, wrap");
      table.setHeaderVisible(true);
      table.setLinesVisible(true);

      return getSearchResultTableViewer();

   }

   private void createPart(final Composite composite) {

      orderGuiFormActionHandler.init(isSearchMode(), getEditorInput(), wf);

      String rowLayout;

      if (isSearchMode()) {
         rowLayout = "[][][][][][][]8[][][]8[]15[][][][grow, fill][]";
      } else {
         rowLayout = "[][][][][][][]8[][][]8[][]25[grow, fill][]";
      }

      MigLayout ml = new MigLayout("ins 10, gapy 2", "[100][200, fill][80][70][200,fill][100][fill,grow]", rowLayout);

      composite.setLayout(ml);

      wf.label(composite).text(getEditorInput().getTitle()).titleStyle().build();

      // =====================

      if (isSearchMode()) {
         wf.combo(composite, "baType").label("Order-Type:").data(OrderType.getTypeList()).hint("wrap").build();
      } else {
         wf.combo(composite, "baType").label("Order-Type:").data(OrderType.getTypeList()).selectFirst().hint("wrap")
               .build();
      }

      // =====================

      Text nummer;

      if (isSearchMode() || getEditorInput().isNewData()) {
         nummer = wf.text(composite, "nummer", SearchTermType.STRING_IGNORE_FIRST_CHAR).label("BA Number:")
               .limitInputToDigits().build();
      } else {
         nummer = wf.numericText(composite, "nummer").label("BA Number:").readOnly().noBinding().build();
         getEditorInput().processData(new Func1Void<Order>() {
            @Override
            public void apply(Order order) {
               wf.setText(P("nummer"), order.getBaNummer());
            }
         });
      }

      setFocusWidget(nummer);

      if (getCrudController().hasValue()) {
         getCrudController().get().setFocusWidget(nummer);
      }

      Text ausstellungsDatum = wf.date(composite, "austellungsDatum").label("BA Date:", "skip").hint("gaptop 5, wrap")
            .build();

      if (isCreateMode() && (getEditorInput().isNewData())) {
         ausstellungsDatum.setText(sd.get().format(new Date()));
         ausstellungsDatum.notifyListeners(SWT.FocusOut, new Event());
      }

      // =====================

      final Combo company = wf.combo(composite, "firmaName").label("Company:").data(firmaService.findAll()).build();

      wf.button(composite, "lookupCompany").withSearchImage().listener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            orderGuiFormActionHandler.selectFirma(company);
         }
      }).build();

      wf.checkbox(composite, "maintenanceContract").text("Maintenance Contract").hint("skip 1, wrap").build();

      // =====================

      final Combo previousBa = wf.combo(composite, "vorherigeBa").label("Previous BA:").data(orderService.findAll())
            .build();

      wf.button(composite, "lookupPreviousBa").withSearchImage().listener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            orderGuiFormActionHandler.selectBa(previousBa);
         }
      }).build();

      wf.date(composite, "validUntil").label("Valid until:").hint("gaptop 5, wrap").build();

      // =====================

      wf.text(composite, "zuInventarNr").label("For Inventary Nr:").build();

      wf.checkbox(composite, "rememberExpiration").text("Remember Expiration").hint("skip 2, wrap").build();

      // =====================

      final Combo aussteller = wf.combo(composite, "aussteller").label("User:").data(logUserService.findAll()).build();

      if (isCreateMode() && (getEditorInput().isNewData())) {
         aussteller.setText(env.getDefaultUserName());
      }

      wf.combo(composite, "gruppe").label("Group:", "skip").data(logGroupService.findAll()).hint("wrap").build();

      // =====================

      wf.text(composite, "abladeStelle").label("BA Deliver To:").build();

      wf.date(composite, "termin").label("Deliver Date:", "skip").hint("gaptop 5, wrap").build();

      // =====================

      wf.text(composite, "gesamtwert").label("Total Value:").build();
      wf.text(composite, "desyauftragsNr").label("Order Nr:", "skip").hint("wrap").build();

      // =====================

      wf.text(composite, "kostenstelle").label("Account:").build();
      wf.text(composite, "projekt").label("Project:", "skip").hint("wrap").build();

      // =====================

      wf.text(composite, "beschreibung").label("Description:").hint("spanx 6,wrap").build();

      if (isSearchMode()) {

         wf.text(composite, "internId").isJoinedForSearch().label("Internal Nr:").build();
         wf.text(composite, "Inventar Nr:").isJoinedForSearch().label("Inventar Nr:", "skip").hint("wrap").build();

      } else {

         wf.text(composite, "text").label("Note:", "ay top").hint("spanx 6, h 90!, wrap 2").build();

         String[] titles = { "Position", "Amount", "Description", "Price" };
         int[] bounds = { 20, 20, 40, 20 };

         setDetailTableViewer(createEditableTableViewer(composite, new Property(DETAIL_TABLE_VIEWER), titles, bounds,
               new ColumnCreator() {
                  @Override
                  public TableViewerColumn create(TableViewer tableViewer, int colNumber) {
                     return createTableViewerColumn(tableViewer, colNumber);
                  }
               }));

         getDetailTableViewer().setContentProvider(new ArrayContentProvider());

         getEditorInput().processData(new Func1Void<Order>() {
            @Override
            public void apply(Order order) {
               if (order.getOrderPositions(orderPosService) == null) {
                  order.setOrderPositions(new ArrayList<OrderPos>());
               }
               getDetailTableViewer().setInput(order.getOrderPositions(orderPosService));
            }
         });

         final Table table = getDetailTableViewer().getTable();

         table.setLayoutData("skip, spanx 6, gaptop 20, ay top, growx, height 200:200:1250, wrap");
         table.setHeaderVisible(true);
         table.setLinesVisible(true);

         MigLayout mlButtons = new MigLayout("ins 0", "[67][67]", "[]");

         Composite buttonComposite = new Composite(composite, SWT.NONE);
         buttonComposite.setLayout(mlButtons);

         buttonComposite.setLayoutData("skip, wrap");

         wf.button(buttonComposite, "addRow").hint("w 65!").text("+").listener(new SimpleSelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               orderGuiFormActionHandler.addNewDetail(getDetailTableViewer(), getCrudController());
            }
         }).build();

         wf.button(buttonComposite, "removeRow").hint("w 65!").text("-").listener(new SimpleSelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               orderGuiFormActionHandler.removeDetail(getDetailTableViewer(), getCrudController());
            }
         }).build();

      }
   }

   @Override
   protected CanSaveAction canSave() {
      if (getEditorInput().isNewData()) {
         String nummer = wf.getText(P("nummer"));
         if (StringUtils.isNotEmpty(nummer)) {
            Option<Order> order = orderService.findByNummer(new BigDecimal(nummer));
            if (order.hasValue()) {
               Dialogs.message("Error", "Order " + nummer + " already exists.");
               return CanSaveAction.ABORT_SAVE;
            }
            int selectionIndex = wf.getSelectionIndex(P("baType"));
            if (selectionIndex == -1) {
               Dialogs.message("Error", "You must select the order type.");
               return CanSaveAction.ABORT_SAVE;
            }
            wf.setText(P("nummer"), String.valueOf(selectionIndex + 1) + nummer);
         }
      }
      return CanSaveAction.CONTINUE;
   }

   @Override
   protected void saveComplete() {
      wf.setReadOnly(P("nummer"));
      getEditorInput().processData(new Func1Void<Order>() {
         @Override
         public void apply(Order order) {
            wf.setText(P("nummer"), order.getBaNummer());
            getCrudController().get().setDirty(false);
         }
      });
   }

   private TableViewerColumn createTableViewerColumn(TableViewer tableViewer, int colNumber) {
      if (isSearchMode()) {
         return new TableViewerColumn(tableViewer, SWT.NONE);
      }
      switch (colNumber) {
      case 0:
         return createPositionNrCol(tableViewer);
      case 1:
         return createAnzahlBestelltCol(tableViewer);
      case 2:
         return createBeschreibungCol(tableViewer);
      case 3:
         return creatEinzelPreisCol(tableViewer);
      default:
         throw new IllegalStateException("Unsupported column index");
      }
   }

   private static class PositionNrLabelProvider extends ColumnLabelProvider {
      @Override
      public String getText(Object element) {
         OrderPos p = (OrderPos) element;
         return p.getPositionNr().toString();
      }
   }

   private TableViewerColumn createPositionNrCol(TableViewer tableViewer) {
      TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
      col.setLabelProvider(new PositionNrLabelProvider());
      return col;
   }

   private static class AnzahlBestelltLabelProvider extends ColumnLabelProvider {
      @Override
      public String getText(Object element) {
         OrderPos p = (OrderPos) element;
         return p.getAnzahlBestellt().toString();
      }
   }

   private TableViewerColumn createAnzahlBestelltCol(TableViewer tableViewer) {
      TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
      col.setLabelProvider(new AnzahlBestelltLabelProvider());
      col.setEditingSupport(new OrderPosBestellmengeEditingSupport(tableViewer));
      return col;
   }

   private static class BeschreibungLabelProvider extends ColumnLabelProvider {
      @Override
      public String getText(Object element) {
         OrderPos p = (OrderPos) element;
         if (p.getArticle().getArticleDescription() == null) {
            return "";
         }
         return p.getArticle().getArticleDescription().getBeschreibung();
      }
   }

   private TableViewerColumn createBeschreibungCol(TableViewer tableViewer) {
      TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
      col.setLabelProvider(new BeschreibungLabelProvider());
      Func2<Object, CustomDialogCellEditor, Control> openDialog = new Func2<Object, CustomDialogCellEditor, Control>() {
         @Override
         public Object apply(final CustomDialogCellEditor dialogCellEditor, final Control cellEditorWindow) {
            IStructuredSelection selection = (IStructuredSelection) getDetailTableViewer().getSelection();
            orderGuiFormActionHandler.selectArticleDescription(dialogCellEditor, selection);
            return null;
         }
      };
      col.setEditingSupport(new ArticleDescriptionEditingSupport(tableViewer, openDialog));
      return col;
   }

   private static class EinzelPreisLabelProvider extends ColumnLabelProvider {
      @Override
      public String getText(Object element) {
         OrderPos p = (OrderPos) element;
         if (p.getEinzelPreis() == null) {
            return "";
         }
         return p.getEinzelPreis().toString();
      }
   }

   private TableViewerColumn creatEinzelPreisCol(TableViewer tableViewer) {
      TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.RIGHT);
      col.setLabelProvider(new EinzelPreisLabelProvider());
      return col;
   }
}
