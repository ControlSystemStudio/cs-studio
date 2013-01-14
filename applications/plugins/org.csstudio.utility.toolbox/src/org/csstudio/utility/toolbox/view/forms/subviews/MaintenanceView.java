package org.csstudio.utility.toolbox.view.forms.subviews;

import static org.csstudio.utility.toolbox.framework.property.Property.P;

import java.util.Date;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleMaintenance;
import org.csstudio.utility.toolbox.entities.KeywordSoftware;
import org.csstudio.utility.toolbox.entities.LogGroup;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.csstudio.utility.toolbox.framework.builder.AbstractControlWithLabelBuilder;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.proposal.TextValueProposalProvider;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.provider.SimpleDateFormatProvider;
import org.csstudio.utility.toolbox.services.DeviceService;
import org.csstudio.utility.toolbox.services.FirmaService;
import org.csstudio.utility.toolbox.services.KeywordService;
import org.csstudio.utility.toolbox.services.LogGroupService;
import org.csstudio.utility.toolbox.services.LogUserService;
import org.csstudio.utility.toolbox.services.MaintenanceStateService;
import org.csstudio.utility.toolbox.services.ProjectService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import com.google.inject.Inject;

public class MaintenanceView extends AbstractSubView<ArticleMaintenance> {

   @Inject
   private LogGroupService logGroupService;

   @Inject
   private LogUserService logUserService;

   @Inject
   private ProjectService projectService;

   @Inject
   private DeviceService deviceService;

   @Inject
   private FirmaService firmaService;

   @Inject
   private MaintenanceStateService maintenanceStateService;

   @Inject
   private KeywordService keywordService;

   @Inject
   private SimpleDateFormatProvider simpleDateFormatProvider;

   private Button sendEmailToGroupButton;

   private Button sendEmailToAccountButton;

   private Text keyword;

