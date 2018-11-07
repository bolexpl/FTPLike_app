package com.ftpl.server.db;

/**
 * Klasa zawierająca dane o zarejestrowanym użytkowniku
 * */
public class User {

    private int id;
    private String login;

    public User(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

}
