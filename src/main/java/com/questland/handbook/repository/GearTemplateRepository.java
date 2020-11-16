package com.questland.handbook.repository;

import com.questland.handbook.publicmodel.GearTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GearTemplateRepository extends JpaRepository<GearTemplate, UUID> {
    List<GearTemplate> findByGoogleId(String googleId);
}
