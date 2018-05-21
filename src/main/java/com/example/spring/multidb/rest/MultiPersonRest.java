package com.example.spring.multidb.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.multidb.models.Person;
import com.example.spring.multidb.repository.PersonRepository;
import com.example.spring.multidb.utils.EntityManagerUtils;


@RestController
@RequestMapping(value = "/multiperson")

public class MultiPersonRest {

    @Autowired 
    private PersonRepository repository;
    
    @Autowired 
    private HttpServletRequest context;
    
    @Autowired 
    private EntityManagerUtils emUtils;

    @RequestMapping(value = "/all")
    public Iterable<Person> getAll() throws Exception{
        setRepository( context.getRequestURL().toString() );
        return repository.findAll();
    }

    @GetMapping(value = "/db/{dbName}/single/{id}")
    public Person getSingle(@PathVariable("dbName") String dbName , @PathVariable("id") Long id) throws Exception{
        //setRepository( context.getRequestURL().toString() );
        setRepository( dbName );
        return repository.findById(id).orElseThrow(() -> new Exception("Person Not Found..."));
    }

    private void setRepository(String url) throws Exception{
        repository = emUtils.getJpaFactory(url).getRepository(PersonRepository.class);
    }

}

