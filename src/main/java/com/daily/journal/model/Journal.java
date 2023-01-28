package com.daily.journal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Journal {

  @Id
  @GeneratedValue
  private Long id;

  private String content;
}
