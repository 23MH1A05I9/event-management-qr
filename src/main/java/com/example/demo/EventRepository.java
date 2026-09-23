package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Increment registered count when someone registers
    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.registeredCount = e.registeredCount + :count WHERE e.id = :id")
    void incrementRegisteredCount(Long id, int count);
}