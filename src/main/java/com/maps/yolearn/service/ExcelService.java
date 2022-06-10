package com.maps.yolearn.service;

import com.maps.yolearn.bean.filter.FilterBean;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author MAPS
 */
public interface ExcelService {

    public String create_INVOICE_Excel(String remoteFilePath, FilterBean filter, HttpServletResponse response) throws Exception;

    public String create_STUDENT_REPORT_Excel(String remoteFilePath, String gradeName, Map<String, Object> dateRange, HttpServletResponse response) throws Exception;

    public String create_STUDENT_REPORT_Excel(String remoteFilePath, Map<String, Object> dateRange, HttpServletResponse response) throws IOException;

    public String create_ADMIN_Excel(String remoteFilePath, HttpServletResponse response) throws IOException;

    public String create_PARENTS_Excel(String remoteFilePath, HttpServletResponse response) throws IOException;

    public String create_TEACHERS_Excel(String remoteFilePath, HttpServletResponse response) throws IOException;

    public String getGradeName(String gradeId);

}
