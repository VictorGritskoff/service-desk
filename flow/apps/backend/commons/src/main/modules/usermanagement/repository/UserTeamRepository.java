package io.flow.modules.usermanagement.repository;

import io.flow.modules.usermanagement.domain.UserTeam;
import io.flow.modules.usermanagement.domain.UserTeamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamId> {}
