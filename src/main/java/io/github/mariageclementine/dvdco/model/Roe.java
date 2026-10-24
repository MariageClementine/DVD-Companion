package io.github.mariageclementine.dvdco.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roe")
public class Roe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roe_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fish_id")
    private Fish fish;

    @Column(name = "nb_roe")
    private Integer nbRoe;

    protected Roe(){}
    public Roe(User user, Fish fish, Integer nbRoe){
        this.user = user;
        this.fish = fish;
        this.nbRoe = nbRoe;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Fish getFish() {
        return fish;
    }

    public Integer getNbRoe() {
        return nbRoe;
    }

    public void setNbRoe(Integer nbRoe) {
        this.nbRoe = nbRoe;
    }
}
