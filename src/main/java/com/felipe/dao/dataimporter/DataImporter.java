package com.felipe.dao.dataimporter;

import java.util.List;

public abstract class DataImporter<K> {
  public abstract List<K> getData();
}
