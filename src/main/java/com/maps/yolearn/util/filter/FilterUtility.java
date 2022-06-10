package com.maps.yolearn.util.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PREMNATH
 */
public class FilterUtility {

    /**
     * METHOD for generating IN CLAUSE values
     *
     * @param list
     * @return
     */
    public String getIN_CLAUSE_VALUES(List<String> list) {
        String s = String.format("%s", "");
        for (String string : list) {
            s = String.format("%s", s + "'" + string + "',");
            s = String.format("%s", s.substring(0, s.length() - 1));
            if (s.contains("''")) {
                s = String.format("%s", s.replace("''", "', '"));
            }
        }
        return s;
    }

    private String dateRangeSQL_StudentAccount(Map<String, Object> dateRange) {
        String sqlDateRange = "";
        if (dateRange != null) {
            String startDate = (String) dateRange.get("startDate");
            String endDate = (String) dateRange.get("endDate");

            if (startDate.equals(endDate)) {
                sqlDateRange = String.format("%s", " AND DATE(s.dateOfCreation) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " AND DATE(s.dateOfCreation) >= '" + startDate + "' AND DATE(s.dateOfCreation) <= '" + endDate + "'");
            }
        }
        return sqlDateRange;
    }

    private String dateRangeSQL_ClassSchedular(String startDate, String endDate) {
        String sqlDateRange = "";
        if ((startDate != null) && (endDate != null)) {
            boolean b = startDate.equals(endDate);
            if (b) {
                sqlDateRange = String.format("%s", " AND DATE(c.scheduledDate) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " AND DATE(c.scheduledDate) >= '" + startDate + "' AND DATE(c.scheduledDate) <= '" + endDate + "'");
            }
        }
        return sqlDateRange;
    }

    /**
     * METHOD for getAllschedular
     *
     * @param accessTo
     * @param presenterDisplayName
     * @param gradeFilters
     * @param syllabusFilters
     * @param subjectFilters
     * @param chapterFilters
     * @param startDate
     * @param endDate
     * @param currentDate
     * @return
     */
    public Map<String, String> getAllschedularFilter(String accessTo, String presenterDisplayName, List<String> gradeFilters, List<String> syllabusFilters, List<String> subjectFilters, List<String> chapterFilters, String startDate, String endDate, String currentDate, String dateOrder, String text) {

        /*count of recorded*/
        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (accessTo != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.accessTo = '" + accessTo + "'");
        }

        if (presenterDisplayName != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
        }
        if (text != null) {
            SQLcountRecorded = SQLcountRecorded + " and c.title LIKE '" + text + "%'";
        }

        String sqlDateRange = dateRangeSQL_ClassSchedular(startDate, endDate);

        if (!sqlDateRange.equals("")) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + sqlDateRange);
        }


        /*list of recorded classes*/
        String SQLRecorded = "FROM ClassScheduler c WHERE c.endDate < '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (accessTo != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.accessTo = '" + accessTo + "'");
        }

        if (presenterDisplayName != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
        }

        if (text != null) {
            SQLRecorded = SQLRecorded + " and c.title LIKE '" + text + "%'";
        }

        if (!sqlDateRange.equals("")) {
            SQLRecorded = String.format("%s", SQLRecorded + sqlDateRange);
        }
        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate " + dateOrder + "");

        System.out.println("SQLRecorded " + SQLRecorded);
        /*list of upcoming classes*/
        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.scheduledDate >= '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }
//        if (text != null) {
//            SQLlistUpcoming = SQLlistUpcoming + " and c.title LIKE '" + text + "%'";
//        }

        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate DESC");

        /*list of live classes*/
        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < '" + currentDate + "' AND c.endDate > '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }
