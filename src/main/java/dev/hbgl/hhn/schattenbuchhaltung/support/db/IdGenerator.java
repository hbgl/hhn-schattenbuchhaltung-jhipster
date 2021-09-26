package dev.hbgl.hhn.schattenbuchhaltung.support.db;

import java.io.Serializable;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class IdGenerator {

    /**
     * Get the next IDs for the given entity class.
     * @param <T>
     * @param entityManager
     * @param entityClass
     * @param count
     * @return The next IDs.
     */
    public static <T> Serializable[] next(EntityManager entityManager, Class<T> entityClass, int count) {
        // The typical Java overengineered and convoluted madness.
        var ids = new Serializable[count];
        var session = entityManager.unwrap(Session.class);
        var sfi = (SessionFactoryImplementor) session.getSessionFactory();
        var ig = sfi.getIdentifierGenerator(entityClass.getName());
        var ssci = entityManager.unwrap(SharedSessionContractImplementor.class);
        for (var i = 0; i < count; i++) {
            ids[i] = ig.generate(ssci, null);
        }
        return ids;
    }
}
