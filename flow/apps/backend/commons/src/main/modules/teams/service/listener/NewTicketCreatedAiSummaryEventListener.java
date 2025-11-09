package io.flow.modules.teams.service.listener;

import io.flow.modules.teams.service.TicketHealthEvalService;
import io.flow.modules.teams.service.dto.TicketDTO;
import io.flow.modules.teams.service.event.NewTicketCreatedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(TicketHealthEvalService.class)
public class NewTicketCreatedAiSummaryEventListener {

    private final TicketHealthEvalService ticketHealthEvalService;

    public NewTicketCreatedAiSummaryEventListener(TicketHealthEvalService ticketHealthEvalService) {
        this.ticketHealthEvalService = ticketHealthEvalService;
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void onNewTicketCreated(NewTicketCreatedEvent event) {
        TicketDTO ticketDTO = event.getTicket();
        ticketHealthEvalService.evaluateConversationHealth(
                ticketDTO.getId(),
                "Title: "
                        + ticketDTO.getRequestTitle()
                        + "\n"
                        + "Description: "
                        + ticketDTO.getRequestDescription()
                        + "\n",
                true);
    }
}
