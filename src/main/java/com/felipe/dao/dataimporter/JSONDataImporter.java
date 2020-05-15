package com.felipe.dao.dataimporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.felipe.domain.Calendar;

@Repository
public class JSONDataImporter extends DataImporter<Calendar> {
  private static final Logger logger = LoggerFactory.getLogger(JSONDataImporter.class);
  private final ObjectMapper mapper;
  private final Path path = Paths.get("data/");

  public JSONDataImporter() {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
  }

  @Override
  public List<Calendar> getData() {
    return getCalendars();
  }

  private List<Calendar> getCalendars() {
    try {
      return Files.list(path).map(this::fromJsonToCalendar).collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error listing files:", e);
    }

    throw new IllegalStateException(String.format("Invalid path: %s", path));
  }

  private Calendar fromJsonToCalendar(Path path) {
    try {
      return mapper.readValue(new File(path.toString()), Calendar.class);
    } catch (IOException e) {
      logger.error("Error while importing JSON file: ", e);
    }

    throw new IllegalStateException(String.format("Unexpected format on JSON file: %s", path));
  }
}
