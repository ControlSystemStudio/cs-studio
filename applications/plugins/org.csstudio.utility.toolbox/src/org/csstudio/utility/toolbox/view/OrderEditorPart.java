package org.csstudio.utility.toolbox.view;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;

import org.csstudio.utility.toolbox.actions.OpenOrderEditorAction;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.template.AbstractCrudEditorPartTemplate;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.services.ArticleService;
import org.csstudio.utility.toolbox.services.OrderPosService;
import org.csstudio.utility.toolbox.view.forms.OrderGuiForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.inject.Inject;

public class OrderEditorPart extends AbstractCrudEditorPartTemplate<Order> implements CrudController<Order> {

   public static final String ID = "org.csstudio.utility.toolbox.view.OrderEditorPart";

   @Inject
   private OrderGuiForm orderGuiForm;

   @Inject
   private OpenOrderEditorAction openOrderEditorAction;

   @Inject
   private EntityManager em;

   @Inject
   private ArticleService articleService;

   @Inject
   private OrderPosService orderPosService;

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input, orderGuiForm);
      setEditorPartName("nummer");
   }

   @Override
   public void createPartControl(Composite composite) {
      orderGuiForm.createEditPart(composite, getEditorInput(), this);
      getEditorInput().setBeforeCommit(new BeforeCommit());
      setFocusWidget(orderGuiForm.getFocusWidget());
   }

   class BeforeCommit implements Func1Void<Order> {
      @Override
      public void apply(Order order) {
         List<OrderPos> orderPositions = order.getOrderPositions(orderPosService);
         for (OrderPos orderPos : orderPositions) {
            if (orderPos.getArticle().getId() == null) {
               BigDecimal articleId = articleService.createId();
               orderPos.getArticle().setId(articleId);
               if (orderPos.getArticle().getGruppeArtikel() == null) {
                  orderPos.getArticle().setGruppeArtikel(articleId);
               }
            }
            orderPos.setBaNr(order.getNummer());
            em.merge(orderPos);
         }
      }
   }
   

   @Override
   public void create() {
      openOrderEditorAction.runWith(new Order());
   }

   @Override
   public void copy() {
      if (!getEditorInput().hasData()) {
         throw new IllegalStateException("Data expected");
      }
      getEditorInput().processData(new Func1Void<Order>() {
         @Override
         public void apply(Order order) {
            Order clone = order.deepClone();
            openOrderEditorAction.runWith(clone);
         }
      });
   }

}
