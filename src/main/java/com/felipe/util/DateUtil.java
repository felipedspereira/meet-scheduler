package com.felipe.util;

import com.felipe.domain.Appointment;
import com.felipe.domain.TimeSlot;

public class DateUtil {

  public static int dateComparator(TimeSlot t1, TimeSlot t2) {
    return t1.getStart().compareTo(t2.getStart());
  }

  public static int dateComparator(Appointment t1, Appointment t2) {
    return t1.getStart().compareTo(t2.getStart());
  }
}
