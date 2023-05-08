package com.sowatec.search.input;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InputRepository extends JpaRepository<Input, Long> {
    Optional<Input> findInputByUserInput (String userInput);
}