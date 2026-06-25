package com.example.lab3.repository;

import com.example.lab3.entity.Summoner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummonerRepository extends CrudRepository<Summoner, Long> {
}