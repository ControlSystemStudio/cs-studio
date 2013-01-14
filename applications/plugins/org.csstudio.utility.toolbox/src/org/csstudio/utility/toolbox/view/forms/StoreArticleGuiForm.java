package org.csstudio.utility.toolbox.view.forms;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.common.Dialogs;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.template.AbstractGuiFormTemplate;
import org.csstudio.utility.toolbox.framework.template.CanSaveAction;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.services.LagerArtikelService;
import org.csstudio.utility.toolbox.services.LagerService;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class StoreArticleGuiForm extends AbstractGuiFormTemplate<LagerArtikel> {

   @Inject
   private StoreArticleGuiFormActionHandler storeArticleGuiFormActionHandler;

   @Inject
   private LagerService lagerService;

   @Inject
   private LagerArtikelService lagerArtikelService;

   @Inject
   private LookupDataAutoCreator lookupDataAutoCreator;

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

      String[] titles = { "Stock-Article-ID", "Description", "Actual", "Target Inventory" };
      int[] bounds = { 30, 50, 10, 10 };

      setSearchResultTableViewer(createTableViewer(composite, SEARCH_RESULT_TABLE_VIEWER, titles, bounds));

      final Table table = getSearchResultTableViewer().getTable();

      table.setLayoutData("spanx 7, ay top, growy, growx, height 250:259:1250, width 500:750:2000, wrap");
      table.setHeaderVisible(true);
      table.setLinesVisible(true);

      return getSearchResultTableViewer();

   }

   @Override
   protected CanSaveAction canSave() {
      final Option<ArticleDescription> selectedArticleDescription = storeArticleGuiFormActionHandler
            .getSelectedArticleDescription();

      if (getEditorInput().isNewData()) {
         String id = wf.getText(P("id"));
         if (StringUtils.isNotEmpty(id)) {
            Option<LagerArtikel> order = lagerArtikelService.findById(id);
            if (order.hasValue()) {
               Dialogs.message("Error", "Store-ID " + id + " already exists.");
               return CanSaveAction.ABORT_SAVE;
            }
         }
      }

      getEditorInput().processData(new Func1Void<LagerArtikel>() {
         @Override
         public void apply(LagerArtikel lagerArtikel) {
            if (selectedArticleDescription.hasValue()) {
               lagerArtikel.setArticleDescription(selectedArticleDescription.get());
            }
            lookupDataAutoCreator.autoCreateLocation(lagerArtikel.getLagerName(), lagerArtikel.getOrt());
            lookupDataAutoCreator.autoCreateShelf(lagerArtikel.getLagerName(), lagerArtikel.getFach());
            lookupDataAutoCreator.autoCreateBox(lagerArtikel.getLagerName(), lagerArtikel.getBox());
         }
      });

      return super.canSave();
   }

   @Override
   protected boolean searchCellUpdate(ViewerCell cell, String text) {
      if (StringUtils.isEmpty(text)) {
         cell.setText("");
         return true;
      } else {
         if (cell.getColumnIndex() > 1) {
            try {
               String sollBestand = BeanUtils.getProperty(cell.getElement(), "sollBestand");
               String actualBestand = BeanUtils.getProperty(cell.getElement(), "actualBestand");

               if (StringUtils.isNotEmpty(sollBestand) && StringUtils.isNotEmpty(actualBestand)) {
                  if ((new BigDecimal(sollBestand)).intValue() != (new BigDecimal(actualBestand)).intValue()) {
                     cell.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                     cell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                     cell.setText(text);
                     return true;
                  }
               }
            } catch (IllegalAccessException e) {
               throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
               throw new IllegalStateException(e);
            } catch (NoSuchMethodException e) {
               throw new IllegalStateException(e);
            }
         }
      }
      return false;
   }

   private void createPart(Composite composite) {

      storeArticleGuiFormActionHandler.init(isSearchMode(), getEditorInput(), wf);

      String rowLayout;

      if (isSearchMode()) {
         rowLayout = "[][][][][][][][][][]12[][][][grow, fill]";
      } else {
         rowLayout = "[][][][][][][][][][]12[fill][]";
      }

      MigLayout ml = new MigLayout("ins 10, gapy 4, wrap 2", "[90][300, fill][grow]", rowLayout);

      composite.setLayout(ml);

      wf.label(composite).text(getEditorInput().getTitle()).titleStyle().build();

      Combo lagerName = wf.combo(composite, "lagerName").label("Stock name:").data(lagerService.findAll())
            .hint("w 250!, ay top, wrap").build();

      lagerName.addSelectionListener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            storeArticleGuiFormActionHandler.lagerNameSelected();
         }
      });

      wf.text(composite, "id").label("Stock-Article-ID:").build();

      wf.text(composite, "ort").label("Stock location:").emptyData().build();

      wf.text(composite, "fach").label("Stock shelf:").emptyData().build();

      wf.text(composite, "box").label("Stock box:").emptyData().build();

      wf.text(composite, "sollBestand").label("Min quantity:").limitInputToDigits().useBigDecimalConverter()
            .hint("split 2").build();

      wf.label(composite).text("pieces").hint("ax left, wrap").build();

      final Text beschreibung;

      if (isSearchMode()) {
         // @formatter:off
         beschreibung = wf.text(composite, "beschreibung")
               .label("Description:", "gaptop 5, wrap")
               .multiLine()
               .hint("h 50!, spanx 7, growx, wrap")
               .isJoinedForSearch()
               .build();
               // @formatter:on
      } else {
         // @formatter:off
         beschreibung = wf.text(composite, "beschreibung")
               .label("Description:", "gaptop 5, wrap")
               .multiLine()
               .hint("h 50!, spanx 7, growx, wrap")
               .noBinding()
               .readOnly()
               .build();
               // @formatter:on
      }

      wf.button(composite, "selectArticleDescription").hint("w 130!, gapbottom 5, ay top, wrap").text("Select Article")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  storeArticleGuiFormActionHandler.selectArticle(beschreibung);
               }
            }).build();

      wf.text(composite, "note").label("Article note:", "wrap").multiLine().hint("h 60!, spanx 7, growx, wrap").build();

      wf.notifyListenersWithSelectionEvent(P("lagerName"));

      getEditorInput().processData(new Func1Void<LagerArtikel>() {
         @Override
         public void apply(LagerArtikel lagerArtikel) {
            beschreibung.setText(lagerArtikel.getBeschreibung());
         }
      });

   }
}
