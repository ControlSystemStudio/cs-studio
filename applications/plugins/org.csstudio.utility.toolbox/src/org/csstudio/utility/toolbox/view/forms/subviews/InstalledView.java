package org.csstudio.utility.toolbox.view.forms.subviews;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleInstalled;
import org.csstudio.utility.toolbox.entities.Raum;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.services.ArticleService;
import org.csstudio.utility.toolbox.services.DeviceService;
import org.csstudio.utility.toolbox.services.GebaeudeService;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.csstudio.utility.toolbox.services.ProjectService;
import org.csstudio.utility.toolbox.view.forms.listener.BuildingModifyListener;
import org.csstudio.utility.toolbox.view.forms.listener.RoomFocusListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class InstalledView extends AbstractSubView<ArticleInstalled> {

   @Inject
   private LogUserService logUserService;

   @Inject
   private ProjectService projectService;

   @Inject
   private DeviceService deviceService;

   @Inject
   private GebaeudeService gebaeudeService;

   @Inject
   private ArticleService articleService;

   @Inject
   private BuildingModifyListener buildingModifyListener;

   @Inject
   private RoomFocusListener roomFocusListener;

   private Func1Void<InstalledView> selectArticleCallBack;

   private BigDecimal artikelDatenId;

   private class SearchArticleSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         selectArticleCallBack.apply(InstalledView.this);
      }
   }

   private void setInstalledDataEnabled(boolean value) {
      wf.setEnabled(P("project"), value);
      wf.setEnabled(P("device"), value);
      wf.setEnabled(P("gebaeude"), value);
      wf.setEnabled(P("raum"), value);
   }

   private class ClearArticleSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         artikelDatenId = null;
         wf.setText(P("beschreibung"), "");
         wf.setText(P("internalNr"), "");
         wf.setText(P("inventoryNr"), "");
         wf.setText(P("locationDetails"), "");
         wf.setText(P("gebaeude"), "");
         wf.setText(P("raum"), "");
         setInstalledDataEnabled(true);
      }
   }

   public void updateAfterArticleSelection(IStructuredSelection selection) {
      Article article = (Article) selection.getFirstElement();
      artikelDatenId = article.getId();
      wf.setText(P("beschreibung"), article.getBeschreibung());
      wf.setText(P("internalNr"), article.getInternId());
      wf.setText(P("inventoryNr"), ObjectUtils.toString(article.getInventarNr(), ""));
      displayArticleData(article);
      setInstalledDataEnabled(false);
      getEditorInput().get().processData(new Func1Void<ArticleInstalled>() {
         @Override
         public void apply(ArticleInstalled articleInstalled) {
            articleInstalled.setEingebautInArtikel(artikelDatenId);
         }
      });
   }

   private void displayArticleData(Article article) {
      wf.setText(P("beschreibung"), article.getBeschreibung());
      wf.setText(P("internalNr"), article.getInternId());
      wf.setText(P("inventoryNr"), ObjectUtils.toString(article.getInventarNr(), ""));
      List<ArticleInstalled> allArticleInstalled = articleService.findAllInstalledData(article.getId());
      if (!allArticleInstalled.isEmpty()) {
         ArticleInstalled lastArticleInstalled = allArticleInstalled.get(allArticleInstalled.size() - 1);
         wf.setText(P("locationDetails"), lastArticleInstalled.getLocationDetails());
         wf.setText(P("gebaeude"), lastArticleInstalled.getGebaeude());
         wf.setText(P("raum"), lastArticleInstalled.getRaum());
      }
   }

   public Some<Composite> build(CrudController<Article> crudController, ArticleInstalled articleInstalled,
         TabFolder tabFolder, Func1Void<InstalledView> selectArticleCallBack) {

      this.selectArticleCallBack = selectArticleCallBack;

      init(crudController, articleInstalled);

      Composite composite = createComposite(tabFolder, "ins 10", "[80][350, fill]30[80][350, fill][fill, grow]",
            "[][][][][]15[][][][]");

      wf.label(composite).text("Installed").add("id").titleStyle().build();

      wf.combo(composite, "eingebautDurch").label("Installed by")
            .data(logUserService.findAllAndIncludeEmptySelection()).hint("w 150!, split 3").build();

      wf.date(composite, "eingebautAm").label("Installation date:").hint("w 100!, wrap").build();

      wf.text(composite, "project").label("Project:").data(projectService.findAll()).build();

      wf.text(composite, "device").label("Device:").data(deviceService.findAll()).hint("wrap").build();

      Text building = wf.text(composite, "gebaeude").label("Building:").data(gebaeudeService.findAll()).build();

      Text room = wf.text(composite, "raum").label("Room:").hint("wrap").data(new ArrayList<Raum>())
            .message("Please provide value for building first...").notEditable().build();

      buildingModifyListener.init(building, room);
      roomFocusListener.init(building, room);

      building.addModifyListener(buildingModifyListener);
      room.addFocusListener(roomFocusListener);

      wf.text(composite, "locationDetails").label("Location details:").hint("wrap").build();

      wf.label(composite).text("Installed in Article").build();

      wf.button(composite, "lookupArticle").hint("w 150!, split 2").text("Search Article")
            .listener(new SearchArticleSelectionListener()).build();

      wf.button(composite, "clearArticle").hint("w 150!, wrap").text("Clear Article")
            .listener(new ClearArticleSelectionListener()).build();

      Composite compositeArticleData = createComposite(composite, "ins 10", "[80][250, fill]30[80][250, fill][grow]",
            "[][][][][]");

      compositeArticleData.setLayoutData("h 190!, spanx 8");

      wf.text(compositeArticleData, "beschreibung").multiLine().isJoinedForSearch().label("Note:", "ay top")
            .hint("spanx 5, h 40!, growx, wrap").noBinding().readOnly().build();

      wf.text(compositeArticleData, "status").label("Status:").noBinding().readOnly().build();

      wf.text(compositeArticleData, "locationDetailsArticle").label("Location details:").noBinding().readOnly()
            .hint("wrap").build();

      wf.text(compositeArticleData, "internalNr").label("Internal-Nr:").noBinding().readOnly().readOnly().build();

      wf.text(compositeArticleData, "building").label("Building:").noBinding().hint("wrap").readOnly().build();

      wf.text(compositeArticleData, "inventoryNr").label("Inventory-Nr:").noBinding().readOnly().build();

      wf.text(compositeArticleData, "Room").label("Room:").hint("wrap").noBinding().readOnly().build();

      if (articleInstalled.getEingebautInArtikel() != null) {
         Option<Article> article = articleService.findById(articleInstalled.getEingebautInArtikel());
         if (article.hasValue()) {
            displayArticleData(article.get());
         }
      }

      return new Some<Composite>(composite);
   }
}
