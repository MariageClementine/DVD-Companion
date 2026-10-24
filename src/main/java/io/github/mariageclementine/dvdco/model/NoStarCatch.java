package io.github.mariageclementine.dvdco.model;

import jakarta.persistence.*;

@Entity
@Table(name = "no_star_catch")
public class NoStarCatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nscatch_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nsf_id")
    private NoStarFish noStarFish;

    protected NoStarCatch(){}
    public NoStarCatch(User user, NoStarFish noStarFish){
        this.user = user;
        this.noStarFish = noStarFish;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public NoStarFish getNoStarFish() {
        return noStarFish;
    }

}
