package com.dirty.code.service;

import com.dirty.code.controller.ExampleController;
import com.dirty.code.exception.ResourceNotFoundException;
import com.dirty.code.repository.ExampleRepository;
import com.dirty.code.repository.model.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExampleService implements ExampleController {

    private final ExampleRepository exampleRepository;

    @Override
    public List<Example> getAll() {
        return exampleRepository.findAll();
    }

    @Override
    public Example getById(Long id) {
        return exampleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Example not found with id " + id));
    }

    @Override
    public Example create(Example example) {
        return exampleRepository.save(example);
    }

    @Override
    public Example update(Long id, Example example) {
        return exampleRepository.findById(id).map(existingExample -> {
            existingExample.setName(example.getName());
            existingExample.setDescription(example.getDescription());
            return exampleRepository.save(existingExample);
        }).orElseThrow(() -> new ResourceNotFoundException("Example not found with id " + id));
    }

    @Override
    public void delete(Long id) {
        exampleRepository.deleteById(id);
    }
}
