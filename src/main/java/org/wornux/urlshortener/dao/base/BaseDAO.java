package org.wornux.urlshortener.dao.base;

import com.mongodb.lang.NonNull;
import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import java.util.List;
import java.util.Optional;

/**
 * A generic base repository implementation for Morphia. Provides common CRUD operations for
 * entities.
 *
 * @param <T> The type of the entity.
 * @param <K> The type of the entity's ID.
 */
public abstract class BaseDAO<T, K> {
  protected final Datastore datastore;
  protected final Class<T> entityClass;

  /**
   * Constructs a new BaseRepository.
   *
   * @param datastore The Morphia Datastore instance.
   * @param entityClass The class of the entity this repository manages.
   */
  public BaseDAO(@NonNull Datastore datastore, @NonNull Class<T> entityClass) {
    this.datastore = datastore;
    this.entityClass = entityClass;
  }

  /**
   * Finds an entity by its ID.
   *
   * @param id The ID of the entity to find.
   * @return An Optional containing the entity if found, or empty if not found.
   */
  public Optional<T> findById(@NonNull K id) {
    return Optional.ofNullable(datastore.find(entityClass).filter(Filters.eq("_id", id)).first());
  }

  /**
   * Retrieves all entities of the specified type.
   *
   * @return A list of all entities.
   */
  public List<T> findAll() {
    Query<T> query = datastore.find(entityClass);
    return query.iterator().toList();
  }

  /**
   * Saves or updates an entity in the datastore.
   *
   * @param entity The entity to save or update.
   */
  public void save(@NonNull T entity) {
    datastore.save(entity);
  }

  /**
   * Updates an entity in the datastore.
   *
   * @param entity The entity to update.
   */
  public void update(@NonNull T entity) {
    datastore.save(entity);
  }

  /**
   * Deletes an entity by its ID.
   *
   * @param id The ID of the entity to delete.
   */
  public void deleteById(@NonNull K id) {
    datastore.find(entityClass).filter(Filters.eq("_id", id)).delete();
  }
}
