package io.flow.modules.teams.service.event;

import io.flow.modules.teams.service.dto.TicketDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewTicketCreatedEvent extends ApplicationEvent {
    private final TicketDTO ticket;

    public NewTicketCreatedEvent(Object source, TicketDTO ticket) {
        super(source);
        this.ticket = ticket;
    }
}
