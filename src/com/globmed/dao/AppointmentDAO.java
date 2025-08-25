package com.globmed.dao;

import com.globmed.model.Appointment;
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
public class AppointmentDAO {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public void save(Appointment appointment) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(appointment);
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

    public Appointment findById(Long id) {
        try {
            Session session = sessionFactory.openSession();
            return (Appointment) session.get(Appointment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Appointment> findAll() {
        try {
            Session session = sessionFactory.openSession();
            return session.createQuery("FROM Appointment").list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
