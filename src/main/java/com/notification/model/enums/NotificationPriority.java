package com.notification.model.enums;

public enum NotificationPriority {
    HIGH(1),
    MEDIUM(2),
    LOW(3);

    private final int weight;

    NotificationPriority(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
