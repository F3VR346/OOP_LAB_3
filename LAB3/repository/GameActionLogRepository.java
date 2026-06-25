package com.example.lab3.repository;

import com.example.lab3.entity.GameActionLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameActionLogRepository extends CrudRepository<GameActionLog, Long> {

}