package io.flow.modules.teams.service.mapper;

import io.flow.modules.teams.domain.Project;
import io.flow.modules.teams.service.dto.ProjectDTO;
import io.flow.modules.usermanagement.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ProjectSettingMapper.class})
public interface ProjectMapper {

    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "team.name", target = "teamName")
    @Mapping(source = "projectSetting", target = "projectSetting")
    ProjectDTO toDto(Project project);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(target = "createdByUser", expression = "java(ofUser(projectDTO.getCreatedBy()))")
    @Mapping(source = "projectSetting", target = "projectSetting")
    Project toEntity(ProjectDTO projectDTO);

    default User ofUser(Long userId) {
        return (userId == null) ? null : User.builder().id(userId).build();
    }
}
