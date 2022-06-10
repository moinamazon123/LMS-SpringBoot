package com.maps.yolearn.controller.grade;

import com.maps.yolearn.bean.grade.GradeMetaData;
import com.maps.yolearn.model.grade.Chapter;
import com.maps.yolearn.model.grade.Grade;
import com.maps.yolearn.model.grade.Subject;
import com.maps.yolearn.model.grade.Syllabus;
import com.maps.yolearn.service.EntityService;
import com.maps.yolearn.util.date.MyDateFormate;
import com.maps.yolearn.util.primarykey.CustomPKGenerator;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KOTARAJA
 * @author PREMNATH
 */
@RestController
@RequestMapping(value = {"/grade"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class GradeController {

    @Autowired
    private EntityService service;

    @Autowired
    private CustomPKGenerator pKGenerator;

    @RequestMapping(value = {"/saveGradeMetadata"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject saveGradeMetaData(@RequestBody final GradeMetaData bean) {

        JSONObject json = new JSONObject();

        try {
            String gradeName = bean.getGradeName();

            Map<String, Object> map = new HashMap<>();
            map.put("gradeName", gradeName);

            if (service.getObject(Grade.class, map).size() > 0) {

                json.put("msg", "Grade " + gradeName + " already exists!");

            } else {
                Timestamp date = new Timestamp(System.currentTimeMillis());

                Grade grade = new Grade();

                String gradeId = (String) pKGenerator.generate(Grade.class, "GRADE");
                grade.setGradeId(gradeId);

                grade.setGradeName(gradeName);

                grade.setDateOfCreation(date);

                if (service.save(grade) > 0) {
                    json.put("gradeName", gradeName);
                    json.put("dateOfCreation", MyDateFormate.dateToString(date));
                    json.put("gradeId", gradeId);
                    json.put("msg", "Grade has been added.");

                } else {
                    json.put("msg", "Something went wrong. Try again!");
                }

            }

        } catch (Exception e) {
        }
        return json;
    }

    @RequestMapping(value = {"/saveSyllabusMetadata"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject saveSyllabusMetadata(@RequestBody final GradeMetaData bean) {

        JSONObject json = new JSONObject();

        try {

            Timestamp date = new Timestamp(System.currentTimeMillis());

            String syllabusName = bean.getSyllabusName();
            String gradeId = bean.getGradeId();

            Map<String, Object> map = new HashMap<>();
            map.put("syllabusName", syllabusName);
            map.put("gradeId", gradeId);

            List<Object> listSyllabusObj = service.getObject(Syllabus.class, map);
            if (listSyllabusObj.size() > 0) {

                Map<String, Object> map1 = new HashMap<>();
                map1.put("gradeId", gradeId);

                List<Object> listGradeObj = service.getObject(Grade.class, map1);

                String gradeName = "";
                for (Object object : listGradeObj) {
                    Grade grade = (Grade) object;
                    gradeName = grade.getGradeName();
                }

                json.put("msg", "Syllabus '" + syllabusName + "' already exists in grade '" + gradeName + "'.");

            } else {

                String syllabusDesc = bean.getSyllabusDesc();

                Syllabus syllabus = new Syllabus();

                String syllabusId = (String) pKGenerator.generate(Syllabus.class, "SYLL");
                syllabus.setSyllabusId(syllabusId);

                syllabus.setSyllabusName(syllabusName);
                syllabus.setSyllabusDesc(syllabusDesc);

                syllabus.setDateOfCreation(date);
                syllabus.setGradeId(gradeId);

                if (service.save(syllabus) > 0) {
                    json.put("syllabusName", syllabusName);
                    json.put("syllabusDesc", syllabusDesc);
                    json.put("dateOfCreation", MyDateFormate.dateToString(date));
                    json.put("gradeId", gradeId);
                    json.put("syllabusId", syllabusId);
                    json.put("msg", "Syllabus has been added.");
                } else {
                    json.put("msg", "Something went wrong. Try again!");
                }

            }

        } catch (Exception e) {
        }
        return json;
    }

    @RequestMapping(value = {"/saveSubjectMetadata"}, method = RequestMethod.POST)
    public @ResponseBody
    JSONObject saveSubjectMetadata(@RequestBody final GradeMetaData bean) {

        JSONObject json = new JSONObject();

        try {

            Timestamp date = new Timestamp(System.currentTimeMillis());

            String subjectName = bean.getSubjectName();
            String syllabusId = bean.getSyllabusId();
            String gradeId = bean.getGradeId();

            Map<String, Object> map = new HashMap<>();
            map.put("subjectName", subjectName);
            map.put("syllabusId", syllabusId);
            map.put("gradeId", gradeId);

            List<Object> listSubjectObj = service.getObject(Subject.class, map);
            if (listSubjectObj.size() > 0) {

                Map<String, Object> map1 = new HashMap<>();
                map1.put("gradeId", gradeId);

                List<Object> listGradeObj = service.getObject(Grade.class, map1);

                String gradeName = "";
                for (Object object : listGradeObj) {
                    Grade grade = (Grade) object;
                    gradeName = grade.getGradeName();
                }

                Map<String, Object> map2 = new HashMap<>();
                map2.put("syllabusId", syllabusId);

                List<Object> listSyllabusObj = service.getObject(Syllabus.class, map2);

                String syllabusName = "";
                for (Object object : listSyllabusObj) {
                    Syllabus syllabus = (Syllabus) object;
                    syllabusName = syllabus.getSyllabusName();
                }

                json.put("msg", "Subject '" + subjectName + "' for grade '" + gradeName + "' with syllabus '" + syllabusName + "' already exists.");

            } else {

                String subjectDesc = bean.getSubjectDesc();

                Subject subject = new Subject();

                String subjectId = (String) pKGenerator.generate(Subject.class, "SUBJ");
                subject.setSubjectId(subjectId);

                subject.setSubjectName(subjectName);
                subject.setSubjectDesc(subjectDesc);
                subject.setSyllabusId(syllabusId);
                subject.setGradeId(gradeId);

                subject.setDateOfCreation(date);

                if (service.save(subject) > 0) {
                    json.put("subjectName", subjectName);
                    json.put("subjectDesc", subjectDesc);
                    json.put("syllabusId", syllabusId);
                    json.put("gradeId", gradeId);
                    json.put("dateOfCreation", MyDateFormate.dateToString(date));
                    json.put("subjectId", subjectId);
                    json.put("msg", "Subject has been added.");
                } else {
                    json.put("msg", "Something went wrong. Try again!");
                }

            }

        } catch (Exception e) {
        }
        return json;
    }

    @GetMapping(value = {"/loadAllGrades"})
    public List<Object> loadAllGrades() {
        List<Object> listOfGrades = new ArrayList<>();
        try {

            List<Object> listGradeObj = service.getObject("SELECT g.gradeId, g.gradeName, g.dateOfCreation, g.disabled FROM Grade g");
            if (listGradeObj.size() > 0) {
                for (Object object : listGradeObj) {
                    Map<String, Object> grade = new HashMap<>();
                    Object[] oarr = (Object[]) object;
                    grade.put("gradeId", oarr[0]);
                    grade.put("gradeName", oarr[1]);
                    grade.put("dateOfCreation", oarr[2]);
                    grade.put("disabled", oarr[3]);
                    listOfGrades.add(grade);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfGrades;
    }

    @RequestMapping(value = {"/loadAllSyllabuses"}, method = RequestMethod.GET)
    public @ResponseBody
    List<JSONObject> loadAllSyllabuses() {

        List<JSONObject> listOfSyllabuses = new ArrayList<>();

        List<Object> listSyllabusObj = service.getObject(Syllabus.class);
        for (Object object : listSyllabusObj) {
            Syllabus syllabus = (Syllabus) object;

            JSONObject json = new JSONObject();
            json.put("syllabusId", syllabus.getSyllabusId());
            json.put("syllabusName", syllabus.getSyllabusName());
            json.put("syllabusDesc", syllabus.getSyllabusDesc());

            json.put("dateOfCreation", MyDateFormate.dateToString(syllabus.getDateOfCreation()));

            json.put("gradeId", syllabus.getGradeId());

            listOfSyllabuses.add(json);
        }

        return listOfSyllabuses;
    }

    @RequestMapping(value = {"/loadSyllabusByGrId"}, method = RequestMethod.POST)
    public @ResponseBody
    List<JSONObject> loadSyllabusByGrId(@RequestBody final GradeMetaData bean) {

        List<JSONObject> listOfSubjects = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("gradeId", bean.getGradeId());

        List<Object> listSyllabusObj = service.getObject(Syllabus.class, map);

        for (Object object : listSyllabusObj) {
            Syllabus syllabus = (Syllabus) object;

            JSONObject json = new JSONObject();
            json.put("syllabusId", syllabus.getSyllabusId());
            json.put("syllabusName", syllabus.getSyllabusName());
            json.put("syllabusDesc", syllabus.getSyllabusDesc());

            json.put("dateOfCreation", MyDateFormate.dateToString(syllabus.getDateOfCreation()));

            json.put("gradeId", syllabus.getGradeId());
            json.put("disabled", syllabus.isDisabled());

            listOfSubjects.add(json);
        }

        return listOfSubjects;
    }

    @RequestMapping(value = {"/loadAllSubjects"}, method = RequestMethod.GET)
    public @ResponseBody
    List<JSONObject> loadAllSubjects() {

        List<JSONObject> listOfSubjects = new ArrayList<>();

        List<Object> listSubjectObj = service.getObject(Subject.class);
        for (Object object : listSubjectObj) {
            Subject subject = (Subject) object;

            JSONObject json = new JSONObject();
            json.put("subjectId", subject.getSubjectId());
            json.put("subjectName", subject.getSubjectName());
            json.put("subjectDesc", subject.getSubjectDesc());
            json.put("gradeId", subject.getGradeId());

            json.put("dateOfCreation", MyDateFormate.dateToString(subject.getDateOfCreation()));

            json.put("syllabusId", subject.getSyllabusId());

            listOfSubjects.add(json);
        }

        return listOfSubjects;
    }

    @RequestMapping(value = {"/loadSubjectByGrId_SyllabId"}, method = RequestMethod.POST)
    public @ResponseBody
    List<JSONObject> loadSubjectByGrId_SyllabId(@RequestBody final GradeMetaData bean) {

        List<JSONObject> listOfSubject = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("gradeId", bean.getGradeId());
        map.put("syllabusId", bean.getSyllabusId());

        List<Object> listSubjectObj = service.getObject(Subject.class, map);
        for (Object object : listSubjectObj) {
            Subject subject = (Subject) object;

            JSONObject json = new JSONObject();
            json.put("subjectId", subject.getSubjectId());
            json.put("subjectName", subject.getSubjectName());
            json.put("subjectDesc", subject.getSubjectDesc());
            json.put("gradeId", subject.getGradeId());

            json.put("dateOfCreation", MyDateFormate.dateToString(subject.getDateOfCreation()));

            json.put("syllabusId", subject.getSyllabusId());
            json.put("disabled", subject.isDisabled());

            listOfSubject.add(json);
        }

        return listOfSubject;
    }

    @RequestMapping(value = {"/addChapter"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> addChapter(@RequestBody GradeMetaData bean) {
        JSONObject json = new JSONObject();
        Timestamp date = new Timestamp(System.currentTimeMillis());
        String gradeId = bean.getGradeId();
        String syllabusId = bean.getSyllabusId();
        String subjectId = bean.getSubjectId();
        String chapterName = bean.getChapterName();
        String chapterDesc = bean.getChapterDesc();

        Chapter chapter = new Chapter();
        String chapterId = (String) pKGenerator.generate(Chapter.class, "CHAP");
        chapter.setChapterId(chapterId);
        chapter.setGradeId(gradeId);
        chapter.setSyllabusId(syllabusId);
        chapter.setSubjectId(subjectId);
        chapter.setChapterName(chapterName);
        chapter.setChapterDesc(chapterDesc);
        chapter.setDateOfCreation(date);

        if (service.save(chapter) > 0) {
            json.put("msg", "Chapter added successfully!");
        } else {
            json.put("msg", "Something went wrong!");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @RequestMapping(value = {"/retrieveChapterByIds"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> retrieveChapterByIds(@RequestBody final GradeMetaData bean) {
        List<JSONObject> liJson = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("gradeId", bean.getGradeId());
        map.put("syllabusId", bean.getSyllabusId());
        map.put("subjectId", bean.getSubjectId());

        List<Object> liChapterObj = service.getObject(Chapter.class, map);
        if (liChapterObj.size() > 0) {
            for (Object object : liChapterObj) {
                Chapter chapter = (Chapter) object;
                JSONObject json = new JSONObject();
                json.put("chapterId", chapter.getChapterId());
                json.put("gradeId", chapter.getGradeId());
                json.put("syllabusId", chapter.getSyllabusId());
                json.put("subjectId", chapter.getSubjectId());
                json.put("chapterName", chapter.getChapterName());
                json.put("chapterDesc", chapter.getChapterDesc());
                json.put("dateOfCreation", chapter.getDateOfCreation());
                json.put("disabled", chapter.isDisabled());

                liJson.add(json);
            }
        }

        return new ResponseEntity<>(liJson, HttpStatus.OK);
    }

    /*by default disable value is false i.e it is enabled*/
    @RequestMapping(value = {"/disable-enable-grade"}, method = RequestMethod.POST)
    public void disableEnableGrade(@RequestBody final Map<String, Object> mapBean) {
        String gradeId = (String) mapBean.get("gradeId");

        try {
            List<String> sqls = new ArrayList<>();

            boolean disabled = (boolean) service.getObject(String.format("%s", "SELECT g.disabled FROM Grade g WHERE g.gradeId = '" + gradeId + "'")).get(0);

            if (!disabled) {
                /*disabling*/
                sqls.add(String.format("%s", "UPDATE Grade g SET g.disabled = " + true + " WHERE g.gradeId = '" + gradeId + "'"));
                sqls.add(String.format("%s", "UPDATE Syllabus s SET s.disabled = " + true + " WHERE s.gradeId = '" + gradeId + "'"));
                sqls.add(String.format("%s", "UPDATE Subject s SET s.disabled = " + true + " WHERE s.gradeId = '" + gradeId + "'"));
                sqls.add(String.format("%s", "UPDATE Chapter c SET c.disabled = " + true + " WHERE c.gradeId = '" + gradeId + "'"));
                service.update(sqls);
            } else if (disabled) {
                /*enabling*/
                sqls.add(String.format("%s", "UPDATE Grade g SET g.disabled = " + false + " WHERE g.gradeId = '" + gradeId + "'"));
                sqls.add(String.format("%s", "UPDATE Syllabus s SET s.disabled = " + false + " WHERE s.gradeId = '" + gradeId + "'"));
                sqls.add(String.format("%s", "UPDATE Subject s SET s.disabled = " + false + " WHERE s.gradeId = '" + gradeId + "'"));
//                sqls.add(String.format("%s", "UPDATE Chapter c SET c.disabled = " + false + " WHERE c.gradeId = '" + gradeId + "'"));
                service.update(sqls);
            }

        } catch (Exception e) {
        }
    }

    @RequestMapping(value = {"/disable-enable-chapter"}, method = RequestMethod.POST)
    public void disableEnableChapter(@RequestBody final Map<String, Object> mapBean) {
        String chapterId = (String) mapBean.get("chapterId");

        try {
            boolean disabled = (boolean) service.getObject(String.format("%s", "SELECT c.disabled FROM Chapter c WHERE c.chapterId = '" + chapterId + "'")).get(0);

            if (!disabled) {
                /*disabling*/
                service.update(String.format("%s", "UPDATE Chapter c SET c.disabled = " + true + " WHERE c.chapterId = '" + chapterId + "'"));
            } else if (disabled) {
                /*enabling*/

                boolean b = (boolean) service.getObject(String.format("%s", "SELECT s.disabled FROM Subject s WHERE s.subjectId IN (SELECT c.subjectId FROM Chapter c WHERE c.chapterId = '" + chapterId + "')")).get(0);
                /*if upper hierarchy (i.e Subject) is disabled then chapter can not be enabled*/
                if (!b) {
                    service.update(String.format("%s", "UPDATE Chapter c SET c.disabled = " + false + " WHERE c.chapterId = '" + chapterId + "'"));
                }
            }
        } catch (Exception e) {
        }
    }

}
