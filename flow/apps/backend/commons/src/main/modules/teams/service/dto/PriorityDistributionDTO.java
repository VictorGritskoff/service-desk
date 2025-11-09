package io.flow.modules.teams.service.dto;

import io.flow.modules.teams.domain.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriorityDistributionDTO {
    private TicketPriority priority;
    private Long ticketCount;
}
