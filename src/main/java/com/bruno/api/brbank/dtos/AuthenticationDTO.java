package com.bruno.api.brbank.dtos;

import lombok.Getter;
import lombok.Setter;

public record AuthenticationDTO(String login, String password) {
}
