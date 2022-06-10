package com.maps.yolearn.service;

import com.maps.yolearn.dao.EntityDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author KOTARAJA
 * @author PREMNATH
 */
@Component
public class EntityServiceImpl implements EntityService {

    @Autowired
    private EntityDAO dao;

    @Override
    public int save(Object object) {
        return dao.save(object);
    }

    @Override
    public String saveOrUpdate(Object... objects) {
        return dao.saveOrUpdate(objects);
    }

    @Override
    public List<Object> getObject(Class clss, Map<String, Object> cri) {
        return dao.getObject(clss, cri);
    }

    @Override
    public List<Object> getObject(Class clss) {
        return dao.getObject(clss);
    }

    @Override
    public int delete(Class className, Map<String, Object> cri) {
        return dao.delete(className, cri);
    }

    @Override
    public int update(Object object) {
        return dao.update(object);
    }

    //    @Override
//    public Object update1(Object object) {
//        return dao.update1(object);
//    }
    @Override
    public List<Object> getByForeignKey(Class clss, String propertyName, String foreignKeyValue) {
        return dao.getByForeignKey(clss, propertyName, foreignKeyValue);
    }

    @Override
    public int deleteByForeignKey(Class clss, String propertyName, String foreignKeyValue) {
        return dao.deleteByForeignKey(clss, propertyName, foreignKeyValue);
    }

    @Override
    public List<Object> getObjectsInAscOrder(Class aClass, String propertyName) {
        return dao.getObjectsInAscOrder(aClass, propertyName);
    }

    @Override
    public List<Object> getObjectsInDescOrder(Class aClass, String propertyName) {
        return dao.getObjectsInDescOrder(aClass, propertyName);
    }

    @Override
    public List<Object> getObjectsInDescOrder(Class clss, Map<String, Object> cri, String propertyName) {
        return dao.getObjectsInDescOrder(clss, cri, propertyName);
    }

    @Override
    public List<Object> getObject(String sql) {
        return dao.getObject(sql);
    }

    @Override
    public List<Object> getObject(String sql, String dateArgument, Date date) {
        return dao.getObject(sql, dateArgument, date);
    }

    @Override
    public long countObject(String sql) {
        return dao.countObject(sql);
    }

    @Override
    public long countObject(String sql, String dateArgument, Date date) {
        return dao.countObject(sql, dateArgument, date);
    }

    @Override
    public long countObject(String sql, Map<String, Object> map) {
        return dao.countObject(sql, map);
    }

    @Override
    public List<Object> loadByLimit(String q, int firstResult, int maxResult) {
        return dao.loadByLimit(q, firstResult, maxResult);
    }

    @Override
    public List<Object[]> loadProperties(String sql) {
        return dao.loadProperties(sql);
    }

    @Override
    public List<Object[]> loadProperties(String sql, String dateArgument, Date date) {
        return dao.loadProperties(sql, dateArgument, date);
    }

    @Override
    public List<Object> loadByLimit(String q, int firstResult, int maxResult, String dateArgument, Date date) {
        return dao.loadByLimit(q, firstResult, maxResult, dateArgument, date);
    }

    @Override
    public String getObject1(Class clss) {
        return dao.getObject1(clss);
    }

    @Override
    public int update(String sql) {
        return dao.update(sql);
    }

    @Override
    public int update(List<String> sqls) {
        return dao.update(sqls);
    }

    @Override
    public int delete(String sql) {
        return dao.delete(sql);
    }

    @Override
    public int delete(List<String> sqls) {
        return dao.delete(sqls);
    }

    @Override
    public List<Object> getObjectsByNativeSqlQuery(String sql) {
        return dao.getObjectsByNativeSqlQuery(sql);
    }

}
