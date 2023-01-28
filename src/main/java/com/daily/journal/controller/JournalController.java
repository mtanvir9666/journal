package com.daily.journal.controller;

import com.daily.journal.exception.JournalNotFoundException;
import com.daily.journal.model.Journal;
import com.daily.journal.repository.JournalRepository;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JournalController {

  private final JournalRepository repository;

  public JournalController(JournalRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/journal/{id}")
  Journal findJournal(@PathVariable Long id) {
    return repository.findById(id)
      .orElseThrow(() -> new JournalNotFoundException(id));
  }

  @GetMapping("/journal")
  List<Journal> all() {
    return repository.findAll();
  }

  @PostMapping("/journal")
  Journal createJournal(@RequestBody Journal newJournal) {
    return repository.save(newJournal);
  }

  @PutMapping("/journal/{id}")
  Journal updateJournal(@RequestBody Journal newEmployee, @PathVariable Long id) {

    return repository.findById(id)
      .map(journal -> {
        journal.setContent(newEmployee.getContent());
        return repository.save(journal);
      })
      .orElseThrow(() -> {
        throw new JournalNotFoundException(id);
      });
  }

  @DeleteMapping("/journal/{id}")
  void removeJournal(@PathVariable Long id) {
    repository.deleteById(id);
  }

}
