package com.maps.yolearn.dao;

import org.hibernate.*;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author KOTARAJA
 * @author PREMNATH
 */
@Component
public class EntityDAOImpl implements EntityDAO {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public int save(Object object) {
        int x;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.save(object);
            tx.commit();
            x = 1;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            x = 0;
        }
        return x;
    }

    @Override
    public String saveOrUpdate(Object... objects) {
        Transaction transaction = null;
        String confirmMsg;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            for (Object obj : objects) {
                session.save(obj);
            }
            transaction.commit();
            confirmMsg = "Data saved successfully.";
        } catch (ConstraintViolationException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            confirmMsg = "";
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            confirmMsg = "";
        }
        return confirmMsg;
    }

    @Override
    public List<Object> getObject(Class clss, Map<String, Object> cri) {
        List<Object> objects = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(clss);
            Set<String> keys = cri.keySet();
            for (String key : keys) {
                Object value = cri.get(key);
                criteria.add(Restrictions.eq(key, value));
            }
            List<Object> obj = criteria.list();
            for (Object ob : obj) {
                objects.add(ob);
            }
        } catch (Exception e) {
        }
        return objects;
    }

    @Override
    public List<Object> getObject(Class clss) {
        System.out.println("inside sao");
        List<Object> objects = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(clss);

            List<Object> obj = criteria.list();
            for (Object ob : obj) {
                objects.add(ob);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    @Override
    public String getObject1(Class clss) {
        String maxAge = "";
        try (Session session = sessionFactory.openSession()) {

            Criteria criteria = session
                    .createCriteria(clss)
                    .setProjection(Projections.max("comboId"));
            maxAge = (String) criteria.uniqueResult();

        } catch (Exception e) {
        }
        return maxAge;
    }

    @Override
    public int delete(Class className, Map<String, Object> cri) {
        int x = 0;
//        List<Object> objects = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(className);
            Set<String> keys = cri.keySet();
            for (String key : keys) {
                Object value = cri.get(key);
                criteria.add(Restrictions.eq(key, value));
            }
            List<Object> obj = criteria.list();
            for (Object ob : obj) {
                Object tObj = ob;
                session.delete(tObj);
                x++;

            }
            transaction.commit();
        } catch (Exception e) {
        }
        return x;
    }

    @Override
    public int update(Object object) {
        int x;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(object);
            tx.commit();
            x = 1;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            x = 0;
        }
        return x;
    }

    @Override
    public List<Object> getByForeignKey(Class clss, String propertyName, String foreignKeyValue) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(clss);
