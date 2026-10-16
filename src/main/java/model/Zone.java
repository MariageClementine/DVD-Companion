package model;


import jakarta.persistence.*;

@Entity
@Table(name="zone")
public class Zone {

    @Id
    @Column(name="zone_id")
    private Integer id;

    @Column(name="zone_name")
    private String name;

    @Column(name="zone_depth_min")
    private Integer depthMin;

    @Column(name="zone_depth_max")
    private Integer depthMax;

    @ManyToOne(optional=true)
    @JoinColumn(name="dlc_id")
    private Dlc dlc;

    public Integer getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public Integer getDepthMin(){
        return this.depthMin;
    }


    public Integer getDepthMax(){
        return this.depthMax;
    }

    public Dlc getDlc(){
        return this.dlc;
    }
}
