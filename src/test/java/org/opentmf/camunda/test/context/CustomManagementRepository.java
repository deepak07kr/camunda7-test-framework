package org.opentmf.camunda.test.context;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomManagementRepository extends JpaRepository<CustomManagement, String> {

}