//        if (text != null) {
//            SQLlistUpcoming = SQLlistUpcoming + " and c.title LIKE '" + text + "%'";
//        }
        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");

        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
        SQLFilter.put("SQLRecorded", SQLRecorded);
        SQLFilter.put("SQLRecordedTotalCount", "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + currentDate + "'");

        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
        SQLFilter.put("SQLUpcomingCount", "select count(*) " + SQLlistUpcoming);
        SQLFilter.put("SQLUpcomingTotalCount", "FROM ClassScheduler c WHERE c.scheduledDate >= '" + currentDate + "'");

        SQLFilter.put("SQLlistLive", SQLlistLive);
        SQLFilter.put("SQLiveCount", "select count(*) " + SQLlistLive);
        SQLFilter.put("SQLiveTotalCount", "FROM ClassScheduler c WHERE c.scheduledDate < '" + currentDate + "' AND c.endDate > '" + currentDate + "'");

        return SQLFilter;
    }

    public Map<String, String> getAllschedularFilter(String presenterDisplayName, List<String> gradeFilters, List<String> syllabusFilters, List<String> subjectFilters, List<String> chapterFilters, List<String> batchFilters) {
        /*count of recorded*/
        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < :current";

        if (gradeFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        if (presenterDisplayName != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
        }

        /*list of recorded classes*/
        String SQLRecorded = "FROM ClassScheduler c WHERE c.endDate < :current";

        if (gradeFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        if (presenterDisplayName != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
        }

        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate DESC");

        /*list of upcoming classes*/
        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.scheduledDate >= :current";

        if (gradeFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate DESC");

        /*list of live classes*/
        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < :current AND c.endDate > :current";

        if (gradeFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");

        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
        SQLFilter.put("SQLRecorded", SQLRecorded);
        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
        SQLFilter.put("SQLlistLive", SQLlistLive);

        return SQLFilter;
    }

    /**
     * METHOD for listOfliveclassBygradeName
     *
     * @param gradeName
     * @param syllabusFilters
     * @param subjectFilters
     * @param chapterFilters
     * @param batchFilters
     * @param currentDate
     * @return
     */
    public Map<String, String> listOfliveclassBygradeNameFilter(String gradeName, List<String> syllabusFilters, List<String> subjectFilters, List<String> chapterFilters, List<String> batchFilters, String currentDate) {
        /*count of recorded*/
        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + currentDate + "' AND c.gradeName = '" + gradeName + "'";

        if (syllabusFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.accessTo <> 'Guest'");

        /*list of recorded classes*/
        String SQLRecorded = "FROM ClassScheduler c WHERE c.endDate < '" + currentDate + "' AND c.gradeName = '" + gradeName + "'";

        if (syllabusFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLRecorded = String.format("%s", SQLRecorded + " AND c.accessTo <> 'Guest' ORDER BY c.scheduledDate DESC");

        /*list of upcoming classes*/
        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.gradeName = '" + gradeName + "' AND c.scheduledDate >= '" + currentDate + "' AND c.accessTo <> 'Guest'";

        if (syllabusFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate ASC");

        /*list of live classes*/
        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < '" + currentDate + "' AND c.endDate > '" + currentDate + "' AND c.gradeName = '" + gradeName + "' AND c.accessTo <> 'Guest'";

        if (syllabusFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");

        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
        SQLFilter.put("SQLRecorded", SQLRecorded);
        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
        SQLFilter.put("SQLlistLive", SQLlistLive);

        return SQLFilter;
    }

    /**
     * method for listOfStudents
     *
     * @param gradeFilters
     * @param dateRange
     * @return
     */
    public Map<String, String> listOfStudentsFilter(List<String> gradeFilters, Map<String, Object> dateRange, String dateOrder, String text,
                                                    List<String> programFilters, String gradeOrder) {

        String startDate = (String) dateRange.get("startDate");
        String endDate = (String) dateRange.get("endDate");

        /*count of studentAccounts*/
        String SQLcountStudentAccount = "SELECT COUNT(*) FROM StudentAccount s WHERE s.subscribeId IS NOT NULL";

//        if (gradeFilters != null) {
//            SQLcountStudentAccount = String.format("%s", SQLcountStudentAccount + " AND s.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
//        }
//
//        String sqlDateRange = dateRangeSQL_StudentAccount(dateRange);
//
//        System.out.println("sqlDateRange  " + sqlDateRange);
//
//        if (!sqlDateRange.equals("")) {
//            SQLcountStudentAccount = String.format("%s", SQLcountStudentAccount + sqlDateRange);
//        }

        /*list of studentAccount*/
        String SQLlistStudentAccount = "FROM StudentAccount s WHERE s.subscribeId IS NOT NULL";

        if (gradeFilters != null) {
            SQLlistStudentAccount = String.format("%s", SQLlistStudentAccount + " AND s.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }
        if (programFilters != null) {
            SQLlistStudentAccount = String.format("%s", SQLlistStudentAccount + " AND s.syllabusId IN (" + getIN_CLAUSE_VALUES(programFilters) + ")");
        }

//        if (!sqlDateRange.equals("")) {
//            SQLlistStudentAccount = String.format("%s", SQLlistStudentAccount + sqlDateRange);
//        }
//        SQLlistStudentAccount = String.format("%s", SQLlistStudentAccount + " ORDER BY s.dateOfCreation DESC");
        if ("all".equals(startDate) && !"all".equals(endDate)) {
            SQLlistStudentAccount = SQLlistStudentAccount + " and DATE(s.dateOfCreation) <  '" + endDate + "'";
        }
        if (!"all".equals(startDate) && "all".equals(endDate)) {
            SQLlistStudentAccount = SQLlistStudentAccount + " and DATE(s.dateOfCreation) between  '" + startDate + "' AND current_date() ";
        }

        if (!"all".equals(startDate) && !"all".equals(endDate)) {
            SQLlistStudentAccount = SQLlistStudentAccount + " and DATE(s.dateOfCreation) between  '" + startDate + "' AND '" + endDate + "' ";
        }
//
        if (text != null) {
            SQLlistStudentAccount = String.format("%s", SQLlistStudentAccount + " and (s.firstName LIKE '" + text + "%'"
                    + " or s.lastName LIKE '" + text + "%' or s.primaryEmail LIKE '" + text + "%' or s.schoolName LIKE '" + text + "%'");
        }
//
////        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate DESC");
        SQLlistStudentAccount = SQLlistStudentAccount + ") order by s.gradeId " + gradeOrder + ",s.dateOfCreation " + dateOrder + " ";

        System.out.println("SQLlistStudentAccount  " + SQLlistStudentAccount);
        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountStudentAccount", SQLcountStudentAccount);
        SQLFilter.put("SQLlistStudentAccount", SQLlistStudentAccount);

        return SQLFilter;
    }

    /**
     * METHOD for getallschedularForGuest
     *
     * @param gradeName
     * @param accessTo
     * @param syllabusFilters
     * @param subjectFilters
     * @param chapterFilters
     * @param currentDate
     * @return
     */
    public Map<String, String> getallschedularForGuestFilter(String gradeName, String accessTo, List<String> syllabusFilters, List<String> subjectFilters, List<String> chapterFilters, String currentDate) {
        /*count of recorded*/
        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.gradeName='" + gradeName + "' AND c.accessTo='" + accessTo + "' AND c.endDate < '" + currentDate + "'";

        if (syllabusFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        /*list of recorded classes*/
        String SQLRecorded = "FROM ClassScheduler c WHERE c.gradeName='" + gradeName + "' AND c.accessTo='" + accessTo + "' AND c.endDate < '" + currentDate + "'";

        if (syllabusFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate DESC");

        /*list of upcoming classes*/
        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.gradeName ='" + gradeName + "' AND c.accessTo='" + accessTo + "' AND c.scheduledDate >= '" + currentDate + "'";

        if (syllabusFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate ASC");

        /*list of live classes*/
        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < '" + currentDate + "' AND c.endDate > '" + currentDate + "' AND c.gradeName = '" + gradeName + "' AND c.accessTo='" + accessTo + "'";

        if (syllabusFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");

        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
        SQLFilter.put("SQLRecorded", SQLRecorded);
        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
        SQLFilter.put("SQLlistLive", SQLlistLive);

        return SQLFilter;
    }

    /**
     * METHOD for listOfClassSchedulerByBatchId
     *
     * @param presenterDisplayName
     * @param batchId
     * @param subjectFilters
     * @param chapterFilters
     * @param scheduledDate
     * @param freeclass
     * @param current
     * @return
     */
    public Map<String, String> listOfClassSchedulerByBatchIdFilter(String presenterDisplayName, List<String> syllabusFilter,
                                                                   List<String> subjectFilters, List<String> chapterFilters, String batchId, String scheduledDate, List<String> freeclass,
                                                                   String current, String text, String startDate, String enddate, String dateOrder, String titleOrder) {

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String current = sdf.format(new Date());
        /*count of recorded*/
        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.scheduledDate < '" + current + "' AND  c.batchId  IN (SELECT s.batchId FROM StudentSubscription s where s.studentAccountId='" + batchId + "') AND c.accessTo IN ('Members', 'Both')";

//        if (freeclass != null) {
//            SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.scheduledDate < '" + current + "' AND  c.batchId  IN (SELECT s.batchId FROM StudentSubscription s where s.studentAccountId='" + batchId + "') AND c.accessTo IN ('Members', 'Both')";
//        } else {
//            SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + current + "' AND  c.batchId  IN ('" + batchId + "', " + getIN_CLAUSE_VALUES(freeclass) + ")   AND c.accessTo IN ('Members', 'Both')";
//
//        }
////        if (freeclass != null) {
//            SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + current + "' AND  c.batchId  IN ('" + batchId + "', " + getIN_CLAUSE_VALUES(freeclass) + ")   AND c.accessTo IN ('Members', 'Both')";
//        } else {
//            SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + current + "' AND  c.batchId  IN ('" + batchId + "', " + getIN_CLAUSE_VALUES(freeclass) + ")   AND c.accessTo IN ('Members', 'Both')";
//
//        }
        if (subjectFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (syllabusFilter != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilter) + ")");
        }

        if (presenterDisplayName != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
        }

        if ("all".equals(startDate) && !"all".equals(enddate)) {
            SQLcountRecorded = SQLcountRecorded + " and DATE(c.scheduledDate) <  '" + enddate + "'";
        }
        if (!"all".equals(startDate) && "all".equals(enddate)) {
            SQLcountRecorded = SQLcountRecorded + " and DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
        }

        if (!"all".equals(startDate) && !"all".equals(enddate)) {
            SQLcountRecorded = SQLcountRecorded + " and DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
        }

        if (text != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " and c.title LIKE '" + text + "%'");
        }

//         if (presenterDisplayName != null) {
//            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
//        }
//        if (!"".equals(scheduledDate)) {
//            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'");
//        }
        System.out.println("SQLcountRecorded" + SQLcountRecorded);

        /*list of recorded classes*/
//        String SQLRecorded = "FROM ClassScheduler c WHERE c.endDate < '" + current + "' AND c.batchId  IN ('" + batchId + "', " + getIN_CLAUSE_VALUES(freeclass) + ") AND c.accessTo IN ('Members', 'Both')";
        String SQLRecorded = "FROM ClassScheduler c WHERE c.scheduledDate < '" + current + "' AND  c.batchId  IN "
                + "(SELECT s.batchId FROM StudentSubscription s where s.studentAccountId='" + batchId + "')"
                + "AND c.accessTo IN ('Members', 'Both')";

        if (subjectFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }
        if (syllabusFilter != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilter) + ")");
        }

        if (presenterDisplayName != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
        }

//        if (!"".equals(scheduledDate)) {
//            SQLRecorded = String.format("%s", SQLRecorded + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'");
//        }
        if ("all".equals(startDate) && !"all".equals(enddate)) {
            SQLRecorded = SQLRecorded + " and DATE(c.scheduledDate) <  '" + enddate + "'";
        }
        if (!"all".equals(startDate) && "all".equals(enddate)) {
            SQLRecorded = SQLRecorded + " and DATE(c.scheduledDate) between  '" + startDate + "' AND current_date() ";
        }

        if (!"all".equals(startDate) && !"all".equals(enddate)) {
            SQLRecorded = SQLRecorded + " and DATE(c.scheduledDate) between  '" + startDate + "' AND '" + enddate + "' ";
        }

        if (text != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " and c.title LIKE '" + text + "%'");
        }

//        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate DESC");
        SQLRecorded = SQLRecorded + " order by c.scheduledDate " + dateOrder + ",c.title " + titleOrder + " ";

        System.out.println("SQLRecorded " + SQLRecorded);

        /*list of upcoming classes*/
        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.scheduledDate >= '" + current + "' AND  c.batchId  IN "
                + "(SELECT s.batchId FROM StudentSubscription s where s.studentAccountId='" + batchId + "')"
                + "AND c.accessTo IN ('Members', 'Both')";
//        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.scheduledDate >= '" + current + "' AND  c.batchId  IN ('" + batchId + "', " + getIN_CLAUSE_VALUES(freeclass) + ") AND c.accessTo IN ('Members', 'Both')";
        if (subjectFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate ASC");

        System.out.println("SQLlistUpcoming " + SQLlistUpcoming);

        /*list of live classes*/
//        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < '" + current + "' AND c.endDate > '" + current + "' AND c.batchId  IN ('" + batchId + "', " + getIN_CLAUSE_VALUES(freeclass) + ") AND c.accessTo IN ('Members', 'Both')";
        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < '" + current + "' AND c.endDate > '" + current + "' AND  c.batchId  IN (SELECT s.batchId FROM StudentSubscription s where s.studentAccountId='" + batchId + "')"
                + "AND c.accessTo IN ('Members', 'Both')";

        if (subjectFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");

        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
        SQLFilter.put("SQLRecorded", SQLRecorded);
        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
        SQLFilter.put("SQLlistLive", SQLlistLive);
        System.out.println(SQLRecorded);

        return SQLFilter;
    }
//    /**
//     * METHOD for listOfClassSchedulerByBatchId
//     *
//     * @param presenterDisplayName
//     * @param batchId
//     * @param subjectFilters
//     * @param chapterFilters
//     * @param scheduledDate
//     * @param freeclass
//     * @return
//     */
//    public Map<String, String> listOfClassSchedulerByBatchIdFilter(String presenterDisplayName, List<String> subjectFilters, List<String> chapterFilters, String batchId, String scheduledDate,List<String> freeclass) {
//        /*count of recorded*/
//        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < :current AND c.batchId = '" + batchId + "' AND c.accessTo IN ('Members', 'Both')";
//
//        if (subjectFilters != null) {
//            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
//        }
//
//        if (chapterFilters != null) {
//            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
//        }
//
//        if (presenterDisplayName != null) {
//            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
//        }
//
//        if (!"".equals(scheduledDate)) {
//            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'");
//        }
//
//        /*list of recorded classes*/
//        String SQLRecorded = "FROM ClassScheduler c WHERE c.endDate < :current AND c.batchId = '" + batchId + "' AND c.accessTo IN ('Members', 'Both')";
//
//        if (subjectFilters != null) {
//            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
//        }
//
//        if (chapterFilters != null) {
//            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
//        }
//
//        if (presenterDisplayName != null) {
//            SQLRecorded = String.format("%s", SQLRecorded + " AND c.presenterDisplayName = '" + presenterDisplayName + "'");
//        }
//
//        if (!"".equals(scheduledDate)) {
//            SQLRecorded = String.format("%s", SQLRecorded + " AND DATE(c.scheduledDate) = '" + scheduledDate + "'");
//        }
//
//        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate DESC");
//
//        /*list of upcoming classes*/
//        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.scheduledDate >= :current AND c.batchId = '" + batchId + "' AND c.accessTo IN ('Members', 'Both')";
//
//        if (subjectFilters != null) {
//            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
//        }
//
//        if (chapterFilters != null) {
//            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
//        }
//
//        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate ASC");
//
//        /*list of live classes*/
//        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < :current AND c.endDate > :current AND c.batchId = '" + batchId + "' AND c.accessTo IN ('Members', 'Both')";
//
//        if (subjectFilters != null) {
//            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
//        }
//
//        if (chapterFilters != null) {
//            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
//        }
//
//        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");
//
//        /*put all sql(s) to map*/
//        Map<String, String> SQLFilter = new HashMap<>();
//        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
//        SQLFilter.put("SQLRecorded", SQLRecorded);
//        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
//        SQLFilter.put("SQLlistLive", SQLlistLive);
//
//        return SQLFilter;
//    }

    /**
     * METHOD for getPresentUrlByTeacherId
     *
     * @param teacherId
     * @param gradeFilters
     * @param syllabusFilters
     * @param subjectFilters
     * @param chapterFilters
     * @param batchFilters
     * @param currentDate
     * @return
     */
    public Map<String, String> getPresentUrlByTeacherIdFilter(String teacherId, List<String> gradeFilters, List<String> syllabusFilters, List<String> subjectFilters, List<String> chapterFilters, List<String> batchFilters, String currentDate, String text) {
        /*count of recorded*/
        String SQLcountRecorded = "SELECT COUNT(*) FROM ClassScheduler c WHERE c.teacherId = '" + teacherId + "' AND c.endDate < '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLcountRecorded = String.format("%s", SQLcountRecorded + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }
        if (text != null) {
            SQLcountRecorded = SQLcountRecorded + " and c.title LIKE '" + text + "%'";
        }

        /*list of recorded classes*/
        String SQLRecorded = "FROM ClassScheduler c WHERE c.teacherId = '" + teacherId + "' AND c.endDate < '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLRecorded = String.format("%s", SQLRecorded + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }
        if (text != null) {
            SQLRecorded = SQLRecorded + " and c.title LIKE '" + text + "%'";
        }

        SQLRecorded = String.format("%s", SQLRecorded + " ORDER BY c.scheduledDate DESC");

        /*list of upcoming classes*/
        String SQLlistUpcoming = "FROM ClassScheduler c WHERE c.teacherId = '" + teacherId + "' AND c.scheduledDate >= '" + currentDate + "'";

        if (gradeFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLlistUpcoming = String.format("%s", SQLlistUpcoming + " ORDER BY c.scheduledDate ASC");

        /*list of live classes*/
        String SQLlistLive = "FROM ClassScheduler c WHERE c.scheduledDate < '" + currentDate + "' AND c.endDate > '" + currentDate + "' AND c.teacherId = '" + teacherId + "'";

        if (gradeFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ")");
        }

        if (syllabusFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ")");
        }

        if (subjectFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.subjectId IN (" + getIN_CLAUSE_VALUES(subjectFilters) + ")");
        }

        if (chapterFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.chapterId IN (" + getIN_CLAUSE_VALUES(chapterFilters) + ")");
        }

        if (batchFilters != null) {
            SQLlistLive = String.format("%s", SQLlistLive + " AND c.batchId IN (" + getIN_CLAUSE_VALUES(batchFilters) + ")");
        }

        SQLlistLive = String.format("%s", SQLlistLive + " ORDER BY c.scheduledDate ASC");

        /*put all sql(s) to map*/
        Map<String, String> SQLFilter = new HashMap<>();
//        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
//        SQLFilter.put("SQLRecorded", SQLRecorded);
//        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
//        SQLFilter.put("SQLlistLive", SQLlistLive);

//========================
        SQLFilter.put("SQLcountRecorded", SQLcountRecorded);
        SQLFilter.put("SQLRecorded", SQLRecorded);
        SQLFilter.put("SQLRecordedTotalCount", "SELECT COUNT(*) FROM ClassScheduler c WHERE c.endDate < '" + currentDate + "'");

        SQLFilter.put("SQLlistUpcoming", SQLlistUpcoming);
        SQLFilter.put("SQLUpcomingCount", "select count(*) " + SQLlistUpcoming);
        SQLFilter.put("SQLUpcomingTotalCount", "FROM ClassScheduler c WHERE c.scheduledDate >= '" + currentDate + "'");

        SQLFilter.put("SQLlistLive", SQLlistLive);
        SQLFilter.put("SQLiveCount", "select count(*) " + SQLlistLive);
        SQLFilter.put("SQLiveTotalCount", "FROM ClassScheduler c WHERE c.scheduledDate < '" + currentDate + "' AND c.endDate > '" + currentDate + "'");

        return SQLFilter;
    }

    public Map<String, String> listOfPaymentFilter(String order_status, String startDate, String endDate, List<String> allotedTo) {

        /* count of PaymentCheckout */
        String SQLcountPaymentCheckout = "SELECT COUNT(*) FROM PaymentCheckout p";

        SQLcountPaymentCheckout = getPaymentCheckoutSQLQuery(order_status, startDate, endDate, SQLcountPaymentCheckout, allotedTo);

        /* list of PaymentCheckout */
        String SQLlistPaymentCheckout = "FROM PaymentCheckout p";

        SQLlistPaymentCheckout = getPaymentCheckoutSQLQuery(order_status, startDate, endDate, SQLlistPaymentCheckout, allotedTo);

        SQLlistPaymentCheckout = String.format("%s", SQLlistPaymentCheckout + " ORDER BY p.dateOfCreation DESC");
        System.out.println("finall  " + SQLlistPaymentCheckout);
        /* put all sql(s) to map */
        Map<String, String> SQLFilter = new HashMap<>();
        SQLFilter.put("SQLcountPaymentCheckout", SQLcountPaymentCheckout);
        SQLFilter.put("SQLlistPaymentCheckout", SQLlistPaymentCheckout);

        return SQLFilter;

    }

    public String getPaymentCheckoutSQLQuery(String order_status, String startDate, String endDate, String SQL, List<String> allotedTo) {

        String allotedToSql_AND = " AND p.allotedStudentAccountId IN (" + getIN_CLAUSE_VALUES(allotedTo) + ")";
        System.out.println("allotedToSql_AND  " + allotedToSql_AND);
        String allotedToSql_WHERE = " WHERE p.allotedStudentAccountId IN (" + getIN_CLAUSE_VALUES(allotedTo) + ")";

        /*all are having values*/
        if ((order_status != null) && (startDate != null && endDate != null) && (allotedTo.size() > 0)) {
            SQL = String.format("%s", SQL + " WHERE p.order_status = '" + order_status + "'");

            String sqlDateRange;
            if (startDate.equals(endDate)) {
                sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) >= '" + startDate + "' AND DATE(p.dateOfCreation) <= '" + endDate + "'");
            }

            SQL = String.format("%s", SQL + sqlDateRange + allotedToSql_AND);

            /*order_status, allotedTo are having values*/
        } else if ((order_status != null) && (startDate == null && endDate == null) && (allotedTo.size() > 0)) {

            SQL = String.format("%s", SQL + " WHERE p.order_status = '" + order_status + "'" + allotedToSql_AND);

            /*daterange, allotedTo are having values*/
        } else if ((order_status == null) && (startDate != null && endDate != null) && (allotedTo.size() > 0)) {

            String sqlDateRange;
            if (startDate.equals(endDate)) {
                sqlDateRange = String.format("%s", " WHERE DATE(p.dateOfCreation) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " WHERE DATE(p.dateOfCreation) >= '" + startDate + "' AND DATE(p.dateOfCreation) <= '" + endDate + "'");
            }

            SQL = String.format("%s", SQL + sqlDateRange + allotedToSql_AND);

        } /*order_status, daterange are having values*/ else if ((order_status != null) && (startDate != null && endDate != null) && (allotedTo.isEmpty())) {

            SQL = String.format("%s", SQL + " WHERE p.order_status = '" + order_status + "'");

            String sqlDateRange;
            if (startDate.equals(endDate)) {
                sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) >= '" + startDate + "' AND DATE(p.dateOfCreation) <= '" + endDate + "'");
            }

            SQL = String.format("%s", SQL + sqlDateRange);

            /*order_status is having value*/
        } else if ((order_status != null) && (startDate == null && endDate == null) && (allotedTo.isEmpty())) {

            SQL = String.format("%s", SQL + " WHERE p.order_status = '" + order_status + "'");

            /*daterange is having value*/
        } else if ((order_status == null) && (startDate != null && endDate != null) && (allotedTo.isEmpty())) {

            String sqlDateRange;
            if (startDate.equals(endDate)) {
                sqlDateRange = String.format("%s", " WHERE DATE(p.dateOfCreation) = '" + startDate + "'");
            } else {
                sqlDateRange = String.format("%s", " WHERE DATE(p.dateOfCreation) >= '" + startDate + "' AND DATE(p.dateOfCreation) <= '" + endDate + "'");
            }

            SQL = String.format("%s", SQL + sqlDateRange);

            /*allotedTo is having value*/
        } else if ((order_status == null) && (startDate == null && endDate == null) && (allotedTo.size() > 0)) {

            SQL = String.format("%s", SQL + allotedToSql_WHERE);
        }
        System.out.println("SQL  " + SQL);
        return SQL;

    }

    //    public static String getPaymentCheckoutSQLQuery(String order_status, String startDate, String endDate, String SQL) {
//
//        if ((order_status != null) && (startDate != null && endDate != null)) {
//
//            SQL = String.format("%s", SQL + " WHERE p.order_status = '" + order_status + "'");
//
//            String sqlDateRange;
//            if (startDate.equals(endDate)) {
//                sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) = '" + startDate + "'");
//            } else {
//                sqlDateRange = String.format("%s", " AND DATE(p.dateOfCreation) >= '" + startDate + "' AND DATE(p.dateOfCreation) <= '" + endDate + "'");
//            }
//
//            SQL = String.format("%s", SQL + sqlDateRange);
//
//        } else if ((order_status != null) && (startDate == null && endDate == null)) {
//            SQL = String.format("%s", SQL + " WHERE p.order_status = '" + order_status + "'");
//
//        } else if ((order_status == null) && (startDate != null && endDate != null)) {
//
//            String sqlDateRange;
//            if (startDate.equals(endDate)) {
//                sqlDateRange = String.format("%s", " WHERE DATE(p.dateOfCreation) = '" + startDate + "'");
//            } else {
//                sqlDateRange = String.format("%s", " WHERE DATE(p.dateOfCreation) >= '" + startDate + "' AND DATE(p.dateOfCreation) <= '" + endDate + "'");
//            }
//
//            SQL = String.format("%s", SQL + sqlDateRange);
//
//        }
//
//        return SQL;
//
//    }
    public String getParentIdSQLQuery(String parent, String mobile) {

        String SQL = "";
        if ((parent == null) && (mobile == null)) {
            return SQL;
        } else {
            SQL = "SELECT r.accountId FROM Registration r";

            if ((parent != null) && (mobile != null)) {
                SQL = String.format("%s", SQL + " WHERE r.firstName LIKE '" + parent + "%' AND r.mobileNum LIKE '" + mobile + "%'");

            } else if ((parent != null) && (mobile == null)) {
                SQL = String.format("%s", SQL + " WHERE r.firstName LIKE '" + parent + "%'");

            } else if ((parent == null) && (mobile != null)) {
                SQL = String.format("%s", SQL + " WHERE r.mobileNum LIKE '" + mobile + "%'");

            }
            return SQL;
        }

    }

    public String productsFilters(List<String> gradeFilters, List<String> syllabusFilters, String status, String dateOrder) {

        String SQLQuery = "FROM SubscribeType s ";

        boolean flag = false;

        if (gradeFilters != null) {

            flag = true;

            SQLQuery = String.format("%s", SQLQuery + "where s.gradeId IN (" + getIN_CLAUSE_VALUES(gradeFilters) + ") ");
        }

        if (syllabusFilters != null && flag) {

            SQLQuery = String.format("%s", SQLQuery + " AND s.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ") ");

        } else if (syllabusFilters != null) {

            flag = true;

            SQLQuery = String.format("%s", SQLQuery + "where  s.syllabusId IN (" + getIN_CLAUSE_VALUES(syllabusFilters) + ") ");
        }

        if (status != null && flag) {

            SQLQuery = String.format("%s", SQLQuery + "AND s.status =" + status + "");

        } else if (status != null) {

            SQLQuery = String.format("%s", SQLQuery + "where s.status =" + status + "");
        }
        SQLQuery = SQLQuery + " order by s.dateOfCreation " + dateOrder + "";

        System.out.println("SQLQuery " + SQLQuery);

        return SQLQuery;

    }

    public String paymentCheckoutFilterSqlQuerySimplified(String order_status, String startDate, String endDate, String SQL,
                                                          List<String> allotedTo, List<String> subscriptionIdFilter, List<String> syllabusFilter) {

        String paymentCheckoutFilterSqlQuery = "FROM PaymentCheckout p where p.paymentCheckoutId is not null";

        if (order_status != null) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " and p.order_status = '" + order_status + "'");
        }
        if (!allotedTo.isEmpty()) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " AND p.allotedStudentAccountId IN (" + getIN_CLAUSE_VALUES(allotedTo) + ")");
        }
        if (subscriptionIdFilter != null) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " AND p.subsctypeId IN (" + getIN_CLAUSE_VALUES(subscriptionIdFilter) + ")");
        }
        if (syllabusFilter != null) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " AND p.subsctypeId IN (select s.subsTypeId from SubscribeType s where s.syllabusId in (" + getIN_CLAUSE_VALUES(syllabusFilter) + "))");
        }

        if (startDate == null && endDate != null) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " and DATE(p.dateOfCreation) <  '" + endDate + "'");
        }
        if (startDate != null && endDate == null) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " and DATE(p.dateOfCreation) between  '" + startDate + "' AND current_date() ");
        }

        if (startDate != null && endDate != null) {
            paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " and DATE(p.dateOfCreation) between  '" + startDate + "' AND '" + endDate + "' ");
        }

        paymentCheckoutFilterSqlQuery = String.format("%s", paymentCheckoutFilterSqlQuery + " ORDER BY p.dateOfCreation DESC");

        return paymentCheckoutFilterSqlQuery;

    }

}
