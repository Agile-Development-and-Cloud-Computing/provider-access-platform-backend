package com.fuas.providers_access_platform.repository;

import com.fuas.providers_access_platform.model.RoleOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleOfferRepository extends JpaRepository<RoleOffer, Long> {
}
