package org.csstudio.utility.toolbox.view.forms;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import net.miginfocom.swt.MigLayout;

import org.csstudio.utility.toolbox.AppLogger;
import org.csstudio.utility.toolbox.common.Dialogs;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleDelivered;
import org.csstudio.utility.toolbox.entities.ArticleInStore;
import org.csstudio.utility.toolbox.entities.ArticleInstalled;
import org.csstudio.utility.toolbox.entities.ArticleMaintenance;
import org.csstudio.utility.toolbox.entities.ArticleRented;
import org.csstudio.utility.toolbox.entities.ArticleRetired;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.editor.TransactionContext;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.csstudio.utility.toolbox.framework.template.AbstractGuiFormTemplate;
import org.csstudio.utility.toolbox.framework.template.CanSaveAction;
import org.csstudio.utility.toolbox.func.Func0Void;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.ArticleService;
import org.csstudio.utility.toolbox.services.ArticleStateService;
import org.csstudio.utility.toolbox.services.FirmaService;
import org.csstudio.utility.toolbox.services.OrderPosService;
import org.csstudio.utility.toolbox.view.forms.subviews.DeliveredView;
import org.csstudio.utility.toolbox.view.forms.subviews.InstalledView;
import org.csstudio.utility.toolbox.view.forms.subviews.MaintenanceView;
import org.csstudio.utility.toolbox.view.forms.subviews.RentedView;
import org.csstudio.utility.toolbox.view.forms.subviews.RetiredView;
import org.csstudio.utility.toolbox.view.forms.subviews.StoreView;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class ArticleGuiForm extends AbstractGuiFormTemplate<Article> implements PropertyChangeListener {

   public static final Property ITEM_COMBO = new Property("ITEM_COMBO");

   // Misuse propertyChangeEvent to inform view about state changes
   private static final String EVENT_SOURCE_ARTICLE_INIT = "articleInit";

   private static final String EVENT_SOURCE_ARTICLE_NEW = "articleNew";

   private static final String EVENT_SOURCE_ARTICLE_CHANGED = "articleChanged";

   private static final String EVENT_SOURCE_ARTICLE_STATUS = "status";

   private WritableList articlesInGroup;

   private BigDecimal articleDescriptionId;

   @Inject
   private AppLogger logger;

   @Inject
   private EntityManager em;

   @Inject
   private Validator validator;

   @Inject
   private ArticleSubViewDataProvider subViewDataProvider;

   @Inject
   private FirmaService firmaService;

   @Inject
   private ArticleStateService articleStateService;

   @Inject
   private ArticleService articleService;

   @Inject
   private OrderPosService orderPosService;

   @Inject
   private ArticleGuiFormActionHandler articleGuiFormActionHandler;

   @Inject
   private RentedView rentedView;

   @Inject
   private DeliveredView deliveredView;

   @Inject
   private RetiredView retiredView;

   @Inject
   private StoreView storeView;

   @Inject
   private InstalledView installedView;;

   @Inject
   private MaintenanceView maintenanceView;

   @Inject
   private TransactionContext transactionContext;

   private Article currentlySelectedArticle;

   private TabFolder tabFolder;

   private TabItem tabItemStatusData;

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
      String[] titles = { "Note", "Status", "Internal Nr" };
      int[] bounds = { 33, 33, 33 };
      setSearchResultTableViewer(createTableViewer(composite, SEARCH_RESULT_TABLE_VIEWER, titles, bounds));
      final Table table = getSearchResultTableViewer().getTable();
      table.setLayoutData("spanx 7, ay top, growy, growx, height 330:330:1250, width 500:800:2000, wrap");
      table.setHeaderVisible(true);
      table.setLinesVisible(true);
      return getSearchResultTableViewer();
   }

   public void selectArticle(Article article) {
      final int index = articlesInGroup.indexOf(article);
      tabFolder.setSelection(0);
      wf.select(ArticleGuiForm.ITEM_COMBO, index);
      wf.notifyListenersWithSelectionEvent(ArticleGuiForm.ITEM_COMBO);
   }

   public void dispose() {
      if (articlesInGroup != null) {
         refreshArticleGroup();
      }
      // how to remove listeners ?
      installedView.dispose();
      rentedView.dispose();
      retiredView.dispose();
      maintenanceView.dispose();
      deliveredView.dispose();
      storeView.dispose();
   }

   @Override
   protected boolean canCreateButton(Property property) {
      return !((property.getName().equalsIgnoreCase(AbstractGuiFormTemplate.CREATE_NEW_FROM_SEARCH_BUTTON) || (property
            .getName().equalsIgnoreCase(AbstractGuiFormTemplate.CREATE_NEW_BUTTON))) || (property.getName()
            .equalsIgnoreCase(AbstractGuiFormTemplate.CREATE_COPY_BUTTON)));
   }

   private void markError(Set<ConstraintViolation<Article>> validationErrors) {
      StringBuffer sb = new StringBuffer();
      for (ConstraintViolation<Article> violation : validationErrors) {
         sb.append(violation.getPropertyPath().toString()).append(" : ").append(violation.getMessage()).append("\n");
      }
      Dialogs.message("Error in Article-View", sb.toString());
   }

   private void markErrorSubView(Set<ConstraintViolation<BindingEntity>> validationErrors) {
      StringBuffer sb = new StringBuffer();
      for (ConstraintViolation<BindingEntity> violation : validationErrors) {
         sb.append(violation.getPropertyPath().toString()).append(" : ").append(violation.getMessage()).append("\n");
      }
      Dialogs.message("Error in State-View", sb.toString());
   }

   // We handle the save manually. This is necessary since we handle a list
   // of entities here. All other forms work on a single entity.
   // Therefore we always return CanSaveAction.SAVE_HANDLED here.
   @Override
   protected CanSaveAction canSave() {
      updateModelsInSubViews();
      if (!validateArticleGroup()) {
         return CanSaveAction.ABORT_SAVE;
      }
      try {
         transactionContext.doRun(new Func0Void() {
            @Override
            public void apply() {
               List<LagerArtikel> lagerArtikelList = subViewDataProvider.calculateStoreMovements();
               for (Object article : articlesInGroup) {
                  Article articleEntity = (Article) article;
                  subViewDataProvider.autoCreateLookupData(articleEntity);
                  subViewDataProvider.assignId(articleEntity);
                  em.merge(article);
               }
               // record movements in and out of the inventory
               for (LagerArtikel lagerArtikel : lagerArtikelList) {
                  em.merge(lagerArtikel);
               }
               subViewDataProvider.mergeEntities();
            }
         });
         if (getCrudController().hasValue()) {
            getCrudController().get().setDirty(false);
         } else {
            throw new IllegalStateException("Unexpected state in ArticelGuiForm.canSave");
         }
         return CanSaveAction.SAVE_HANDLED;
      } catch (Exception e) {
         logger.logError(e);
         refreshArticleGroup();
         Dialogs.exception("Error while saving...", e);
         return CanSaveAction.ABORT_SAVE;
      }
   }

   private boolean validateArticleGroup() {
      for (Object article : articlesInGroup) {
         Set<ConstraintViolation<Article>> validationErrors = validator.validate((Article) article);
         if (!validationErrors.isEmpty()) {
            markError(validationErrors);
            return false;
         }
         Set<ConstraintViolation<BindingEntity>> subViewValidationErrors = subViewDataProvider
               .validateSubViewFor((Article) article);
         if (!subViewValidationErrors.isEmpty()) {
            markErrorSubView(subViewValidationErrors);
            return false;
         }
      }
      return true;
   }

   private void refreshArticleGroup() {
      for (Object article : articlesInGroup) {
         try {
            em.refresh(article);
         } catch (Exception ex) {
            // do nothing
         }
      }
   }

   private void createPart(final Composite composite) {

      articleGuiFormActionHandler.init(isSearchMode(), getEditorInput(), wf, getCrudController(), getCtx());

      String rowLayout;

      if (isSearchMode()) {
         rowLayout = "[][][][][]15[][][][][][grow, fill]";
      } else {
         rowLayout = "[][][][][]15[][][grow, fill]";
      }

      MigLayout ml = new MigLayout("ins 10, gapy 4", "[80][250]80[80][250][10, grow, fill]", rowLayout);
      composite.setLayout(ml);

      wf.label(composite).text(getEditorInput().getTitle()).titleStyle().build();
      wf.text(composite, "beschreibung").multiLine().isJoinedForSearch().label("Note:", "ay top")
            .hint("spanx 5, h 40!, growx, wrap").build();

      if (isSearchMode()) {

         final Combo company = wf.combo(composite, "lieferantName").isJoined().label("Company:").hint("split 2")
               .data(firmaService.findAll()).build();

         wf.button(composite, "lookupCompany").withSearchImage().hint("wrap").listener(new SimpleSelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               articleGuiFormActionHandler.selectFirma(company);
            }
         }).build();

         wf.text(composite, "baNr", SearchTermType.STRING_IGNORE_FIRST_CHAR).label("BA Number:").isJoinedForSearch()
               .limitInputToDigits().hint("growx, wrap, h 21!").build();

      } else {
         wf.text(composite, "lieferantName").multiLine().label("Company:").hint("growx, wrap, h 21!").build();
      }

      if (isSearchMode()) {
         createMainData(composite);
      } else {

         // @formatter:off
         final Text baNr = wf.text(composite, "baNr")
               .label("BA Number:")
               .noBinding()
               .style(SWT.READ_ONLY)
               .useBigDecimalConverter()
               .hint("growx, wrap, h 21!").build();
               // @formatter:on

         createItemSelectionCombo(composite, getEditorInput());
         createButtonComposite(composite);
         createTabsheets(composite);

         // Display the BA number for the selected article
         // init the view by sending EVENT_SOURCE_ARTICLE_INIT
         getEditorInput().processData(new Func1Void<Article>() {
            @Override
            public void apply(Article article) {
               BigDecimal gruppeArtikel = article.getGruppeArtikel();
               articleDescriptionId = article.getArticleDescription().getId();
               List<OrderPos> orderPos = orderPosService.findByGruppeArtikel(gruppeArtikel);
               if (!orderPos.isEmpty()) {
                  baNr.setText(orderPos.get(0).getOrder().getNummer().toString());
               }
               propertyChange(new PropertyChangeEvent(ArticleGuiForm.this, EVENT_SOURCE_ARTICLE_INIT, article, article));
            }
         });

         wf.notifyListenersWithSelectionEvent(ITEM_COMBO);

      }

   }

   private void createMainData(Composite composite) {
      if (isSearchMode()) {
         wf.text(composite, "internId").label("Internal-Id:").hint("growx, wrap").build();
      } else {
         wf.text(composite, "internId").label("Internal-Id:").hint("growx, split 2").build();
         wf.button(composite, "assignId").text("<=").hint("wrap").listener(new SimpleSelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               articleGuiFormActionHandler.assignId();
            }
         }).build();
      }
      wf.text(composite, "serienNr").label("Serial-Nr:").hint("wrap, growx").build();
      wf.text(composite, "gruppeArtikel").label("Article Group:").hint("growx").build();
      wf.text(composite, "inventarNr").label("Inventory-Nr:").hint("wrap,growx").useBigDecimalConverter().build();
      wf.combo(composite, "status").label("Status:").data(articleStateService.findAll()).hint("growx").build();
      wf.text(composite, "gruppe").label("Group:").hint("wrap,growx").build();
   }

   private Option<IStructuredSelection> currentSelection = new None<IStructuredSelection>();

   public void setButtonsEnabled(boolean enabled) {
      wf.setEnabled(P("showHistory"), enabled);
      wf.setEnabled(P("contains"), enabled);
      wf.setEnabled(P("isContainedIn"), enabled);
   }

   protected void createAdditionalSearchResultButtons(Composite composite, final TableViewer viewer) {

      viewer.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            setButtonsEnabled(true);
            currentSelection = new Some<IStructuredSelection>((IStructuredSelection) event.getSelection());
         }
      });

      Composite buttonComposite = new Composite(composite, SWT.NONE);
      buttonComposite.setLayoutData("span 7");
      MigLayout ml = new MigLayout("ins 0", "[150,fill][150, fill][150, fill]", "[fill]");
      buttonComposite.setLayout(ml);

      wf.button(buttonComposite, "showHistory").disable().text("Show article history")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  if (currentSelection.hasValue()) {
                     Article article = (Article) currentSelection.get().getFirstElement();
                     articleGuiFormActionHandler.showHistory(article);
                  }
               }
            }).hint("h 29!").build();

      wf.button(buttonComposite, "contains").disable().text("Show contains").listener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (currentSelection.hasValue()) {
               Article article = (Article) currentSelection.get().getFirstElement();
               articleGuiFormActionHandler.showContains(article);
            }
         }
      }).hint("h 29!").build();

      wf.button(buttonComposite, "isContainedIn").disable().text("Show is contained in")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  Article article = (Article) currentSelection.get().getFirstElement();
                  articleGuiFormActionHandler.showIsContainedIn(article);
               }
            }).hint("h 29!").build();
   }

   // Register listener for change of article selection
   private void addItemSelectionListener(Combo combo) {
      combo.addSelectionListener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent evt) {
            IStructuredSelection selection = wf.getSelection(ITEM_COMBO);
            articleGuiFormActionHandler.changeArticleSelectionAndUpdateBinding(selection);
            propertyChange(new PropertyChangeEvent(ArticleGuiForm.this, EVENT_SOURCE_ARTICLE_CHANGED, selection,
                  selection));
         }
      });
   }

   // Create combobox with all articles that belongs to an articlegroup.
   private void createItemSelectionCombo(final Composite composite, final GenericEditorInput<Article> editorInput) {
      editorInput.processData(new Func1Void<Article>() {
         @Override
         public void apply(Article article) {
            List<Article> articles = articleService.findAllArticleInGroup(article.getGruppeArtikel());
            articlesInGroup = new WritableList(articles, articles.getClass());

            // @formatter:off
            Combo itemSelectionCombo = wf.combo(composite, ITEM_COMBO.getName())
                  .label("Item Position:")
                  .noBinding()
                  .data(articlesInGroup, new Property("index"))
                  .select(article)
                  .hint("growx, wrap, gaptop 7, gapbottom 7")
                  .build();
                  // @formatter:on

            articleGuiFormActionHandler.setSelectedArticleSelection(wf.getSelection(ITEM_COMBO));
            addItemSelectionListener(itemSelectionCombo);
         }
      });
   }

   // create "Add new Article", "Add remaining articles" and
   // "Bulk assign Internal-Ids" buttons
   private void createButtonComposite(Composite composite) {

      Composite buttonComposite = new Composite(composite, SWT.NONE);
      MigLayout mlButtonComposite = new MigLayout("ins 0", "[165]5[165]5[165]", "[]5");

      buttonComposite.setLayout(mlButtonComposite);
      buttonComposite.setLayoutData("spanx 6, wrap");

      wf.button(composite, "addNewArticle").text("Add new Article").hint("w 160!, h 28!")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  Article article = articleGuiFormActionHandler.addNewArticle(articlesInGroup);
                  propertyChange(new PropertyChangeEvent(ArticleGuiForm.this, EVENT_SOURCE_ARTICLE_NEW, article,
                        article));
               }
            }).build();

      wf.button(composite, "addRemainingArticles").text("Add remaining articles").hint("w 160!, h 28!, split 2")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  Article refArticle = (Article) articlesInGroup.get(0);
                  List<OrderPos> orderPos = orderPosService.findByGruppeArtikel(refArticle.getGruppeArtikel());
                  if (!orderPos.isEmpty()) {
                     BigDecimal anzahlBestellt = orderPos.get(0).getAnzahlBestellt();
                     if (anzahlBestellt != null) {
                        Article article = articleGuiFormActionHandler.addRemainingArticles(articlesInGroup,
                              anzahlBestellt.intValue() - articlesInGroup.size());
                        propertyChange(new PropertyChangeEvent(ArticleGuiForm.this, EVENT_SOURCE_ARTICLE_NEW, article,
                              article));
                     }
                  }
               }
            }).build();

      wf.button(composite, "assignAllIds").text("Bulk assign Internal-Ids").hint("w 160!, h 28!, wrap")
            .listener(new SimpleSelectionListener() {
               @Override
               public void widgetSelected(SelectionEvent e) {
                  articleGuiFormActionHandler.bulkAssignId(articlesInGroup);
                  wf.setText(P("internId"), currentlySelectedArticle.getInternId());
               }
            }).build();

   }

   private void createTabsheets(Composite composite) {
      tabFolder = wf.createTabFolder(composite);
      tabFolder.setLayoutData("wrap, height 400:n:n, spanx 6, growx, growy");
      tabFolder.addSelectionListener(new SimpleSelectionListener() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            TabFolder tf = (TabFolder) e.getSource();

            boolean enabled = tf.getSelectionIndex() == 0;

            wf.setEnabled(ITEM_COMBO, enabled);
            wf.setEnabled(P("addNewArticle"), enabled);
            wf.setEnabled(P("addRemainingArticles"), enabled);
            wf.setEnabled(P("assignAllIds"), enabled);
         }
      });
      createTabSheetMainData(tabFolder);
   }

   private void createTabSheetMainData(TabFolder tf) {
      TabItem tabItemMain = wf.createTabItem("Article", tf);
      Composite c1 = new Composite(tf, SWT.BORDER);
      MigLayout mlc1 = new MigLayout("ins 10", "[80][250]80[80][250][10, grow, fill]", "[][][][][]12[fill][]");
      c1.setLayout(mlc1);
      tabItemMain.setControl(c1);
      createMainData(c1);
   }

   // make sure that the status subview corresponds to the article status
   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      String newStatus;
      String oldStatus;
      if (evt.getPropertyName().equalsIgnoreCase(EVENT_SOURCE_ARTICLE_INIT)) {
         currentlySelectedArticle = (Article) evt.getNewValue();
         oldStatus = currentlySelectedArticle.getStatus();
         newStatus = oldStatus;
      } else if (evt.getPropertyName().equalsIgnoreCase(EVENT_SOURCE_ARTICLE_NEW)) {
         currentlySelectedArticle = (Article) evt.getNewValue();
         oldStatus = currentlySelectedArticle.getStatus();
         newStatus = oldStatus;
      } else if (evt.getPropertyName().equalsIgnoreCase(EVENT_SOURCE_ARTICLE_CHANGED)) {
         IStructuredSelection structuredSelection = (IStructuredSelection) evt.getNewValue();
         currentlySelectedArticle = (Article) structuredSelection.getFirstElement();
         oldStatus = currentlySelectedArticle.getStatus();
         newStatus = oldStatus;
      } else if (evt.getPropertyName().equalsIgnoreCase(EVENT_SOURCE_ARTICLE_STATUS)) {
         // triggered by setStatus(String status) in Article
         newStatus = (String) evt.getNewValue();
         oldStatus = (String) evt.getOldValue();
      } else {
         return;
      }
      if (tabItemStatusData != null) {
         tabItemStatusData.dispose();
      }
      tabItemStatusData = wf.createTabItem("Status Data", tabFolder);
      try {
         Option<Composite> statusComposite = getCompositeFromStatus(newStatus, oldStatus);
         if (statusComposite.hasValue()) {
            tabItemStatusData.setControl(statusComposite.get());
            int currentSelectionIndex = tabFolder.getSelectionIndex();
            tabFolder.setSelection(currentSelectionIndex);
         }
      } catch (Exception e) {
         logger.logError(e);
      }
   }

   // create status subview for the given status
   private Option<Composite> getCompositeFromStatus(String newStatus, String oldStatus) throws InstantiationException,
         IllegalAccessException, InvocationTargetException {
      // --- Ausgeliehen ---
      if (newStatus.equalsIgnoreCase("ausgeliehen")) {
         ArticleRented articleRented = subViewDataProvider.getOrCreateArticleRented(currentlySelectedArticle,
               newStatus, oldStatus);
         return rentedView.build(getCrudController().get(), articleRented, tabFolder);
         // --- Angeliefert ---
      } else if (newStatus.equalsIgnoreCase("angeliefert")) {
         ArticleDelivered articleDelivered = subViewDataProvider.getOrCreateArticleDelivered(currentlySelectedArticle,
               newStatus, oldStatus);
         return deliveredView.build(getCrudController().get(), articleDelivered, tabFolder);
         // --- Eingebaut ---
      } else if (newStatus.equals("eingebaut")) {
         ArticleInstalled articleInstalled = subViewDataProvider.getOrCreateArticleInstalled(currentlySelectedArticle,
               newStatus, oldStatus);
         Func1Void<InstalledView> selectArticle = new Func1Void<InstalledView>() {
            @Override
            public void apply(final InstalledView installedView) {
               articleGuiFormActionHandler.lookupArticle(installedView);
            }
         };
         return installedView.build(getCrudController().get(), articleInstalled, tabFolder, selectArticle);
         // --- Im Lager ---
      } else if (newStatus.equals("in Lager")) {
         ArticleInStore articleInStore = subViewDataProvider.getOrCreateArticleInStore(currentlySelectedArticle,
               newStatus, oldStatus);
         return storeView.build(getCrudController().get(), articleInStore, articleDescriptionId, tabFolder);
         // --- in Reparatur ---
      } else if (newStatus.equals("ausgemustert")) {
         ArticleRetired articleRetired = subViewDataProvider.getOrCreateArticleRetired(currentlySelectedArticle,
               newStatus, oldStatus);
         return retiredView.build(getCrudController().get(), articleRetired, tabFolder);
         // --- in Reparatur ---
      } else if (newStatus.equals("in Reparatur")) {
         ArticleMaintenance articleMaintenance = subViewDataProvider.getOrCreateArticleMaintenance(
               currentlySelectedArticle, newStatus, oldStatus);
         return maintenanceView.build(getCrudController().get(), articleMaintenance, tabFolder);
      } else {
         subViewDataProvider.removeObject(currentlySelectedArticle);
         return new None<Composite>();
      }
   }

   private void updateModelsInSubViews() {
      rentedView.updateModels();
      deliveredView.updateModels();
      retiredView.updateModels();
      installedView.updateModels();
      maintenanceView.updateModels();
      storeView.updateModels();
   }

}
