package io.flow.modules.teams.service;

import static io.flow.query.QueryUtils.createSpecification;

import io.flow.exceptions.ResourceNotFoundException;
import io.flow.modules.teams.domain.EstimationUnit;
import io.flow.modules.teams.domain.Project;
import io.flow.modules.teams.domain.Team;
import io.flow.modules.teams.domain.TicketPriority;
import io.flow.modules.teams.repository.ProjectRepository;
import io.flow.modules.teams.repository.TeamRepository;
import io.flow.modules.teams.service.dto.ProjectDTO;
import io.flow.modules.teams.service.dto.ProjectSettingDTO;
import io.flow.modules.teams.service.event.NewProjectCreatedEvent;
import io.flow.modules.teams.service.mapper.ProjectMapper;
import io.flow.query.QueryDTO;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectService {

    private final TeamRepository teamRepository;

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final ApplicationEventPublisher eventPublisher;

    public ProjectService(
            TeamRepository teamRepository,
            ProjectRepository projectRepository,
            ProjectMapper projectMapper,
            ApplicationEventPublisher eventPublisher) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.eventPublisher = eventPublisher;
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = projectMapper.toEntity(projectDTO);

        // Ensure we fetch the Team from the database before saving
        Team team =
                teamRepository
                        .findById(projectDTO.getTeamId())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Team not found with ID: "
                                                        + projectDTO.getTeamId()));
        project.setTeam(team);
        ProjectDTO savedProjectDTO = projectMapper.toDto(projectRepository.save(project));
        eventPublisher.publishEvent(new NewProjectCreatedEvent(this, savedProjectDTO));
        return savedProjectDTO;
    }

    public Optional<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDTO> findProjects(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Project> spec = createSpecification(queryDTO.orElse(null));
        return projectRepository.findAll(spec, pageable).map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProjectDTO> getProjectsByUserId(Long userId, Pageable pageable) {
        return projectRepository.findAllByUserId(userId, pageable).map(projectMapper::toDto);
    }

    public ProjectDTO getByShortName(String shortName) {
        return projectRepository
                .findByShortName(shortName)
                .map(
                        project -> {
                            ProjectDTO projectDTO = projectMapper.toDto(project);
                            if (projectDTO.getProjectSetting() == null) {
                                // Create default project setting
                                ProjectSettingDTO defaultSetting =
                                        ProjectSettingDTO.builder()
                                                .projectId(projectDTO.getId())
                                                .sprintLengthDays(14) // Default 2 weeks sprint
                                                .defaultPriority(
                                                        TicketPriority.Low) // Default low priority
                                                .estimationUnit(
                                                        EstimationUnit
                                                                .STORY_POINTS) // Default story
                                                // points
                                                .enableEstimation(true) // Default enable estimation
                                                .build();
                                projectDTO.setProjectSetting(defaultSetting);
                            }
                            return projectDTO;
                        })
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Cannot find project with short name '" + shortName + "'"));
    }

    public ProjectDTO updateProject(Long id, ProjectDTO updatedProject) {
        return projectRepository
                .findById(id)
                .map(
                        existingProject -> {
                            existingProject.setName(updatedProject.getName());
                            existingProject.setDescription(updatedProject.getDescription());
                            existingProject.setStatus(updatedProject.getStatus());
                            existingProject.setStartDate(updatedProject.getStartDate());
                            existingProject.setEndDate(updatedProject.getEndDate());
                            existingProject.setModifiedAt(updatedProject.getModifiedAt());
                            existingProject.setModifiedBy(updatedProject.getModifiedBy());
                            return projectMapper.toDto(projectRepository.save(existingProject));
                        })
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
