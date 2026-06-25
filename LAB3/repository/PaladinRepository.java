package com.example.lab3.repository;

import com.example.lab3.entity.Paladin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaladinRepository extends CrudRepository<Paladin, Long> {
}