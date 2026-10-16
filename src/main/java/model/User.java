package model;

import jakarta.persistence.*;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name="user_login")
    private String login;

    @Column(name="user_password")
    private String password;

    public User(){}
    public User(String login, String password){
        this.login = login;
        this.password = password;
    }
    public Integer getId(){
        return this.id;
    }

    public String getLogin(){
        return this.login;
    }

    public String getPassword(){
        return this.password;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
