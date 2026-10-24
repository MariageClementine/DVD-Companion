package io.github.mariageclementine.dvdco.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dlc")
public class Dlc {

    @Id
    @Column(name = "dlc_id")
    private Integer id;

    @Column(name = "dlc_name")
    private String name;

    public Integer getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }
}
