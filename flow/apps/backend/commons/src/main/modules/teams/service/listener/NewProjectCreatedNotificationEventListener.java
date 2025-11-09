package io.flow.modules.teams.service.listener;

import static j2html.TagCreator.a;
import static j2html.TagCreator.p;
import static j2html.TagCreator.text;

import io.flow.exceptions.ResourceNotFoundException;
import io.flow.modules.collab.domain.ActivityLog;
import io.flow.modules.collab.domain.EntityType;
import io.flow.modules.collab.domain.Notification;
import io.flow.modules.collab.domain.NotificationType;
import io.flow.modules.collab.repository.ActivityLogRepository;
import io.flow.modules.collab.repository.NotificationRepository;
import io.flow.modules.teams.repository.TeamRepository;
import io.flow.modules.teams.service.dto.ProjectDTO;
import io.flow.modules.teams.service.event.NewProjectCreatedEvent;
import io.flow.modules.usermanagement.domain.User;
import io.flow.modules.usermanagement.repository.UserRepository;
import io.flow.modules.usermanagement.service.dto.UserWithTeamRoleDTO;
import io.flow.utils.Obfuscator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewProjectCreatedNotificationEventListener {

    private final TeamRepository teamRepository;
    private final SimpMessagingTemplate messageTemplate;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public NewProjectCreatedNotificationEventListener(
            TeamRepository teamRepository,
            SimpMessagingTemplate messageTemplate,
            NotificationRepository notificationRepository,
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.messageTemplate = messageTemplate;
        this.notificationRepository = notificationRepository;
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewProjectCreated(NewProjectCreatedEvent event) {
        ProjectDTO projectDTO = event.getProjectDTO();

        List<UserWithTeamRoleDTO> usersInTeam =
                teamRepository.findUsersByTeamId(projectDTO.getTeamId());

        User requestUser =
                userRepository
                        .findOneById(projectDTO.getCreatedBy())
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found: " + projectDTO.getCreatedBy()));

        String html =
                p(
                                a(requestUser.getFirstName() + " " + requestUser.getLastName())
                                        .withHref(
                                                "/portal/users/"
                                                        + Obfuscator.obfuscate(
                                                                projectDTO.getCreatedBy())),
                                text(" has created a new project "),
                                a(projectDTO.getName())
                                        .withHref(
                                                "/portal/teams/"
                                                        + Obfuscator.obfuscate(
                                                                projectDTO.getTeamId())
                                                        + "/projects/"
                                                        + Obfuscator.obfuscate(projectDTO.getId())))
                        .render();

        List<Notification> notifications = new ArrayList<>();

        for (UserWithTeamRoleDTO user : usersInTeam) {
            if (!Objects.equals(user.getId(), projectDTO.getCreatedBy())) {
                Notification notification =
                        Notification.builder()
                                .content(html)
                                .type(NotificationType.INFO)
                                .user(User.builder().id(user.getId()).build())
                                .isRead(false)
                                .build();

                notifications.add(notification);
            }
        }

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

        for (Notification notification : savedNotifications) {
            messageTemplate.convertAndSendToUser(
                    String.valueOf(notification.getUser().getId()),
                    "/queue/notifications",
                    notification);
        }

        ActivityLog activityLog =
                ActivityLog.builder()
                        .entityId(projectDTO.getTeamId())
                        .entityType(EntityType.Team)
                        .content(html)
                        .build();
        activityLogRepository.save(activityLog);
    }
}
