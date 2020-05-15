package com.felipe.domain;

import static java.util.stream.Collectors.toList;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.felipe.util.DateUtil;
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
public class Calendar {

  @JsonProperty("appointments")
  private List<Appointment> appointments;

  @JsonProperty("timeslots")
  private List<TimeSlot> timeSlots;

  public List<Appointment> getAppointmentsWithinInterval(LocalDateTime start, LocalDateTime end) {
    return this.getAppointments().stream()
        .filter(appointment -> isAppointmentWithinInterval(appointment, start, end))
        .sorted(DateUtil::dateComparator)
        .collect(toList());
  }

  private boolean isAppointmentWithinInterval(
      Appointment appointment, LocalDateTime start, LocalDateTime end) {
    return isBeforeOrEqual(appointment, start) && isAfterOrEqual(appointment, end);
  }

  private boolean isAfterOrEqual(Appointment appointment, LocalDateTime end) {
    return end.isAfter(appointment.getEnd()) || end.isEqual(appointment.getEnd());
  }

  private boolean isBeforeOrEqual(Appointment appointment, LocalDateTime start) {
    return start.isBefore(appointment.getStart()) || start.isEqual(appointment.getStart());
  }
}
