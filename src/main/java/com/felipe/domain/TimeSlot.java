package com.felipe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {

  @JsonProperty("id")
  private String id;

  @JsonProperty("calendar_id")
  private String calendarId;

  @JsonProperty("start")
  private LocalDateTime start;

  @JsonProperty("end")
  private LocalDateTime end;

  public boolean isOverlapping(TimeSlot timeSlot) {
    return this.getStart().isBefore(timeSlot.getStart())
        ? isThereOverlapping(this, timeSlot)
        : isThereOverlapping(timeSlot, this);
  }

  private static boolean isThereOverlapping(TimeSlot timeSlot1, TimeSlot timeSlot2) {
    return timeSlot1.getStart().isBefore(timeSlot2.getEnd())
        && timeSlot2.getStart().isBefore(timeSlot1.getEnd());
  }

  public TimeSlot mapIntersection(TimeSlot timeSlot) {
    LocalDateTime start1 = this.getStart();
    LocalDateTime end1 = this.getEnd();
    LocalDateTime start2 = timeSlot.getStart();
    LocalDateTime end2 = timeSlot.getEnd();

    TimeSlot newTimeSlot = null;

    if (start1.isBefore(start2) && end1.isBefore(end2)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start2).end(end1).build();
    }
    if (start2.isBefore(start1) && end2.isBefore(end1)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start1).end(end2).build();
    }
    if (start1.equals(start2) && end2.isBefore(end1)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start1).end(end2).build();
    }
    if (start1.equals(start2) && end1.isBefore(end2)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start1).end(end1).build();
    }
    if (start1.isBefore(start2) && end1.equals(end2)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start2).end(end2).build();
    }
    if (start2.isBefore(start1) && end1.equals(end2)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start1).end(end2).build();
    }
    if (start1.equals(start2) && end1.equals(end2)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start1).end(end1).build();
    }
    if (start2.isBefore(start1) && end1.isBefore(end2)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start1).end(end1).build();
    }
    if (start1.isBefore(start2) && end2.isBefore(end1)) {
      newTimeSlot =
          TimeSlot.builder().calendarId(this.getCalendarId()).start(start2).end(end2).build();
    }

    if (newTimeSlot == null) {
      throw new IllegalStateException(
          String.format("scenario not mapped for this[%s] and timeSlot2[%s]", this, timeSlot));
    }

    return newTimeSlot;
  }
}
