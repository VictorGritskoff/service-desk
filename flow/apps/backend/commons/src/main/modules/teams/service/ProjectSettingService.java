package io.flow.modules.teams.service;

import static io.flow.modules.teams.domain.EstimationUnit.STORY_POINTS;

import io.flow.exceptions.ResourceNotFoundException;
import io.flow.modules.teams.domain.ProjectSetting;
import io.flow.modules.teams.domain.TicketPriority;
import io.flow.modules.teams.repository.ProjectRepository;
import io.flow.modules.teams.repository.ProjectSettingRepository;
import io.flow.modules.teams.service.dto.ProjectSettingDTO;
import io.flow.modules.teams.service.mapper.ProjectSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectSettingService {

    private final ProjectSettingRepository projectSettingRepository;
    private final ProjectRepository projectRepository;
    private final ProjectSettingMapper projectSettingMapper;

    @Transactional(readOnly = true)
    public ProjectSettingDTO getByProjectId(Long projectId) {
        return projectSettingRepository
                .findByProjectId(projectId)
                .map(projectSettingMapper::toDto)
                .orElseGet(() -> constructDefaultSetting(projectId));
    }

    @Transactional
    public ProjectSettingDTO save(ProjectSettingDTO dto) {
        ProjectSetting setting = projectSettingMapper.toEntity(dto);
        ProjectSetting saved = projectSettingRepository.save(setting);
        return projectSettingMapper.toDto(saved);
    }

    @Transactional
    public ProjectSettingDTO updateByProjectId(Long projectId, ProjectSettingDTO dto) {
        // Ensure the project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }

        // Set the project ID in the DTO
        dto.setProjectId(projectId);

        return projectSettingRepository
                .findByProjectId(projectId)
                .map(
                        existing -> {
                            projectSettingMapper.updateEntity(dto, existing);
                            ProjectSetting saved = projectSettingRepository.save(existing);
                            return projectSettingMapper.toDto(saved);
                        })
                .orElseGet(
                        () -> {
                            ProjectSetting newSetting = projectSettingMapper.toEntity(dto);
                            ProjectSetting saved = projectSettingRepository.save(newSetting);
                            return projectSettingMapper.toDto(saved);
                        });
    }

    private ProjectSettingDTO constructDefaultSetting(Long projectId) {
        // Ensure the project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }

        ProjectSettingDTO defaultDto = new ProjectSettingDTO();
        defaultDto.setProjectId(projectId);
        defaultDto.setSprintLengthDays(14);
        defaultDto.setDefaultPriority(TicketPriority.Low); // Medium
        defaultDto.setEstimationUnit(STORY_POINTS);
        defaultDto.setEnableEstimation(true);
        defaultDto.setIntegrationSettings(null);

        return defaultDto;
    }
}
