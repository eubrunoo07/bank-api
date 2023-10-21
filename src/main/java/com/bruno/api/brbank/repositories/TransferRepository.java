package com.bruno.api.brbank.repositories;

import com.bruno.api.brbank.entities.Transfers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfers, Long> {
}
