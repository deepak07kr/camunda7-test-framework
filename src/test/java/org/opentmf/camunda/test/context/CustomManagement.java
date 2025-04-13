package org.opentmf.camunda.test.context;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CUSTOM_MANAGEMENT")
public class CustomManagement {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false)
    private boolean serviceTaskListenerTriggered;

    @Column(nullable = false)
    private boolean serviceTaskTriggered;
}
