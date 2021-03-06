package com.ftpl.server.db;

import com.ftpl.server.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa singleton odpowiedzialna za połączenie z bazą danych
 * */
public class SQLiteJDBC {

    private Connection c;
    private Statement statement;

    private static SQLiteJDBC ourInstance = new SQLiteJDBC();

    public static SQLiteJDBC getInstance() {
        return ourInstance;
    }

    private SQLiteJDBC() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+ Main.database);
            statement = c.createStatement();
            createTable();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Metoda tworząca tabelę w bazie
     * @throws SQLException wyjątek
     * */
    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login VARCHAR(50)," +
                "pass VARCHAR(50))";
        statement.executeUpdate(sql);
    }

    /**
     * Metoda do zapisania nowego użytkownika
     * @param login login
     * @param pass hasło
     * @throws SQLException wyjątek
     * */
    public void insert(String login, String pass) throws SQLException {
        String sql = "INSERT INTO users (login, pass) VALUES ('" + login + "','" + pass + "');";
        statement.executeUpdate(sql);
    }

    /**
     * Metoda do usunięcia użytkownika
     * @param id id użytkownika
     * @throws SQLException wyjątek
     * */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=" + id + ";";
        statement.executeUpdate(sql);
    }

    /**
     * Metoda Zwracająca wszystkich użytkowników
     * @return lista użytkowników
     * @throws SQLException wyjątek
     * */
    public List<User> selectAll() throws SQLException {
        return select(null, null);
    }

    /**
     * Metoda zwracająca użytkownika o wybranym loginie i haśle
     * @param login login
     * @param pass hasło
     * @return lista użytkowników
     * @throws SQLException wyjątek
     * */
    public List<User> select(String login, String pass) throws SQLException {
        String sql;
        if (login != null) {
            if (pass != null)
                sql = "SELECT * FROM users WHERE login='" + login + "' AND pass='" + pass + "';";
            else
                sql = "SELECT * FROM users WHERE login='" + login + "';";

        } else
            sql = "SELECT * FROM users;";

        ResultSet rs = statement.executeQuery(sql);

        List<User> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new User(
                    rs.getInt("id"),
                    rs.getString("login")
            ));
        }

        rs.close();

        return list;
    }

    @Override
    protected void finalize() throws Throwable {
        statement.close();
        c.close();
        super.finalize();
    }
}
