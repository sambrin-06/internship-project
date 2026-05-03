package com.ict.internal_controls_testing.repository;

import com.ict.internal_controls_testing.entity.Control;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlRepository extends JpaRepository<Control, Long> {
    Page<Control> findAll(Pageable pageable);
    List<Control> findByStatus(String status);
}
