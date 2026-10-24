package io.github.mariageclementine.dvdco.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "user_login")
    private String login;

    @Column(name = "user_password_hash")
    private String passwordHash;

    protected User(){}
    public User(String login, String passwordHash){
        this.login = login;
        this.passwordHash = passwordHash;
    }
    public Integer getId(){
        return this.id;
    }

    public String getLogin(){
        return this.login;
    }

    public String getPasswordHash(){
        return this.passwordHash;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
}
