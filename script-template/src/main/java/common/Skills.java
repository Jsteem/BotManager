package common;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.methods.Web;

@Getter
@Setter
public class Skills {
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

    public Skills(){

    }

    public static Skills build(MethodContext ctx){

            Skills skills = new Skills();

            skills.setAttack(ctx.client.getRealSkillLevel(Skill.ATTACK));
            skills.setDefence(ctx.client.getRealSkillLevel(Skill.DEFENCE));
            skills.setStrength(ctx.client.getRealSkillLevel(Skill.STRENGTH));
            skills.setHitpoints(ctx.client.getRealSkillLevel(Skill.HITPOINTS));
            skills.setRanged(ctx.client.getRealSkillLevel(Skill.RANGED));
            skills.setPrayer(ctx.client.getRealSkillLevel(Skill.PRAYER));
            skills.setMagic(ctx.client.getRealSkillLevel(Skill.MAGIC));
            skills.setCooking(ctx.client.getRealSkillLevel(Skill.COOKING));
            skills.setWoodcutting(ctx.client.getRealSkillLevel(Skill.WOODCUTTING));
            skills.setFletching(ctx.client.getRealSkillLevel(Skill.FLETCHING));
            skills.setFishing(ctx.client.getRealSkillLevel(Skill.FISHING));
            skills.setFiremaking(ctx.client.getRealSkillLevel(Skill.FIREMAKING));
            skills.setCrafting(ctx.client.getRealSkillLevel(Skill.CRAFTING));
            skills.setSmithing(ctx.client.getRealSkillLevel(Skill.SMITHING));
            skills.setMining(ctx.client.getRealSkillLevel(Skill.MINING));
            skills.setHerblore(ctx.client.getRealSkillLevel(Skill.HERBLORE));
            skills.setAgility(ctx.client.getRealSkillLevel(Skill.AGILITY));
            skills.setThieving(ctx.client.getRealSkillLevel(Skill.THIEVING));
            skills.setSlayer(ctx.client.getRealSkillLevel(Skill.SLAYER));
            skills.setFarming(ctx.client.getRealSkillLevel(Skill.FARMING));
            skills.setRunecrafting(ctx.client.getRealSkillLevel(Skill.RUNECRAFT));
            skills.setHunter(ctx.client.getRealSkillLevel(Skill.HUNTER));
            skills.setConstruction(ctx.client.getRealSkillLevel(Skill.CONSTRUCTION));

            return skills;

    }
}