   private class SoftwareHardwareSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         TextValueProposalProvider proposalProvider = (TextValueProposalProvider) keyword
               .getData(AbstractControlWithLabelBuilder.CONTENT_PROPOSAL_PROVIDER);
         List<? extends TextValue> data;
         if (wf.isSelected(P("software"))) {
            data = keywordService.findAllSoftware();
         } else {
            data = keywordService.findAllHardware();
         }
         proposalProvider.setData(data);
         keyword.setText("");
      }
   }

   private class RepairSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         if (wf.isSelected(P("repairGroup"))) {
            wf.setInput(P("repair"), logGroupService.findAllAndIncludeEmptySelection());
         } else if (wf.isSelected(P("repairAccount"))) {
            wf.setInput(P("repair"), logUserService.findAllAndIncludeEmptySelection());
         } else if (wf.isSelected(P("repairCompany"))) {
            wf.setInput(P("repair"), firmaService.findAll());
         } else {
            wf.setInput(P("repair"), logGroupService.findAllAndIncludeEmptySelection());
         }
      }
   }

   private class EmailGroupAccountSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         if (wf.isSelected(P("emailGroup"))) {
            wf.setInput(P("sendEmailTo"), logGroupService.findAllAndIncludeEmptySelectionUseEmail());
         } else {
            wf.setInput(P("sendEmailTo"), logUserService.findAllAndIncludeEmptySelectionUseEmail());
         }
      }
   }

   private class StateSelectionListener extends SimpleSelectionListener {
      @Override
      public void widgetSelected(SelectionEvent e) {
         wf.setText(P("statusVom"), simpleDateFormatProvider.get().format(new Date()));
      }
   }

   public Some<Composite> build(CrudController<Article> crudController, ArticleMaintenance articleMaintenance,
         TabFolder tabFolder) {

      init(crudController, articleMaintenance);

      Composite composite = createComposite(tabFolder, "ins 10", "[100][250, fill]10[80][80, grow, fill]",
            "[][][]10[]15[]10[]10[][]10[][][][]");

      wf.label(composite).text("Maintenance").titleStyle().build();

      wf.text(composite, "id").label("ID:").hint("wrap").build();

      keyword = wf.text(composite, "keywords").label("Keyword:").data(keywordService.findAllSoftware()).build();

      Composite keywordsComposite = new Composite(composite, SWT.None);
      MigLayout layoutKeywordsComposite = new MigLayout("ins 0", "", "");
      keywordsComposite.setLayout(layoutKeywordsComposite);
      keywordsComposite.setLayoutData("span 2, wrap");

      final Button keywordSoftwareButton = wf.radioButton(keywordsComposite, "software").text("Software")
            .hint("w 90!, ay top").build();
      final Button keywordHardwareButton = wf.radioButton(keywordsComposite, "hardware").text("Hardware")
            .hint("w 90!, ay top").build();

      SoftwareHardwareSelectionListener softwareHardwareSelectionListener = new SoftwareHardwareSelectionListener();
      keywordSoftwareButton.addSelectionListener(softwareHardwareSelectionListener);

      wf.text(composite, "project").label("Installed in Project:").data(projectService.findAll())
            .hint("split 6, span 4").build();

      wf.text(composite, "device").label("in Device:", "gapleft 10, w 60!").data(deviceService.findAll()).build();

      wf.text(composite, "location").label("in Location:", "gapleft 10, w 60!").hint("wrap").build();

      wf.label(composite).text("State:").build();

      Composite stateComposite = new Composite(composite, SWT.None);
      MigLayout migLayoutStateComposite = new MigLayout("ins 0", "", "");
      stateComposite.setLayout(migLayoutStateComposite);
      stateComposite.setLayoutData("span 2, wrap");

      Combo statusCombo = wf.combo(stateComposite, "status").data(maintenanceStateService.findAll()).hint("w 120!")
            .build();
      statusCombo.addSelectionListener(new StateSelectionListener());

      wf.text(stateComposite, "statusVom").label("State Date:").readOnly().hint("wrap, w 120!").build();

      wf.text(composite, "descShort").label("Description short:").hint("span 6, wrap").build();

      wf.text(composite, "descLong").label("Description of problem", "aligny top").multiLine()
            .hint("span 6, wrap, h 60!").build();

      wf.date(composite, "startRequest").label("Repair has to start on:").hint("w 100!, split 3").build();

      wf.date(composite, "finishRequest").label("Repair has to be finished on:").hint("w 100!, wrap").build();

      wf.label(composite).text("Repair has to be done by:").build();

      Composite repairComposite = new Composite(composite, SWT.None);
      MigLayout layoutRepairComposite = new MigLayout("ins 0", "", "[20]");
      repairComposite.setLayout(layoutRepairComposite);
      repairComposite.setLayoutData("wrap");

      final Button repairDoneByGroup = wf.radioButton(repairComposite, "repairGroup").text("group")
            .hint("w 90!, ay top").build();
      final Button repairDoneByAccount = wf.radioButton(repairComposite, "repairAccount").text("account")
            .hint("w 90!, ay top").build();
      final Button repairDoneByCompany = wf.radioButton(repairComposite, "repairCompany").text("company")
            .hint("w 90!, ay top").build();
      final Combo comboRepairDone = wf.combo(repairComposite, "repair")
            .data(logGroupService.findAllAndIncludeEmptySelection()).hint("w 150!, ay top").build();

      repairDoneByGroup.addSelectionListener(new RepairSelectionListener());
      repairDoneByAccount.addSelectionListener(new RepairSelectionListener());

      wf.label(composite).text("Send email to:").build();

      Composite emailComposite = new Composite(composite, SWT.None);
      MigLayout layoutEmailComposite = new MigLayout("ins 0", "", "[20]");
      emailComposite.setLayout(layoutEmailComposite);
      emailComposite.setLayoutData("wrap");

      sendEmailToGroupButton = wf.radioButton(emailComposite, "emailGroup").text("group").hint("w 90!, ay top").build();
      sendEmailToAccountButton = wf.radioButton(emailComposite, "emailAccount").text("account").hint("w 90!").build();

      final Combo comboEmail = wf.combo(emailComposite, "sendEmailTo")
            .data(logGroupService.findAllAndIncludeEmptySelection()).hint("w 150!").build();

      sendEmailToGroupButton.addSelectionListener(new EmailGroupAccountSelectionListener());

      wf.text(composite, "sendEmailTo").label("Send email to account:").hint("wrap").build();

      if (!getEditorInput().hasValue()) {
         throw new IllegalStateException("Editor input has no value");
      }

      getEditorInput().get().processData(new Func1Void<ArticleMaintenance>() {

         // update the state of the radio buttons and fill the combobox
         // accordingly
         @Override
         public void apply(ArticleMaintenance articleMaintenance) {

            String keywords = articleMaintenance.getKeywords();
            if (StringUtils.isNotEmpty(keywords)) {
               Option<KeywordSoftware> keywordSoftware = keywordService.findByKeywordSoftware(keywords);
               if (keywordSoftware.hasValue()) {
                  keywordSoftwareButton.setSelection(true);
               } else {
                  keywordHardwareButton.setSelection(true);
               }
            } else {
               keywordSoftwareButton.setSelection(true);
            }

            if (StringUtils.isNotEmpty(articleMaintenance.getBeiFirma())) {
               repairDoneByCompany.setSelection(true);
            } else if (StringUtils.isNotEmpty(articleMaintenance.getBeiGruppe())) {
               repairDoneByGroup.setSelection(true);
            } else if (StringUtils.isNotEmpty(articleMaintenance.getBeiAccount())) {
               repairDoneByAccount.setSelection(true);
            } else {
               repairDoneByGroup.setSelection(true);
            }

            comboRepairDone.notifyListeners(SWT.Selection, new Event());

            String email = articleMaintenance.getSendEmailTo();

            if (StringUtils.isNotEmpty(email)) {
               Option<LogGroup> logGroup = logGroupService.findByEmail(email);
               if (logGroup.hasValue()) {
                  sendEmailToGroupButton.setSelection(true);
               } else {
                  sendEmailToAccountButton.setSelection(true);
               }
            } else {
               sendEmailToGroupButton.setSelection(true);
            }

            comboEmail.notifyListeners(SWT.Selection, new Event());

         }
      });

      return new Some<Composite>(composite);
   }

}