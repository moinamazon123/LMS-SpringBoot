package com.maps.yolearn.util.primarykey;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author KOTARAJA
 */
@Component
public class CustomPKGenerator {

    @Autowired
    private SessionFactory sessionFactory;

    public Serializable generate(Class clss, String prefix) {
        Serializable pkID = String.format("%s", prefix + "000001");
        String className = clss.getSimpleName();
        try {

            Session session = sessionFactory.openSession();
            session.createQuery(String.format("%s", "from " + className + " obj"));
            Query q2 = session.createQuery(String.format("%s", "select max(obj."
                    + this.getPrimaryKeyColumn(clss) + ") from " + className + " obj"));
            Object obj = q2.uniqueResult();
            String id = obj.toString();
            String sufix = id.substring(prefix.length());
            int x = Integer.parseInt(sufix);
            x = x + 1;
            if (x <= 9) {
                pkID = String.format("%s", prefix + "00000" + x);
            } else if (x <= 99) {
                pkID = String.format("%s", prefix + "0000" + x);
            } else if (x <= 999) {
                pkID = String.format("%s", prefix + "000" + x);
            } else if (x <= 9999) {
                pkID = String.format("%s", prefix + "00" + x);
            } else if (x <= 99999) {
                pkID = String.format("%s", prefix + "0" + x);
            } else if (x <= 999999) {
                pkID = String.format("%s", prefix + x);
            }
        } catch (Exception exception) {
            exception.getMessage();
        }
        return pkID;
    }

    /**
     * Returns primary key column of Entity class
     *
     * @param clss
     * @return
     */
    private String getPrimaryKeyColumn(Class clss) {
        String fieldName = null;
        Field[] fields = clss.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                fieldName = field.getName();
                break;
            }
        }
        return fieldName;
    }
}
