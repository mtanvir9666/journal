package com.daily.journal.exception;

public class JournalNotFoundException extends RuntimeException {

  public JournalNotFoundException(Long id) {
    super("Could not find journal with id: " + id);
  }
}
