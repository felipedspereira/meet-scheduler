package com.felipe.rest;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import com.felipe.domain.TimeSlot;
import com.felipe.service.CalendarService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("calendar")
public class CalendarResource {
  @Autowired private CalendarService service;

  @GetMapping("availableTime")
  public List<TimeSlot> findAvailableTime(
      @RequestParam("calendarIds") List<String> calendarIds,
      @RequestParam("meetingDuration") Integer meetingDuration,
      @RequestParam("startPeriod") @DateTimeFormat(iso = DATE_TIME) LocalDateTime startPeriod,
      @RequestParam("endPeriod") @DateTimeFormat(iso = DATE_TIME) LocalDateTime endPeriod) {
    return service.findAvailableTime(calendarIds, meetingDuration, startPeriod, endPeriod);
  }
}
