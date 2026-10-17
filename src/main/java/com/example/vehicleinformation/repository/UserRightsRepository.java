package com.example.vehicleinformation.repository;

import com.example.vehicleinformation.model.UserRights;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRightsRepository extends JpaRepository<UserRights, Long> {
}
