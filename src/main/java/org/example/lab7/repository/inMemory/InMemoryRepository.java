package org.example.lab7.repository.inMemory;


import org.example.lab7.domain.Entity;
import org.example.lab7.repository.Repository;
import org.example.lab7.validator.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Generic repo for saving entities in memory
 * @param <ID>, the type of the IDs of the entities
 * @param <E>, the Entity Objects
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    Map<ID, Optional<E>> entities;

    /**
     * Constructor
     * @param validator, generic validator, specially used for validating the generic entities
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,Optional<E>>();
    }

    public InMemoryRepository() {
        entities=new HashMap<ID,Optional<E>>();
    }

    /**
     * Returns the entity with the given ID or null if it does not exist
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the found entity or null
     */
    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /**
     * @return all the entities in the repository
     */
    @Override
    public Iterable<Optional<E>> findAll() {
        return entities.values();
    }


    @Override
    public boolean save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(Optional.of(entity));
        if(entities.get(entity.getId()) != null) {
            return false;
        }
        else entities.put(entity.getId(), Optional.of(entity));
        return true;
    }

    /**
     * Deletes an entity from the repository, if it exists
     * @param id
     *      id must be not null
     * @return the deleted entity, or null if no entity was deleted
     */
    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = entities.getOrDefault(id, null);
        if (entity != null) {
            entities.remove(id);
        }
        return entity;
    }


    @Override
    public boolean update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");

        if (entities.get(entity.getId()) != null) {
            entities.put(entity.getId(), Optional.of(entity));
            return true;
        }
        return false;

    }

    @Override
    public Iterable<E> getCustomList(String format) {
        return null;
    }
}
