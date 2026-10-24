package model;

import jakarta.persistence.*;

@Entity
@Table(name = "catch")
public class FishCatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catch_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fish_id")
    private Fish fish;

    @Column(name = "best_nb_stars")
    private Integer bestNbStars;

    public FishCatch(){}
    public FishCatch(User user, Fish fish, Integer bestNbStars){
        this.user = user;
        this.fish = fish;
        this.bestNbStars = bestNbStars;
    }

    public User getUser() {
        return user;
    }

    public Fish getFish() {
        return fish;
    }

    public Integer getBestNbStars() {
        return bestNbStars;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }

    public void setBestNbStars(Integer bestNbStars) {
        this.bestNbStars = bestNbStars;
    }
}
