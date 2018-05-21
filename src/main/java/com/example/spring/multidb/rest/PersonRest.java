package com.example.spring.multidb.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.multidb.models.Person;
import com.example.spring.multidb.repository.PersonRepository;


@RestController
@RequestMapping(value = "/person")
public class PersonRest {

    @Autowired private PersonRepository repository;

    @RequestMapping(value = "/all")
    public Iterable<Person> getAll(){
        return repository.findAll();
    }
}
