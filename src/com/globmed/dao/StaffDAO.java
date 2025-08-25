package com.globmed.dao;

import com.globmed.model.HibernateUtil;
import com.globmed.model.Staff;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Hansana
 */
public class StaffDAO {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public void save(Staff staff) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(staff);
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

    public Staff findByUsername(String username) {
        try {
            Session session = sessionFactory.openSession();
            return (Staff) session.createQuery("FROM Staff WHERE username = :username")
                    .setParameter("username", username)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Staff> findAll() {
        try {
            Session session = sessionFactory.openSession();
            return session.createQuery("FROM Staff").list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
