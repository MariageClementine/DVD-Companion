package model;


import jakarta.persistence.*;

@Entity
@Table(name="fish")
public class Fish {

    @Id
    @Column(name="fish_id")
    private Integer id;

    @Column(name="fish_name")
    private String name;

    @Column(name="fish_rank")
    private Integer rank;

    @Column(name="active_time")
    @Enumerated(EnumType.STRING)
    private ActiveTime activeTime;

    @Column(name="always_three_stars")
    private Boolean alwaysThreeStars;

    @ManyToOne(optional=false)
    @JoinColumn(name="zone_id")
    private Zone zone;

    @ManyToOne(optional=true)
    @JoinColumn(name="dlc_id")
    private Dlc dlc;
}
