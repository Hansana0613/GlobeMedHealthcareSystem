package com.globmed.dao;

import com.globmed.model.HibernateUtil;
import com.globmed.model.Role;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Hansana
 */
public class RoleDAO {

    public void save(Role role) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(role);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Role findByName(String name) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return (Role) session.createQuery("from Role r where r.name = :name")
                    .setParameter("name", name)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}
