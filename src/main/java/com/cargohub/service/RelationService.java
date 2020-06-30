package com.cargohub.service;

import com.cargohub.entities.Relation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RelationService {

    boolean existsById(Integer id);

    Relation findById(Integer id);

    Relation update(Relation relation);

    Relation save(Relation relation);

    Page<Relation> findAll(Pageable pageable);

    void delete(Integer id);

}
