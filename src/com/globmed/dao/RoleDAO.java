package com.globmed.dao;

import com.globmed.model.HibernateUtil;
import com.globmed.model.Role;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Hansana
 */
public class RoleDAO {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public void save(Role role) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(role);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Role findByName(String name) {
        try {
            Session session = sessionFactory.openSession();
            return (Role) session.createQuery("FROM Role WHERE name = :name")
                    .setParameter("name", name)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Role> findAll() {
        try {
            Session session = sessionFactory.openSession();
            return session.createQuery("FROM Role").list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
