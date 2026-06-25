package com.example.lab3.repository;

import com.example.lab3.entity.GameClass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameClassRepository extends CrudRepository<GameClass, Long> {
}