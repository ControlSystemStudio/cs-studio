package org.csstudio.utility.toolbox.view.forms;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleDelivered;
import org.csstudio.utility.toolbox.entities.ArticleInStore;
import org.csstudio.utility.toolbox.entities.ArticleInstalled;
import org.csstudio.utility.toolbox.entities.ArticleMaintenance;
import org.csstudio.utility.toolbox.entities.ArticleRented;
import org.csstudio.utility.toolbox.entities.ArticleRetired;
import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.services.ArticleService;
import org.csstudio.utility.toolbox.services.LagerArtikelService;

import com.google.inject.Inject;

public class ArticleSubViewDataProvider {

	@Inject
	private EntityManager em;

	@Inject
	private ArticleService articleService;

	@Inject
	private LagerArtikelService lagerArticleService;

	@Inject
	private Validator validator;

	@Inject
	private LookupDataAutoCreator lookupDataAutoCreator;

	@Inject
	private Environment env;

	private Map<Article, BindingEntity> assignments = new HashMap<Article, BindingEntity>();

	private interface FindEntity<T> {
		List<T> find(BigDecimal articleId);
	}

	private class GetOrCreate<T extends BindingEntity> {
		@SuppressWarnings("unchecked")
		public T getObject(Article article, Class<T> clazz, FindEntity<T> finder, String newStatus, String oldStatus)
					throws InstantiationException, IllegalAccessException, InvocationTargetException {
			T object = null;
			if (newStatus.equals(oldStatus)) {
				if (assignments.containsKey(article)) {
					object = (T) assignments.get(article);
				} else {
					List<T> data = finder.find(article.getId());
					if (!data.isEmpty()) {
						object = data.get(0);
					}
				}
			} else {
				Object oldOne = assignments.get(article);
				if (oldOne != null) {
					try {
						em.detach(oldOne);
					} catch (Exception e) {
						// ignore it
					}
				}
			}
			if (object == null) {
				object = clazz.newInstance();
			}
			BeanUtils.setProperty(object, "artikelDatenId", article.getId());
			assignments.put(article, object);
			return object;
		}
	}

