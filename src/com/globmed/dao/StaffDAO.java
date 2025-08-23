package com.globmed.dao;

import com.globmed.model.HibernateUtil;
import com.globmed.model.Staff;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Hansana
 */
public class StaffDAO {

    public void save(Staff staff) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            session.save(staff);
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

    public Staff findByUsername(String username) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return (Staff) session.createQuery("from Staff s where s.username = :username")
                    .setParameter("username", username)
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
