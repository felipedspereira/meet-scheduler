package com.felipe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class Appointment {

  @JsonProperty("id")
  private String id;

  @JsonProperty("calendar_id")
  private String calendarId;

  @JsonProperty("start")
  private LocalDateTime start;

  @JsonProperty("end")
  private LocalDateTime end;
}
