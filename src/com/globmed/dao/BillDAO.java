package com.globmed.dao;

import com.globmed.model.Bill;
import com.globmed.model.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Hansana
 */
public class BillDAO {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public void save(Bill bill) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(bill);
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

    public Bill findById(Long id) {
        try {
            Session session = sessionFactory.openSession();
            return (Bill) session.get(Bill.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Bill> findAll() {
        try {
            Session session = sessionFactory.openSession();
            return session.createQuery("FROM Bill").list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
