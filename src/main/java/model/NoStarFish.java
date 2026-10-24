package model;

import jakarta.persistence.*;

@Entity
@Table(name = "no_star_fish")
public class NoStarFish {

    @Id
    @Column(name = "nsf_id")
    private Integer id;

    @Column(name = "nsf_name")
    private String name;

    @Column(name = "nsf_rank")
    private Integer rank;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public Integer getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public Integer getRank() {
        return this.rank;
    }

    public Zone getZone() {
        return this.zone;
    }
}
