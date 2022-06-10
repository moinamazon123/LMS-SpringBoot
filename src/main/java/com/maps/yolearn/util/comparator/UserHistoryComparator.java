package com.maps.yolearn.util.comparator;

import com.maps.yolearn.model.user.UserHistory;

import java.util.Comparator;

/**
 * @author PREMNATH
 */
public class UserHistoryComparator implements Comparator<UserHistory> {

    @Override
    public int compare(UserHistory o1, UserHistory o2) {
//        return Integer.parseInt(o2.getSessionId()) - Integer.parseInt(o1.getSessionId());
        return o2.getSessionId().compareTo(o1.getSessionId());
    }

}
