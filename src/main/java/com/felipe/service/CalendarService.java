package com.felipe.service;

import static java.util.stream.Collectors.toList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.felipe.dao.CalendarCache;
import com.felipe.domain.Appointment;
import com.felipe.domain.Calendar;
import com.felipe.domain.TimeSlot;
import com.felipe.service.collectors.CalendarIntersectionCollector;
import com.felipe.service.collectors.FreeSlotCollector;

@Component
public class CalendarService {

  @Autowired private CalendarCache cache;

  public List<TimeSlot> findAvailableTime(
      List<String> calendarIds,
      Integer meetingDuration,
      LocalDateTime startPeriod,
      LocalDateTime endPeriod) {

    List<List<TimeSlot>> freeSlotsByCalendar =
        calendarIds.stream()
            .map(calendarId -> getFreeSlotsByCalendar(calendarId, startPeriod, endPeriod))
            .collect(toList());

    List<TimeSlot> freeSlotsIntersection =
        calculateCommonAgendaBetweenFreeSlots(freeSlotsByCalendar);

    return filterSlotsByDuration(meetingDuration, freeSlotsIntersection);
  }

  private List<TimeSlot> getFreeSlotsByCalendar(
      String calendarId, LocalDateTime startPeriod, LocalDateTime endPeriod) {
    List<Appointment> appointmentsWithinInterval =
        getAppointmentsWithinInterval(calendarId, startPeriod, endPeriod);

    boolean thereIsNoAppointment = appointmentsWithinInterval.isEmpty();
    if (thereIsNoAppointment) {
      TimeSlot wholeIntervalAsFreeSlot =
          TimeSlot.builder().calendarId(calendarId).start(startPeriod).end(endPeriod).build();
      return new ArrayList<>(List.of(wholeIntervalAsFreeSlot));
    }

    FreeSlotCollector collector =
        FreeSlotCollector.builder()
            .appointmentsTotal(appointmentsWithinInterval.size())
            .startPeriod(startPeriod)
            .endPeriod(endPeriod)
            .build();

    return appointmentsWithinInterval.stream().collect(collector);
  }

  private List<Appointment> getAppointmentsWithinInterval(
      String calendarId, LocalDateTime startPeriod, LocalDateTime endPeriod) {
    Calendar calendar = this.cache.getCalendar(calendarId);
    return calendar.getAppointmentsWithinInterval(startPeriod, endPeriod);
  }

  private List<TimeSlot> calculateCommonAgendaBetweenFreeSlots(List<List<TimeSlot>> freeSlots) {
    List<TimeSlot> firstCalendar = freeSlots.remove(0);
    
    CalendarIntersectionCollector calendarIntersectionCollector =
        CalendarIntersectionCollector.builder().firstCalendar(firstCalendar).build();

    return freeSlots.stream().collect(calendarIntersectionCollector);
  }

  private List<TimeSlot> filterSlotsByDuration(
      Integer meetingDuration, List<TimeSlot> freeSlotsIntersection) {
    return freeSlotsIntersection.stream()
        .filter(slot -> onlySlotsLongEnough(meetingDuration, slot))
        .collect(toList());
  }

  private boolean onlySlotsLongEnough(Integer meetingDuration, TimeSlot slot) {
    return ChronoUnit.MINUTES.between(slot.getStart(), slot.getEnd()) >= meetingDuration;
  }
}
