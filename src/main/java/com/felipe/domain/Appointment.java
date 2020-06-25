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
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Appointment {

  @JsonProperty("id")
  private String id;

  @JsonProperty("calendar_id")
  private String calendarId;

  @JsonProperty("start")
  private LocalDateTime start;

  @JsonProperty("end")
  private LocalDateTime end;

  public boolean isAppointmentWithinInterval(LocalDateTime start, LocalDateTime end) {
    return isBeforeOrEqual(this, start) && isAfterOrEqual(this, end);
  }

  private boolean isAfterOrEqual(Appointment appointment, LocalDateTime end) {
    return end.isAfter(appointment.getEnd()) || end.isEqual(appointment.getEnd());
  }

  private boolean isBeforeOrEqual(Appointment appointment, LocalDateTime start) {
    return start.isBefore(appointment.getStart()) || start.isEqual(appointment.getStart());
  }
}
