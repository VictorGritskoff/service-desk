package io.flow.modules.teams.service.listener;

import static io.flow.modules.teams.utils.PathUtils.buildTicketPath;

import io.flow.modules.collab.EmailContext;
import io.flow.modules.collab.domain.EntityType;
import io.flow.modules.collab.domain.EntityWatcher;
import io.flow.modules.collab.repository.EntityWatcherRepository;
import io.flow.modules.collab.service.MailService;
import io.flow.modules.teams.service.TicketService;
import io.flow.modules.teams.service.dto.TicketDTO;
import io.flow.modules.teams.service.event.NewTicketCreatedEvent;
import io.flow.modules.usermanagement.service.mapper.UserMapper;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Event listener for new ticket creation events. This listener is responsible for sending email
 * notifications to watchers of a ticket when a new ticket is created. Emails are sent
 * asynchronously to all watchers of the ticket.
 */
@Component
public class NewTicketCreatedMailEventListener {

    private final UserMapper userMapper;
    private final EntityWatcherRepository entityWatcherRepository;
    private final TicketService ticketService;
    private final MailService mailService;
    private final MessageSource messageSource;

    public NewTicketCreatedMailEventListener(
            EntityWatcherRepository entityWatcherRepository,
            TicketService ticketService,
            MailService mailService,
            UserMapper userMapper,
            MessageSource messageSource) {
        this.entityWatcherRepository = entityWatcherRepository;
        this.ticketService = ticketService;
        this.mailService = mailService;
        this.userMapper = userMapper;
        this.messageSource = messageSource;
    }

    /**
     * Handles the new ticket created event. This method is triggered when a new ticket is created.
     * It retrieves the ticket details, finds all watchers of the ticket, and sends email
     * notifications to each watcher.
     *
     * @param event The event containing information about the created ticket
     */
    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onNewTicketCreated(NewTicketCreatedEvent event) {
        TicketDTO ticketDTO = ticketService.getTicketById(event.getTicket().getId());
        List<EntityWatcher> watchers =
                entityWatcherRepository.findByEntityTypeAndEntityId(
                        EntityType.Ticket, ticketDTO.getId());

        if (!watchers.isEmpty()) {
            watchers.forEach(
                    watcher -> {
                        String ticketPath = buildTicketPath(ticketDTO);
                        EmailContext emailContext =
                                new EmailContext(
                                                Locale.forLanguageTag("en"),
                                                mailService.getBaseUrl(),
                                                messageSource)
                                        .setToUser(userMapper.toDto(watcher.getWatchUser()))
                                        .setSubject(
                                                "email.new.ticket.subject",
                                                ticketDTO.getRequestTitle())
                                        .addVariable("ticket", ticketDTO)
                                        .addVariable("ticketPath", ticketPath)
                                        .setTemplate("mail/newTicketEmail");
                        mailService.sendEmail(emailContext);
                    });
        }
    }
}
