package minibank.repository;

import minibank.model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic repository using reflection to detect @Id keys (Practical 13).
 * Thread-safe using ConcurrentHashMap.
 * Can be reused for any entity type, e.g. Repository<Account>, Repository<Customer>.
 */
public class Repository<T> {

    private final Map<String, T> storage = new ConcurrentHashMap<>();

    /**
     * Extracts the @Id field value from the entity using reflection.
     */
    private String extractId(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        Class<?> clazz = entity.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object val = field.get(entity);
                        if (val != null) {
                            return val.toString();
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Could not read @Id field " + field.getName(), e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }

        throw new IllegalArgumentException("No field annotated with @Id found in class hierarchy of " + entity.getClass().getName());
    }

    /**
     * Saves an entity, indexing it by its reflected @Id value.
     */
    public T save(T entity) {
        String id = extractId(entity);
        storage.put(id, entity);
        return entity;
    }

    /**
     * Saves multiple entities at once using varargs (Practical 13).
     */
    @SafeVarargs
    public final void saveAll(T... items) {
        if (items != null) {
            for (T item : items) {
                if (item != null) {
                    save(item);
                }
            }
        }
    }

    /**
     * Finds an entity by its identifier.
     */
    public T findById(String id) {
        if (id == null) return null;
        return storage.get(id);
    }

    /**
     * Returns an ArrayList of all stored entities.
     */
    public ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Deletes an entity by its identifier.
     */
    public boolean delete(String id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    /**
     * Returns the count of stored entities (Supplementary).
     */
    public int count() {
        return storage.size();
    }

    /**
     * Clears all stored entities.
     */
    public void clear() {
        storage.clear();
    }

    /**
     * Generic method with bounded type parameter <N extends Number> (Practical 13).
     * Calculates the arithmetic average of an array of numbers.
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N val : values) {
            if (val != null) {
                sum += val.doubleValue();
            }
        }
        return sum / values.length;
    }
}
