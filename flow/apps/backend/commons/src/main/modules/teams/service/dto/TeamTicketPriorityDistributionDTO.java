package io.flow.modules.teams.service.dto;

import io.flow.modules.teams.domain.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamTicketPriorityDistributionDTO {
    private Long teamId;
    private String teamName;
    private TicketPriority priority;
    private Long count;
}
