package org.example.lab7.repository.paging;


import org.example.lab7.domain.Entity;
import org.example.lab7.repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    //returneaza o pagina
    Page<E> findAll(Pageable pageable);
}
