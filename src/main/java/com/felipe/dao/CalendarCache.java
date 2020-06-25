package com.felipe.dao;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.felipe.dao.dataimporter.DataImporter;
import com.felipe.domain.Calendar;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarCache {
  private Map<String, Calendar> cache;

  @Autowired
  public CalendarCache(DataImporter<Calendar> dataImporter) throws IOException {
    List<Calendar> data = dataImporter.getData();
    this.cache = data.stream().collect(toMap(this::getCalendarId, identity()));
  }

  private String getCalendarId(Calendar calendar) {
    return calendar.getAppointments().stream().findAny().get().getCalendarId();
  }

  public Calendar getCalendar(String id) {
    return this.cache.get(id);
  }
}
