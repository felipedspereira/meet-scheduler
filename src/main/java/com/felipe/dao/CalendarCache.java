package com.felipe.dao;

import static java.util.stream.Collectors.toMap;

import com.felipe.dao.dataimporter.DataImporter;
import com.felipe.domain.Calendar;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;

public class CalendarCache {
  private Map<String, Calendar> cache;
  @Autowired private DataImporter<Calendar> dataImporter;

  public CalendarCache() throws IOException {
    this.cache =
        dataImporter.getData().stream().collect(toMap(Calendar::getId, Function.identity()));
  }

  public Calendar getCalendar(String id) {
    return this.cache.get(id);
  }
}
