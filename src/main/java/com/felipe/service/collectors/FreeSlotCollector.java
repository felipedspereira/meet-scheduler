package com.felipe.service.collectors;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import static java.util.stream.Collectors.toList;

import com.felipe.domain.Appointment;
import com.felipe.domain.TimeSlot;
import com.felipe.util.DateUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import lombok.Builder;

@Builder
public class FreeSlotCollector implements Collector<Appointment, List<TimeSlot>, List<TimeSlot>> {
  private int iteration;
  private int appointmentsTotal;
  private LocalDateTime startPeriod;
  private LocalDateTime endPeriod;
  private Appointment previous, current;

  @Override
  public Supplier<List<TimeSlot>> supplier() {
    return ArrayList::new;
  }

  @Override
  public BiConsumer<List<TimeSlot>, Appointment> accumulator() {
    return this::calculateFreeSlots;
  }

  @Override
  public BinaryOperator<List<TimeSlot>> combiner() {
    return (list1, list2) -> Stream.of(list1, list2).flatMap(List::stream).collect(toList());
  }

  @Override
  public Function<List<TimeSlot>, List<TimeSlot>> finisher() {
    return list -> list.stream().sorted(DateUtil::dateComparator).collect(toList());
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(UNORDERED, IDENTITY_FINISH, CONCURRENT);
  }

  private void calculateFreeSlots(List<TimeSlot> resultList, Appointment appointment) {
    previous = current;
    current = appointment;

    if (isFirstAppointment()) {
      resultList.add(getFreeSlotBeforeFirstAppointment());
    } else {
      resultList.add(getFreeSlotBetweenAppointments(appointment));
    }

    if (isLastAppointment()) {
      resultList.add(getFreeSlotAfterLastAppointment());
    }

    iteration++;
  }

  private boolean isLastAppointment() {
    return iteration == this.appointmentsTotal - 1;
  }

  private boolean isFirstAppointment() {
    return iteration == 0;
  }

  private TimeSlot getFreeSlotBeforeFirstAppointment() {
    return TimeSlot.builder()
        .calendarId(current.getCalendarId())
        .start(startPeriod)
        .end(current.getStart())
        .build();
  }

  private TimeSlot getFreeSlotBetweenAppointments(Appointment appointment) {
    return TimeSlot.builder()
        .calendarId(appointment.getCalendarId())
        .start(previous.getEnd())
        .end(current.getStart())
        .build();
  }

  private TimeSlot getFreeSlotAfterLastAppointment() {
    return TimeSlot.builder()
        .calendarId(current.getCalendarId())
        .start(current.getEnd())
        .end(endPeriod)
        .build();
  }
}
