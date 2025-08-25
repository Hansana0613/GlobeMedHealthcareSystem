package com.globmed.dao;

import com.globmed.model.HibernateUtil;
import com.globmed.model.Patient;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Hansana
 */
public class PatientDAO {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory(); // Assuming HibernateUtil is updated

    public void save(Patient patient) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(patient);
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

    public Patient findById(Long id) {
        try {
            Session session = sessionFactory.openSession();
            return (Patient) session.get(Patient.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Patient> findAll() {
        try {
            Session session = sessionFactory.openSession();
            return session.createQuery("FROM Patient").list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
