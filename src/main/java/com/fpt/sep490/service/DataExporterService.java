package com.fpt.sep490.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface DataExporterService {
    void exportToExcel(List<Map<String,Object>> dataList, OutputStream outputStream)throws IOException;
    List<Map<String, Object>> importFromExcel(String filename) throws IOException;
}
