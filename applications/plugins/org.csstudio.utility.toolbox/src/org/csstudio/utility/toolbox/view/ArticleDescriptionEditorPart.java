package org.csstudio.utility.toolbox.view;

import org.csstudio.utility.toolbox.actions.OpenArticleDescriptionEditorAction;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.view.forms.ArticleDescriptionGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class ArticleDescriptionEditorPart extends AbstractCrudEditorPartTemplate<ArticleDescription> implements
      CrudController<ArticleDescription> {

   public static final String ID = "org.csstudio.utility.toolbox.view.ArticleDescriptionEditorPart";

   @Inject
   private ArticleDescriptionGuiForm articleDescriptionGuiForm;

   @Inject
   private OpenArticleDescriptionEditorAction openArticleDescriptionEditorAction;

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input, articleDescriptionGuiForm);
      setEditorPartName("beschreibung");
   }

   @Override
   public void createPartControl(Composite composite) {
      articleDescriptionGuiForm.createEditPart(composite, getEditorInput(), this);
      setFocusWidget(articleDescriptionGuiForm.getFocusWidget());
   }

   @Override
   public void create() {
      openArticleDescriptionEditorAction.runWith(new ArticleDescription());
   }

   @Override
   public void copy() {
      if (!getEditorInput().hasData()) {
         throw new IllegalStateException("Data expected");
      }
      getEditorInput().processData(new Func1Void<ArticleDescription>() {
         @Override
         public void apply(ArticleDescription articleDescription) {
            ArticleDescription clone = articleDescription.deepClone();
            openArticleDescriptionEditorAction.runWith(clone);
         }
      });
   }

}
