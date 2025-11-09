package io.flow.modules.teams.service.event;

import io.flow.modules.teams.service.dto.TeamDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewTeamCreatedEvent extends ApplicationEvent {
    private final TeamDTO team;

    public NewTeamCreatedEvent(Object source, TeamDTO team) {
        super(source);
        this.team = team;
    }
}
