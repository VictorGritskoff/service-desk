package io.flow.modules.teams.service.mapper;

import io.flow.modules.teams.domain.ProjectSetting;
import io.flow.modules.teams.service.dto.ProjectSettingDTO;
import io.flow.utils.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectSettingMapper extends BaseMapper {
    @Mapping(target = "projectId", source = "project.id")
    ProjectSettingDTO toDto(ProjectSetting setting);

    @Mapping(target = "project", expression = "java(toStub(dto.getProjectId(), Project.class))")
    ProjectSetting toEntity(ProjectSettingDTO dto);

    @Mapping(target = "project", expression = "java(toStub(dto.getProjectId(), Project.class))")
    void updateEntity(ProjectSettingDTO dto, @MappingTarget ProjectSetting entity);
}
