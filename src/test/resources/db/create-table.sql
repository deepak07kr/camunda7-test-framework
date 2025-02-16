CREATE TABLE CUSTOM_MANAGEMENT
(
    "id"                            VARCHAR(50) NOT NULL UNIQUE,
    "status"                        VARCHAR(21) NOT NULL,
    service_task_listener_triggered BOOLEAN     NOT NULL DEFAULT FALSE,
    service_task_triggered          BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY ("id")
);
