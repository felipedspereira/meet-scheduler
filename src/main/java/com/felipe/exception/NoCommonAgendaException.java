package com.felipe.exception;

public class NoCommonAgendaException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public NoCommonAgendaException(String message) {
    super(message);
  }
}
