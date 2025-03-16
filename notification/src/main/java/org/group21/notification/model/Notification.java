package org.group21.notification.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String passenger;
    private String email;
    private Journey journey;
    private String message;
}