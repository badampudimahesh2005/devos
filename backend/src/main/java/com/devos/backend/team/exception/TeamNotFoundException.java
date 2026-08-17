package com.devos.backend.team.exception;

public class TeamNotFoundException extends RuntimeException {

    public TeamNotFoundException(Long teamId) {
        super("Team not found with id: " + teamId);
    }
}