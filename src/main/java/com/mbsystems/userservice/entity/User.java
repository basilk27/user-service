package com.mbsystems.userservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
        @Id
        Long id,
        String name,
        String surname,
        String email,
        String address,
        Boolean alerting,
        Double energyAlertingThreshold
) {
        public static User create(
                String name,
                String surname,
                String email,
                String address,
                Boolean alerting,
                Double energyAlertingThreshold) {
        return new User(null, name, surname, email, address, alerting, energyAlertingThreshold);
    }
}
