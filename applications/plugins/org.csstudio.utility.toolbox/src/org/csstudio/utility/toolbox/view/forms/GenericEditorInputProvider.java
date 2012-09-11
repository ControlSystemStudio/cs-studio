package org.csstudio.utility.toolbox.view.forms;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.framework.action.EditorInputProvider;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;

public class GenericEditorInputProvider<T extends BindingEntity> implements EditorInputProvider<T> {

	@Inject
	private TypeLiteral<T> typeLiteral;

	@Inject
	private MembersInjector<ArticleEditorInput<T>> miArticle;

	@Inject
	private MembersInjector<GenericEditorInput<T>> miGeneric;

	@Override
	public GenericEditorInput<T> get() {
		if (typeLiteral.getRawType() == Article.class) {
			ArticleEditorInput<T> editorInput = new ArticleEditorInput<T>();
			miArticle.injectMembers(editorInput);
			return editorInput;
		} else {
			GenericEditorInput<T> editorInput = new GenericEditorInput<T>();
			miGeneric.injectMembers(editorInput);
			return editorInput;
		}
	}

}
