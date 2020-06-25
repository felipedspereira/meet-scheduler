package com.felipe.service.collectors;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import static java.util.stream.Collectors.toList;

import com.felipe.domain.TimeSlot;
import com.felipe.exception.NoCommonAgendaException;
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
public class CalendarIntersectionCollector
    implements Collector<List<TimeSlot>, List<TimeSlot>, List<TimeSlot>> {

  private List<TimeSlot> firstCalendar;

  @Override
  public Supplier<List<TimeSlot>> supplier() {
    return () -> this.firstCalendar;
  }

  @Override
  public BiConsumer<List<TimeSlot>, List<TimeSlot>> accumulator() {
    return this::calculateIntersection;
  }

  @Override
  public BinaryOperator<List<TimeSlot>> combiner() {
    return (list1, list2) -> Stream.of(list1, list2).flatMap(List::stream).collect(toList());
  }

  @Override
  public Function<List<TimeSlot>, List<TimeSlot>> finisher() {
    return Function.identity();
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(UNORDERED, IDENTITY_FINISH, CONCURRENT);
  }

  private void calculateIntersection(List<TimeSlot> resultList, List<TimeSlot> otherCalendar) {
    List<TimeSlot> intersections =
        resultList.stream()
            .map(slot1 -> intersectListWithSlot(otherCalendar, slot1))
            .flatMap(List::stream)
            .collect(toList());

    intersections.stream().findAny().orElseThrow(throwException(resultList, otherCalendar));

    resultList.clear();
    resultList.addAll(intersections);
  }

  private List<TimeSlot> intersectListWithSlot(List<TimeSlot> otherCalendar, TimeSlot slot1) {
    return otherCalendar.stream()
        .filter(slot2 -> slot1.isOverlapping(slot2))
        .map(slot2 -> slot1.mapIntersection(slot2))
        .collect(toList());
  }

  private static Supplier<? extends NoCommonAgendaException> throwException(
      List<TimeSlot> calendar1, List<TimeSlot> calendar2) {
    return () ->
        new NoCommonAgendaException(
            String.format(
                "No common free time between calendar %s and %s",
                calendar1.stream().findAny().get().getCalendarId(),
                calendar2.stream().findAny().get().getCalendarId()));
  }
}
