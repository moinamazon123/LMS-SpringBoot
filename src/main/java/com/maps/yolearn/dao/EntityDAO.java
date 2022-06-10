package com.maps.yolearn.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author KOTARAJA
 * @author PREMNATH
 */
public interface EntityDAO {

    public int save(Object object);

    public String saveOrUpdate(Object... objects);

    public List<Object> getObject(Class clss, Map<String, Object> cri);

    public List<Object> getObject(Class clss);

    public int delete(Class className, Map<String, Object> cri);

    public int update(Object object);

    public String getObject1(Class clss);

//    public Object update1(Object object);

    /*not in use*/
    public List<Object> getByForeignKey(Class clss, String propertyName, String foreignKeyValue);

    /*not in use*/
    public int deleteByForeignKey(Class clss, String propertyName, String foreignKeyValue);

    public List<Object> getObjectsInAscOrder(Class aClass, String propertyName);

    /*not in use*/
    public List<Object> getObjectsInDescOrder(Class aClass, String propertyName);

    /*not in use*/
    public List<Object> getObjectsInDescOrder(Class clss, Map<String, Object> cri, String propertyName);

    public List<Object> getObject(String sql);

    public List<Object> getObject(String sql, String dateArgument, Date date);

    public long countObject(String sql);

    public long countObject(String sql, String dateArgument, Date date);

    public long countObject(String sql, Map<String, Object> map);

    public List<Object> loadByLimit(String q, int firstResult, int maxResult);

    public List<Object> loadByLimit(String q, int firstResult, int maxResult, String dateArgument, Date date);

    public List<Object[]> loadProperties(String sql);

    public List<Object[]> loadProperties(String sql, String dateArgument, Date date);

    public int update(String sql);

    public int update(List<String> sqls);

    public int delete(String sql);

    public int delete(List<String> sqls);

    public List<?> getObjectWithDate(Class<?> clss, Map<String, Object> cri, String propertyName, String startDateStr,
                                     String endDateStr) throws Exception;

    /**
     * get objects by native sql query
     */
    public List<Object> getObjectsByNativeSqlQuery(String sql);


}
