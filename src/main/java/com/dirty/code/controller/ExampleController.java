package com.dirty.code.controller;

import com.dirty.code.repository.model.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examples")
public interface ExampleController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<Example> getAll();

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Example getById(@PathVariable Long id);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Example create(@RequestBody Example example);
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Example update(@PathVariable Long id, @RequestBody Example example);
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void delete(@PathVariable Long id);
}
