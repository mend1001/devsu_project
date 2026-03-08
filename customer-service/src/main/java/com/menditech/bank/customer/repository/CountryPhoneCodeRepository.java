package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.CountryPhoneCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryPhoneCodeRepository extends JpaRepository<CountryPhoneCodeEntity, Long> {
    List<CountryPhoneCodeEntity> findByCountryId(Long countryId);
}