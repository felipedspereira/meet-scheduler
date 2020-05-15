package com.felipe.service;

import static org.junit.Assert.assertTrue;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalendarServiceIntegrationTest {
  @Autowired private CalendarService calendarService;

  private LocalDateTime startPeriod = LocalDateTime.of(2019, 4, 23, 8, 0);
  private LocalDateTime endPeriod = LocalDateTime.of(2019, 4, 23, 17, 0);

  @Test
  public void shouldTestWithRealCalendars() {
    String calendar1 = "48cadf26-975e-11e5-b9c2-c8e0eb18c1e9";
    String calendar2 = "452dccfc-975e-11e5-bfa5-c8e0eb18c1e9";

    List<String> calendars = List.of(calendar1, calendar2);

    calendarService.findAvailableTime(calendars, 60, startPeriod, endPeriod);
    
    assertTrue(calendars.size() > 0);
  }
}
