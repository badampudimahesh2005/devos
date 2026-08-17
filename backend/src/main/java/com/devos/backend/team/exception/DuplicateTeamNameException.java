package com.devos.backend.team.exception;

public class DuplicateTeamNameException extends RuntimeException {

    public DuplicateTeamNameException(String teamName) {
        super("Team with name '" + teamName + "' already exists");
    }
}