package com.felipe.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.felipe.dao.CalendarCache;
import com.felipe.domain.Appointment;
import com.felipe.domain.Calendar;
import com.felipe.domain.TimeSlot;
import com.felipe.exception.NoCommonAgendaException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CalendarServiceTest {
  @Mock private CalendarCache cache;
  @InjectMocks private CalendarService calendarService;

  private Calendar calendarWithNoAppointments;
  private Calendar calendarWithAppointmentAt10;
  private Calendar calendarWithTwoAppointments;
  private Calendar calendarWithAppointment9h30min;
  private Calendar calendarWithWholeDayBusy;
  private LocalDateTime startPeriod = LocalDateTime.of(2020, 1, 1, 8, 0);
  private LocalDateTime endPeriod = LocalDateTime.of(2020, 1, 1, 17, 0);

  private static String calendarIdNoAppointments = "123123";
  private static String calendarIdAppointment10 = "54353";
  private static String calendarIdTwoAppointments = "4444";
  private static String calendarIdAppointment930to1030 = "5555";
  private static String calendarIdWholeDayBusy = "6767";

  @Before
  public void setup() {
    calendarWithNoAppointments = Calendar.builder().appointments(List.of()).build();

    calendarWithAppointmentAt10 =
        Calendar.builder()
            .appointments(List.of(createAppointmentAt10(calendarIdAppointment10)))
            .build();

    calendarWithTwoAppointments =
        Calendar.builder()
            .appointments(
                List.of(
                    createAppointmentAt10(calendarIdTwoAppointments),
                    createAppointmentAt15(calendarIdTwoAppointments)))
            .build();

    calendarWithAppointment9h30min =
        Calendar.builder()
            .appointments(List.of(createAppointmentAt9h30min(calendarIdAppointment930to1030)))
            .build();

    calendarWithWholeDayBusy =
        Calendar.builder()
            .appointments(List.of(createAppointmentForTheWholeDay(calendarIdWholeDayBusy)))
            .build();

    when(this.cache.getCalendar(calendarIdTwoAppointments)).thenReturn(calendarWithTwoAppointments);
    when(this.cache.getCalendar(calendarIdAppointment930to1030))
        .thenReturn(calendarWithAppointment9h30min);
    when(this.cache.getCalendar(calendarIdWholeDayBusy)).thenReturn(calendarWithWholeDayBusy);
    when(this.cache.getCalendar(calendarIdNoAppointments)).thenReturn(calendarWithNoAppointments);
    when(this.cache.getCalendar(calendarIdAppointment10)).thenReturn(calendarWithAppointmentAt10);
  }

  @Test
  public void shouldFindCommonAgendaBetweenCalendars() {
    List<String> calendars =
        List.of(
            calendarIdTwoAppointments,
            calendarIdAppointment930to1030,
            calendarIdAppointment10,
            calendarIdNoAppointments);

    List<TimeSlot> freeSlotsByCalendar =
        calendarService.findAvailableTime(calendars, 45, startPeriod, endPeriod);

    assertTrue(freeSlotsByCalendar.size() == 3);

    TimeSlot timeSlot = freeSlotsByCalendar.get(0);
    assertTrue(timeSlot.getStart().equals(LocalDateTime.of(2020, 1, 1, 8, 0)));
    assertTrue(timeSlot.getEnd().equals(LocalDateTime.of(2020, 1, 1, 9, 30)));

    TimeSlot timeSlot2 = freeSlotsByCalendar.get(1);
    assertTrue(timeSlot2.getStart().equals(LocalDateTime.of(2020, 1, 1, 11, 0)));
    assertTrue(timeSlot2.getEnd().equals(LocalDateTime.of(2020, 1, 1, 15, 0)));

    TimeSlot timeSlot3 = freeSlotsByCalendar.get(2);
    assertTrue(timeSlot3.getStart().equals(LocalDateTime.of(2020, 1, 1, 16, 0)));
    assertTrue(timeSlot3.getEnd().equals(LocalDateTime.of(2020, 1, 1, 17, 0)));
  }

  @Test
  public void shouldNotFindCommonAgendaDueToTooLongDuration() {
    List<String> calendars =
        List.of(
            calendarIdTwoAppointments,
            calendarIdAppointment930to1030,
            calendarIdAppointment10,
            calendarIdNoAppointments);

    List<TimeSlot> freeSlotsByCalendar =
        calendarService.findAvailableTime(calendars, 99999, startPeriod, endPeriod);

    assertTrue(freeSlotsByCalendar.size() == 0);
  }

  @Test(expected = NoCommonAgendaException.class)
  public void shouldNotFindCommonAgenda() {
    List<String> calendars =
        List.of(
            calendarIdTwoAppointments,
            calendarIdAppointment930to1030,
            calendarIdWholeDayBusy,
            calendarIdNoAppointments,
            calendarIdWholeDayBusy);

    calendarService.findAvailableTime(calendars, 60, startPeriod, endPeriod);
  }

  private static Appointment createAppointmentAt15(String calendarId) {
    return Appointment.builder()
        .calendarId(calendarId)
        .start(LocalDateTime.of(2020, 1, 1, 15, 0))
        .end(LocalDateTime.of(2020, 1, 1, 16, 0))
        .build();
  }

  private static Appointment createAppointmentAt10(String calendarId) {
    return Appointment.builder()
        .calendarId(calendarId)
        .start(LocalDateTime.of(2020, 1, 1, 10, 0))
        .end(LocalDateTime.of(2020, 1, 1, 11, 0))
        .build();
  }

  private Appointment createAppointmentAt9h30min(String calendarId) {
    return Appointment.builder()
        .calendarId(calendarId)
        .start(LocalDateTime.of(2020, 1, 1, 9, 30))
        .end(LocalDateTime.of(2020, 1, 1, 10, 30))
        .build();
  }

  private Appointment createAppointmentForTheWholeDay(String calendarId) {
    return Appointment.builder()
        .calendarId(calendarId)
        .start(LocalDateTime.of(2020, 1, 1, 8, 0))
        .end(LocalDateTime.of(2020, 1, 1, 17, 00))
        .build();
  }
}
