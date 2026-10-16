package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
