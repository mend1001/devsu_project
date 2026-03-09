package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByNumber(String number);

    List<AccountEntity> findByClientId(Long clientId);

    @Query("""
           select max(cast(a.number as long))
           from AccountEntity a
           where a.accountType.code = :accountTypeCode
           """)
    Long findMaxAccountNumberByAccountTypeCode(String accountTypeCode);

    boolean existsByNumber(String number);
}