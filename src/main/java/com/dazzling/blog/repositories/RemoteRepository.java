package com.dazzling.blog.repositories;

import com.dazzling.blog.models.Remote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RemoteRepository extends JpaRepository<Remote, Long> {

    @Query("SELECT r FROM Remote r WHERE r.articleId = :articleId AND r.address = :address")
    Remote findRemoteByMuilFields(@Param("articleId") Long articleId, @Param("address") String address);

    Remote findByAddress(String address);
}