//        criteria.add(Restrictions.eq("grade.gradeId", id));
        criteria.add(Restrictions.eq(propertyName, foreignKeyValue));

        List list = criteria.list();

        if (session.isOpen()) {
            session.close();
        }
        return list;
    }

    @Override
    public int deleteByForeignKey(Class clss, String propertyName, String foreignKeyValue) {
        int x = 0;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(clss);
            criteria.add(Restrictions.eq(propertyName, foreignKeyValue));
            List<Object> obj = criteria.list();
            for (Object ob : obj) {
                Object tObj = ob;
                session.delete(tObj);
                x++;
            }
            transaction.commit();
        } catch (Exception e) {
            x = 0;
        }
        return x;
    }

    @Override
    public List<Object> getObjectsInAscOrder(Class aClass, String propertyName) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(aClass);
        criteria.addOrder(Order.asc(propertyName));
        List<Object> list = criteria.list();
        return list;
    }

    @Override
    public List<Object> getObjectsInDescOrder(Class aClass, String propertyName) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(aClass);
        /*according to dateOfCreation column the list will be ordered*/
        criteria.addOrder(Order.desc(propertyName));
        List<Object> list = criteria.list();
        return list;
    }

    @Override
    public List<Object> getObjectsInDescOrder(Class clss, Map<String, Object> cri, String propertyName) {
        List<Object> list = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Criteria criteria = session.createCriteria(clss);
            Set<String> keys = cri.keySet();
            for (String key : keys) {
                Object value = cri.get(key);
                criteria.add(Restrictions.eq(key, value));
                criteria.addOrder(Order.desc(propertyName));
            }
            list = criteria.list();

        } catch (Exception e) {
        }
        return list;
    }

    @Override
    public List<Object> getObject(String sql) {
//        String sql = "SELECT u.sessionId FROM UserHistory u WHERE u.accountId = '" + studentAccountId + "'";
        List<Object> list = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(sql);
            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Object> getObject(String sql, String dateArgument, Date date) {
//        String SQLlistUpcomingAndLive = "FROM ClassScheduler c WHERE c.scheduledDate > :current ORDER BY c.scheduledDate DESC";
        List<Object> list = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(sql);
//            query.setTimestamp(dateArgument, date);
            list = query.list();
        } catch (Exception e) {
        }
        return list;
    }

    @Override
    public long countObject(String sql) {
//        String sql = "SELECT COUNT(*) FROM Candidate c WHERE c.email = '" + email + "'";
        long count = 0;
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(sql);
            count = (long) query.uniqueResult();
        } catch (Exception e) {
        }
        return count;
    }

    @Override
    public long countObject(String sql, String dateArgument, Date date) {
//        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < :current";
//        query.setTimestamp("current", date);
        long count = 0;
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(sql);
//            query.setTimestamp(dateArgument, date);
            count = (long) query.uniqueResult();
        } catch (Exception e) {
        }
        return count;
    }

    @Override
    public long countObject(String sql, Map<String, Object> map) {
        long count = 0;
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(sql);

            Set<String> keys = map.keySet();
            for (String argKey : keys) {
                Date date = (Date) map.get(argKey);
                query.setTimestamp(argKey, date);
            }
            count = (long) query.uniqueResult();
        } catch (Exception e) {
        }
        return count;
    }

    @Override
    public List<Object> loadByLimit(String q, int firstResult, int maxResult) {
        List<Object> objects;
        try (Session s = sessionFactory.openSession();) {
            Query query = s.createQuery(q);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResult);
            objects = query.list();
        }
        return objects;
    }

    @Override
    public List<Object> loadByLimit(String q, int firstResult, int maxResult, String dateArgument, Date date) {
        List<Object> objects;
        try (Session s = sessionFactory.openSession();) {
            Query query = s.createQuery(q);
//            query.setTimestamp(dateArgument, date);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResult);
            objects = query.list();
        }
        return objects;
    }

    @Override
    public List<Object[]> loadProperties(String sql) {
        List<Object[]> objects;
        try (Session s = sessionFactory.openSession();) {
            Query query = s.createQuery(sql);
            objects = (List<Object[]>) query.list();
        }
        return objects;
    }

    @Override
    public List<Object[]> loadProperties(String sql, String dateArgument, Date date) {
        List<Object[]> objects;
        try (Session s = sessionFactory.openSession();) {
            Query query = s.createQuery(sql);
            query.setTimestamp(dateArgument, date);
            objects = (List<Object[]>) query.list();
        }
        return objects;
    }

    @Override
    public int update(String sql) {
        int result;
        try (Session s = sessionFactory.openSession();) {
            Query query = s.createQuery(sql);
            result = query.executeUpdate();
        }
        return result;
    }

    @Override
    public int update(List<String> sqls) {
        int result = 0;
        try (Session s = sessionFactory.openSession();) {
            for (String sql : sqls) {
                Query query = s.createQuery(sql);
                query.executeUpdate();
                result += 1;
            }
        }
        return result;
    }

    @Override
    public int delete(String sql) {
        int result;
        try (Session s = sessionFactory.openSession();) {
            Query query = s.createQuery(sql);
            result = query.executeUpdate();
        }
        return result;
    }

    @Override
    public int delete(List<String> sqls) {
        int result = 0;
        try (Session s = sessionFactory.openSession();) {
            for (String sql : sqls) {
                Query query = s.createQuery(sql);
                query.executeUpdate();
                result += 1;
            }
        }
        return result;
    }

    @Override
    public List<?> getObjectWithDate(Class<?> clss, Map<String, Object> cri, String propertyName, String startDateStr,
                                     String endDateStr) throws Exception {
        try (Session session = sessionFactory.openSession();) {
            Criteria criteria = session.createCriteria(clss);

            Set<String> keys = cri.keySet();
            keys.forEach((key) -> {
                Object value = cri.get(key);
                criteria.add(Restrictions.eq(key, value));
            });

            Conjunction and = multipleDates(propertyName, startDateStr, endDateStr);
            criteria.add(and);

            return criteria.list();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param propertyNameDate
     * @param startDateStr:    ex- 2019-07-20
     * @param endDateStr:      ex- 2019-07-20
     * @return
     * @throws ParseException
     */
    private Conjunction multipleDates(String propertyNameDate, String startDateStr, String endDateStr)
            throws ParseException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            // Create minDate from startDateStr, ex: 2019-07-20 - 00h00
            Date minDate = formatter.parse(startDateStr);

            // Create maxDate from endDateStr, ex: 2019-07-23 - 00h00
            Date maxDate = formatter.parse(endDateStr);

            // Since maxDate is exclusive, let's make it inclusive by adding 1 day
            maxDate = new Date(maxDate.getTime() + TimeUnit.DAYS.toMillis(1));

            Conjunction and = Restrictions.conjunction();
            and.add(Restrictions.ge(propertyNameDate, minDate));
            and.add(Restrictions.lt(propertyNameDate, maxDate));
            return and;
        } catch (ParseException e) {
            throw e;
        }
    }

    @Override
    public List<Object> getObjectsByNativeSqlQuery(String sql) {
//        String sql = "SELECT u.sessionId FROM UserHistory u WHERE u.accountId = '" + studentAccountId + "'";
        List<Object> list = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            SQLQuery query = session.createSQLQuery(sql);
            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}


