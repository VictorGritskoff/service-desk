package io.flow.modules.usermanagement.repository;

import io.flow.modules.usermanagement.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, String> {}
