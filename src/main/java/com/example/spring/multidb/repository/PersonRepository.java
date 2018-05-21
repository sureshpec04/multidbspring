package com.example.spring.multidb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.example.spring.multidb.models.Person;

@Service
public interface PersonRepository extends JpaRepository<Person,Long> {
}
