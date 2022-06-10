package com.maps.yolearn.util.comparator;

import com.maps.yolearn.model.user.StudentAccount;

import java.util.Comparator;

/**
 * @author MAPS
 */
public class StudentAccountComparator implements Comparator<StudentAccount> {

    @Override
    public int compare(StudentAccount o1, StudentAccount o2) {
        return o1.getStudentAccountId().compareTo(o2.getStudentAccountId());
    }

}
