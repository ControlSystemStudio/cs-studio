package org.csstudio.utility.toolbox.guice;

import java.text.SimpleDateFormat;

import javax.validation.Validator;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleDescription;
import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.framework.action.EditorInputProvider;
import org.csstudio.utility.toolbox.guice.provider.DisplayProvider;
import org.csstudio.utility.toolbox.guice.provider.SimpleDateFormatProvider;
import org.csstudio.utility.toolbox.guice.provider.ValidatorProvider;
import org.csstudio.utility.toolbox.guice.provider.WorkbenchPageProvider;
import org.csstudio.utility.toolbox.guice.provider.WorkbenchProvider;
import org.csstudio.utility.toolbox.guice.provider.WorkbenchWindowProvider;
import org.csstudio.utility.toolbox.view.forms.GenericEditorInputProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

public class GuiceModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IWorkbench.class).toProvider(WorkbenchProvider.class);
		bind(IWorkbenchWindow.class).toProvider(WorkbenchWindowProvider.class);
		bind(IWorkbenchPage.class).toProvider(WorkbenchPageProvider.class);
		bind(Display.class).toProvider(DisplayProvider.class);
		bind(SimpleDateFormat.class).toProvider(SimpleDateFormatProvider.class);
		bind(Validator.class).toProvider(ValidatorProvider.class);
		bind(new TypeLiteral<EditorInputProvider<Order>>() {
		}) // NOSONAR
		.to(new TypeLiteral<GenericEditorInputProvider<Order>>() {
		}); // NOSONAR
		bind(new TypeLiteral<EditorInputProvider<Article>>() {
		}) // NOSONAR
		.to(new TypeLiteral<GenericEditorInputProvider<Article>>() {
		}); // NOSONAR
		bind(new TypeLiteral<EditorInputProvider<Firma>>() {
		}) // NOSONAR
		.to(new TypeLiteral<GenericEditorInputProvider<Firma>>() {
		}); // NOSONAR
		bind(new TypeLiteral<EditorInputProvider<ArticleDescription>>() {
		}) // NOSONAR
		.to(new TypeLiteral<GenericEditorInputProvider<ArticleDescription>>() {
		}); // NOSONAR
		bind(new TypeLiteral<EditorInputProvider<Lager>>() {
		}) // NOSONAR
		.to(new TypeLiteral<GenericEditorInputProvider<Lager>>() {
		}); // NOSONAR
		bind(new TypeLiteral<EditorInputProvider<LagerArtikel>>() {
		}) // NOSONAR
		.to(new TypeLiteral<GenericEditorInputProvider<LagerArtikel>>() {
		}); // NOSONAR
	 
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(ClearPersistenceContextOnReturn.class),
					PersistenceContextClearer.persistenceContextClearer);

	}

}
