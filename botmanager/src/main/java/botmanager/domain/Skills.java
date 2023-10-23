package botmanager.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="skills")
public class Skills {



    @Id
    @Column(name = "account_login")
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_login")
    private Account account;


    private int attack;
    private int defence;
    private int strength;
    private int hitpoints;
    private int ranged;
    private int prayer;
    private int magic;
    private int cooking;
    private int woodcutting;
    private int fletching;
    private int fishing;
    private int firemaking;
    private int crafting;
    private int smithing;
    private int mining;
    private int herblore;
    private int agility;
    private int thieving;
    private int slayer;
    private int farming;
    private int runecrafting;
    private int hunter;
    private int construction;

    Timestamp lastUpdated;

    public Skills(){

    }
}
