package com.work.newrrow.domain.group;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByGid(String gid);
    Optional<Group> findByCode(String code);
    boolean existsByGid(String gid);
    boolean existsByCode(String code);
}