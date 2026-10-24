package model;

import jakarta.persistence.*;

@Entity
@Table(name = "no_star_catch")
public class NoStarCatch {

    @Id
    @Column(name = "nscatch_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nsf_id")
    private NoStarFish noStarFish;

    public NoStarCatch(){}
    public NoStarCatch(User user, NoStarFish noStarFish){
        this.user = user;
        this.noStarFish = noStarFish;
    }

    public User getUser() {
        return user;
    }

    public NoStarFish getNoStarFish() {
        return noStarFish;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNoStarFish(NoStarFish noStarFish) {
        this.noStarFish = noStarFish;
    }
}
