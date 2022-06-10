package com.maps.yolearn.service;

import com.maps.yolearn.dao.EntityDAO;
import com.maps.yolearn.model.user.StudentAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author MAPS
 */
@Service
public class FilterServiceImpl implements FilterService {

    @Autowired
    private EntityDAO dao;

    @Override
    public List<StudentAccount> sortStudents_DateOfReg_Grade(Map<String, Object> map) throws Exception {
        try {
            List<StudentAccount> listStudentAccount = new ArrayList<>();

            String gradeName = (String) map.get("gradeName");
            String gradeId = "";
            Map<String, Object> filter = new HashMap<>();

            if (gradeName != null) {
                List<Object> gradeObjectList = dao.getObject(String.format("%s", "SELECT g.gradeId FROM Grade g WHERE g.gradeName = '" + gradeName + "'"));
                if (gradeObjectList.size() > 0) {
                    gradeId = (String) gradeObjectList.get(0);
                    filter.put("gradeId", gradeId);
                }
            }

            Set<String> keySet = map.keySet();
            List<String> dateSet = new ArrayList<>();
            dateSet.add("startDate");
            dateSet.add("endDate");

            if (keySet.containsAll(dateSet)) {
                String startDateStr = (String) map.get("startDate");
                String endDateStr = (String) map.get("endDate");
                listStudentAccount = (List<StudentAccount>) dao.getObjectWithDate(StudentAccount.class, filter, "dateOfCreation", startDateStr, endDateStr);
            } else {
                if (!gradeId.equals("")) {
                    listStudentAccount = (List<StudentAccount>) (Object) dao.getObject(String.format("%s", "FROM StudentAccount s WHERE s.gradeId = '" + gradeId + "'"));
                }
            }

            return listStudentAccount;
        } catch (Exception e) {
            throw e;
        }
    }

}
