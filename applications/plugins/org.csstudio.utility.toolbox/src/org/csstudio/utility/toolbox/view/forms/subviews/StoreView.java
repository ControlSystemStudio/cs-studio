package org.csstudio.utility.toolbox.view.forms.subviews;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.math.BigDecimal;
import java.util.List;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleInStore;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.LagerArtikelService;
import org.csstudio.utility.toolbox.services.LagerService;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;

import com.google.inject.Inject;

public class StoreView extends AbstractSubView<ArticleInStore> {

   private BigDecimal articleDescriptionId;

   @Inject
   private LagerService lagerService;

   @Inject
   private LagerArtikelService lagerArtikeService;

   @Inject
   private LogUserService logUserService;

   @Inject
   private LagerArtikelService lagerArtikelService;

   private class LagerSelectionLister extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         String lagerName = wf.getText(P("lagerName"));
         List<LagerArtikel> lagerArtikel = lagerArtikelService.findAll(lagerName, articleDescriptionId);
         wf.setInput(P("lagerArtikelId"), lagerArtikel);
         wf.setText(P("stockLocation"), "");
         wf.setText(P("stockShelf"), "");
         wf.setText(P("stockBox"), "");
      }
   }

   private class LagerIdSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         if (wf.getSelectionIndex(P("lagerArtikelId")) == -1) {
            return;
         }
         String lagerArtikelId = wf.getText(P("lagerArtikelId"));
         Option<LagerArtikel> lagerArtikel = lagerArtikeService.findById(lagerArtikelId);
         if (lagerArtikel.hasValue()) {
            wf.setText(P("stockLocation"), lagerArtikel.get().getOrt());
            wf.setText(P("stockShelf"), lagerArtikel.get().getFach());
            wf.setText(P("stockBox"), lagerArtikel.get().getBox());
         }
      }
   }

   public Some<Composite> build(CrudController<Article> crudController, ArticleInStore articleInStore,
         BigDecimal articleDescriptionId, TabFolder tabFolder) {

      this.articleDescriptionId = articleDescriptionId;

      init(crudController, articleInStore);

      final Composite composite = createComposite(tabFolder, "ins 10", "[80][250, fill]10[80][80, grow, fill]",
            "[][][][][]");

      wf.label(composite).text("Article in Store").add("id").titleStyle().build();

      final Combo comboLager = wf.combo(composite, "lagerName").label("Stock name:").data(lagerService.findAll())
            .hint("w 250!, ay top, wrap").build();
      comboLager.addSelectionListener(new LagerSelectionLister());

      final Combo comboLagerId = wf.combo(composite, "lagerArtikelId").label("Stock id").emptyData()
            .hint("w 250!, ay top, wrap").build();
      comboLagerId.addSelectionListener(new LagerIdSelectionListener());

      wf.text(composite, "stockLocation").label("Stock location:").noBinding().readOnly().hint("wrap").build();
      wf.text(composite, "stockShelf").label("Stock shelf:").noBinding().readOnly().hint("wrap").build();
      wf.text(composite, "stockBox").label("Stock box:").noBinding().readOnly().hint("wrap").build();

      wf.combo(composite, "inLagerDurch").label("Put in stock by:").data(logUserService.findAll()).hint("wrap").build();

      wf.date(composite, "inLagerAm").label("Date:").hint("gaptop 5, wrap").build();

      comboLager.notifyListeners(SWT.Selection, new Event());

      getEditorInput().get().processData(new Func1Void<ArticleInStore>() {
         @Override
         public void apply(ArticleInStore articleInStore) {
            wf.setText(P("lagerArtikelId"), articleInStore.getLagerArtikelId());
            wf.notifyListenersWithSelectionEvent(P("lagerArtikelId"));
         }
      });

      return new Some<Composite>(composite);
   }

}
