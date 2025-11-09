package io.flow.modules.teams.service.event;

import io.flow.modules.collab.service.dto.CommentDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketCommentCreatedEvent extends ApplicationEvent {
    private final CommentDTO commentDTO;

    public TicketCommentCreatedEvent(Object source, CommentDTO commentDTO) {
        super(source);
        this.commentDTO = commentDTO;
    }
}
