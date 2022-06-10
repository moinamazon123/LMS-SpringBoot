package com.maps.yolearn.service;

import com.maps.yolearn.model.user.StudentAccount;

import java.util.List;
import java.util.Map;

/**
 * @author MAPS
 */
public interface FilterService {

    public List<StudentAccount> sortStudents_DateOfReg_Grade(Map<String, Object> map) throws Exception;

}
