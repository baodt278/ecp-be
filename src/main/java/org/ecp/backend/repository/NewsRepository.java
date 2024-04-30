package org.ecp.backend.repository;

import org.ecp.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.acronym = :acronym ORDER BY n.time DESC")
    List<News> findByAcronym(String acronym);

    @Query("SELECT n FROM News n WHERE n.acronym IS NULL ORDER BY n.time DESC")
    List<News> findSystemNews();
    Optional<News> findByCode(String code);
}
