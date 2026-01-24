package com.janilla.websitetemplate.backend;

import java.util.List;
import java.util.Set;

import com.janilla.backend.cms.Document;
import com.janilla.backend.cms.DocumentReference;
import com.janilla.backend.cms.DocumentStatus;
import com.janilla.backend.persistence.Crud;
import com.janilla.backend.persistence.CrudObserver;
import com.janilla.backend.persistence.Persistence;
import com.janilla.java.Reflection;

public class SearchObserver<D extends Document<?>> implements CrudObserver<D> {

	protected final List<Class<?>> types;

	protected final Persistence persistence;

	public SearchObserver(List<Class<?>> types, Persistence persistence) {
		this.types = types;
		this.persistence = persistence;
	}

	@Override
	public void afterCreate(D entity) {
		var d = (Document<?>) entity;
		var dc = d.getClass();
		if (types.contains(dc) && d.documentStatus() == DocumentStatus.PUBLISHED) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			var r = new DocumentReference(dc, d.id());
			crud().create(Reflection.copy(d, new SearchResult(null, r, null, null, null, null, null, null, null, null),
					y -> !Set.of("id", "document").contains(y)));
		}
	}

	@Override
	public void afterUpdate(D entity1, D entity2) {
		var d1 = (Document<?>) entity1;
		var d2 = (Document<?>) entity2;
		var dc = d1.getClass();
		if (types.contains(dc)) {
			switch (d1.documentStatus()) {
			case DRAFT:
				if (d2.documentStatus() == d1.documentStatus())
					;
				else {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					var r = new DocumentReference(dc, d2.id());
					crud().create(Reflection.copy(d2,
							new SearchResult(null, r, null, null, null, null, null, null, null, null),
							y -> !Set.of("id", "document").contains(y)));
				}
				break;
			case PUBLISHED:
				if (d2.documentStatus() == d1.documentStatus()) {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					var r = new DocumentReference(dc, d2.id());
					crud().update(crud().find("document", r),
							x -> Reflection.copy(d2, x, y -> !Set.of("id", "document").contains(y)));
				} else
					crud().delete(crud().find("document", d2.id()));
				break;
			}
		}
	}

	@Override
	public void afterDelete(D entity) {
		var d = (Document<?>) entity;
		var dc = d.getClass();
		if (types.contains(dc) && d.documentStatus() == DocumentStatus.PUBLISHED) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			var r = new DocumentReference(dc, d.id());
			crud().delete(crud().find("document", r));
		}
	}

	protected Crud<Long, SearchResult> crud() {
		return persistence.crud(SearchResult.class);
	}
}