	public ArticleRented getOrCreateArticleRented(Article article, String newStatus, String oldStatus)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return new GetOrCreate<ArticleRented>().getObject(article, ArticleRented.class,
					new FindEntity<ArticleRented>() {
						@Override
						public List<ArticleRented> find(BigDecimal articleId) {
							return articleService.findArticleRented(articleId);
						}
					}, newStatus, oldStatus);
	}

	public ArticleDelivered getOrCreateArticleDelivered(Article article, String newStatus, String oldStatus)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return new GetOrCreate<ArticleDelivered>().getObject(article, ArticleDelivered.class,
					new FindEntity<ArticleDelivered>() {
						@Override
						public List<ArticleDelivered> find(BigDecimal articleId) {
							return articleService.findArticleDelivered(articleId);
						}
					}, newStatus, oldStatus);
	}

	public ArticleRetired getOrCreateArticleRetired(Article article, String newStatus, String oldStatus)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return new GetOrCreate<ArticleRetired>().getObject(article, ArticleRetired.class,
					new FindEntity<ArticleRetired>() {
						@Override
						public List<ArticleRetired> find(BigDecimal articleId) {
							return articleService.findArticleRetired(articleId);
						}
					}, newStatus, oldStatus);
	}

	public ArticleInStore getOrCreateArticleInStore(Article article, String newStatus, String oldStatus)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return new GetOrCreate<ArticleInStore>().getObject(article, ArticleInStore.class,
					new FindEntity<ArticleInStore>() {
						@Override
						public List<ArticleInStore> find(BigDecimal articleId) {
							return articleService.findArticleInStore(articleId);
						}
					}, newStatus, oldStatus);
	}

	public ArticleMaintenance getOrCreateArticleMaintenance(Article article, String newStatus, String oldStatus)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return new GetOrCreate<ArticleMaintenance>().getObject(article, ArticleMaintenance.class,
					new FindEntity<ArticleMaintenance>() {
						@Override
						public List<ArticleMaintenance> find(BigDecimal articleId) {
							return articleService.findArticleMaintenance(articleId);
						}
					}, newStatus, oldStatus);
	}

	public ArticleInstalled getOrCreateArticleInstalled(Article article, String newStatus, String oldStatus)
				throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return new GetOrCreate<ArticleInstalled>().getObject(article, ArticleInstalled.class,
					new FindEntity<ArticleInstalled>() {
						@Override
						public List<ArticleInstalled> find(BigDecimal articleId) {
							return articleService.findArticleInstalled(articleId);
						}
					}, newStatus, oldStatus);
	}

	private Option<LagerArtikel> artikelInsLager(BindingEntity bindingEntity) {
		ArticleInStore articleInStore = (ArticleInStore) bindingEntity;
		if (articleInStore.isNew()) {
			Option<LagerArtikel> lagerArtikel = lagerArticleService.findById(articleInStore.getLagerArtikelId());
			BigDecimal actualBestand = lagerArtikel.get().getActualBestand();
			if (actualBestand == null) {
				lagerArtikel.get().setActualBestand(BigDecimal.ONE);
			} else {
				BigDecimal neuBestand = actualBestand.add(BigDecimal.ONE);
				lagerArtikel.get().setActualBestand(neuBestand);
			}
			return lagerArtikel;
		}
		return new None<LagerArtikel>();
	}

	private Option<LagerArtikel> artikelAusDemLager(BindingEntity bindingEntity) {
		if (bindingEntity.isNew()) {
			try {
				BigDecimal artikelDatenId = new BigDecimal(BeanUtils.getProperty(bindingEntity, "artikelDatenId"));
				Option<ArticleInStore> articleInStore = articleService.findNewestEntryInStore(artikelDatenId);
				if (articleInStore.hasValue()) {
					Option<LagerArtikel> lagerArtikel = lagerArticleService.findById(articleInStore.get()
								.getLagerArtikelId());
					if (lagerArtikel.hasValue()) {
						BigDecimal actualBestand = lagerArtikel.get().getActualBestand();
						BigDecimal neuBestand = actualBestand.subtract(BigDecimal.ONE);
						lagerArtikel.get().setActualBestand(neuBestand);
						return lagerArtikel;
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
		return new None<LagerArtikel>();
	}

	public List<LagerArtikel> calculateStoreMovements() {
		List<LagerArtikel> merges = new ArrayList<LagerArtikel>();
		for (BindingEntity bindingEntity : assignments.values()) {
			Option<LagerArtikel> lagerArtikel;
			if (bindingEntity instanceof ArticleInStore) {
				lagerArtikel = artikelInsLager(bindingEntity);
			} else {
				lagerArtikel = artikelAusDemLager(bindingEntity);
			}
			if (lagerArtikel.hasValue()) {
				merges.add(lagerArtikel.get());
			}
		}
		return merges;
	}
	
	public void mergeEntities() {
		for (BindingEntity bindingEntity : assignments.values()) {
			BindingEntity mergedObject = em.merge(bindingEntity);
			try {
				BeanUtils.setProperty(bindingEntity, "id", 	BeanUtils.getProperty(mergedObject,"id"));
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			} catch (InvocationTargetException e) {
				throw new IllegalStateException(e);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public Set<ConstraintViolation<BindingEntity>> validateSubViewFor(Article article) {
		if (assignments.containsKey(article)) {
			BindingEntity bindingEntity = assignments.get(article);
			return validator.validate(bindingEntity);
		} else {
			return new HashSet<ConstraintViolation<BindingEntity>>();
		}
	}

	public void autoCreateLookupData(Article article) {
		if (assignments.containsKey(article)) {
			BindingEntity bindingEntity = assignments.get(article);
			if (bindingEntity instanceof ArticleInstalled) {
				autoCreateArticleInstalledLookupData((ArticleInstalled) bindingEntity);
			} else if (bindingEntity instanceof ArticleMaintenance) {
				autoCreateArticleMaintenanceLookupData((ArticleMaintenance) bindingEntity);
			}
		}
	}

	public void assignId(Article article) {
		if (assignments.containsKey(article)) {
			BindingEntity bindingEntity = assignments.get(article);
			if (bindingEntity instanceof ArticleMaintenance) {
				ArticleMaintenance articleMaintenance = (ArticleMaintenance) bindingEntity;
				if (StringUtils.isEmpty((articleMaintenance.getId()))) {
					SimpleDateFormat sd = new SimpleDateFormat("yyMMdd-HH:mm:ss");
					articleMaintenance.setId(env.getActiveLogGroup() + ":" + sd.format(new Date()));
				}
			}
		}
	}

	public void removeObject(Object object) {
		assignments.remove(object);
	}

	private void autoCreateArticleInstalledLookupData(ArticleInstalled articleInstalled) {
		lookupDataAutoCreator.autoCreateProject(articleInstalled.getProject());
		lookupDataAutoCreator.autoCreateDevice(articleInstalled.getDevice());
		lookupDataAutoCreator.autoCreateGebaeude(articleInstalled.getGebaeude());
		lookupDataAutoCreator.autoCreateRaum(articleInstalled.getGebaeude(), articleInstalled.getRaum());
	}

	private void autoCreateArticleMaintenanceLookupData(ArticleMaintenance articleMaintenance) {
		lookupDataAutoCreator.autoCreateProject(articleMaintenance.getProject());
		lookupDataAutoCreator.autoCreateDevice(articleMaintenance.getDevice());
		lookupDataAutoCreator.autoCreateKeyword(articleMaintenance.isHardware(), articleMaintenance.getKeywords());
	}

}
