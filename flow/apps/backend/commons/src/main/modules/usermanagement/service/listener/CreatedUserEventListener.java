package io.flow.modules.usermanagement.service.listener;

import io.flow.modules.collab.service.MailService;
import io.flow.modules.usermanagement.service.event.CreatedUserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreatedUserEventListener {

    private final MailService mailService;

    public CreatedUserEventListener(MailService mailService) {
        this.mailService = mailService;
    }

    @Async("asyncTaskExecutor")
    @Transactional
    @EventListener
    public void onCreatedUserEvent(CreatedUserEvent event) {
        mailService.sendCreationEmail(event.getUser());
    }
}
