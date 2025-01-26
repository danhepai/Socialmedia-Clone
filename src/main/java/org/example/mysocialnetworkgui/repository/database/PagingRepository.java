package org.example.mysocialnetworkgui.repository.database;

import org.example.mysocialnetworkgui.domain.Entity;
import org.example.mysocialnetworkgui.repository.Repository;
import org.example.mysocialnetworkgui.utils.paging.Page;
import org.example.mysocialnetworkgui.utils.paging.Pageable;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}

