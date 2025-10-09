package com.vibecoding.store_batch_job.repository;

import com.vibecoding.store_batch_job.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}