package com.example.TTMS.entity;

import lombok.Getter;

@Getter
public enum TransportStatus {

  AVAILABLE("Available"),
  ASSIGNED("Assigned"),
  ON_TRIP("On Trip"),
  MAINTENANCE("Maintenance"),
  UNAVAILABLE("Unavailable");

  private final String label;

  private TransportStatus(String label) {
        this.label = label;
    }

  public String getLabel() {
    return label;
  }
}
