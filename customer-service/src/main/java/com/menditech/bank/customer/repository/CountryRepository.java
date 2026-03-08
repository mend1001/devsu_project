package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByIso2(String iso2);
}
