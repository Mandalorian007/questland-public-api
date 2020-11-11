package com.questland.handbook.repository;

import com.questland.handbook.publicmodel.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {

}
