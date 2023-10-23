package To30;

import dax_api.api_lib.DaxWalker;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.rsb.methods.Equipment;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.methods.Methods;
import net.runelite.rsb.methods.NPCs;
import net.runelite.rsb.script.Random;
import net.runelite.rsb.wrappers.*;

import java.util.logging.Logger;

public class ChickenFighter {
    enum Stages{
        CHECK_REQ,
        BANK,
        OPERATE,
        GET
    }
    private Stages stage = Stages.CHECK_REQ;
    public int loop(MethodContext ctx) {

        try {
            System.out.println(stage);
            switch (stage) {
                case CHECK_REQ -> {
                    RSItem sword = ctx.equipment.getItem(EquipmentInventorySlot.WEAPON);
                    RSItem shield = ctx.equipment.getItem(EquipmentInventorySlot.SHIELD);
                    if(sword != null && sword.getName().equals("Training sword") && shield != null && shield.getName().equals("Training shield")) {
                        stage = Stages.OPERATE;
                    }
                    else{
                        ctx.inventory.open();

                        if(ctx.inventory.getItem("Training sword") != null || ctx.inventory.getItem("Training shield") != null){
                            ctx.inventory.getItem("Training sword").doClick();
                            Thread.sleep(1000);
                            ctx.inventory.getItem("Training shield").doClick();
                            return 0;
                        }
                        else{
                            System.out.println("going to GET");
                            stage = Stages.GET;
                        }
                    }
                }

                case GET -> {
                    RSPlayer p = ctx.players.getMyPlayer();
                    if(p.getLocation().distanceTo(new RSTile(3220,3238)) > 4){
                        DaxWalker.walkTo(new RSTile(3220,3283));
                    }
                    else{
                        if(ctx.inventory.getItem("Training sword") != null ||
                                ctx.equipment.getItem(EquipmentInventorySlot.WEAPON).getName().equals("Training sword")){
                            stage = Stages.CHECK_REQ;
                            return 0;
                        }

                        RSNPC tutor = ctx.npcs.getNearest("Melee combat tutor");
                        if(tutor != null){
                            tutor.doAction("Talk-to");

                            ctx.interfaces.clickContinue();

                            Thread.sleep(1000);
                            RSWidget w = ctx.interfaces.getComponent(219,1);
                            if(w != null){
                                for(int i = 1; i < 6; i++){
                                    RSWidget ww = w.getDynamicComponent(i);
                                    System.out.println(ww.getText());
                                    if(ww.getText().equals("I'd like a training sword and shield.")){
                                        ww.doClick();
                                    }

                                }

                            }
                            Thread.sleep(1000);
                            ctx.interfaces.clickContinue();

                        }



                    }
                }

                case OPERATE -> {

                    RSNPC chicken = ctx.npcs.getNearest("Chicken");
                    RSPlayer p = ctx.players.getMyPlayer();
                    RSTile chickenArea = new RSTile(3177,3297);
                    if(p.getLocation().distanceTo(chickenArea) > 10){
                        Logger.getLogger(getClass().getName()).info("Walking with dax");
                        DaxWalker.walkTo(new RSTile(3177,3297));
                    }
                    else{
                        if(ctx.client.getRealSkillLevel(Skill.STRENGTH) < 30){
                            ctx.combat.setFightMode(2);
                        }
                        else if(ctx.client.getRealSkillLevel(Skill.ATTACK) < 31){
                            ctx.combat.setFightMode(0);
                        }

                        else{
                            ctx.combat.setFightMode(3);
                        }


                        if (chicken != null && p != null) {
                            Logger.getLogger(getClass().getName()).info("Chicken not null");


                            if (!chicken.isOnScreen() || (chicken.getLocation() != null && p.getLocation().distanceTo(chicken.getLocation()) > 10)) {
                                // We're a bit far, let's walk a little closer
                                DaxWalker.walkTo(chicken.getLocation());
                                Logger.getLogger(getClass().getName()).info("Pathing to chicken");


                            } else if (!chicken.isInCombat() && !chicken.isInteractingWithLocalPlayer() && !p.isInCombat()) {
                                // We passed our checks, let's attack a chicken now
                                if (chicken.doAction("Attack")) {
                                    Logger.getLogger(getClass().getName()).info("Attack");

                                    Methods.sleep(700, 1200);
                                    if (!p.isIdle() || p.isInCombat()) {
                                        // Seems like our attack worked, we can exit
                                        return 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }  catch (NullPointerException | InterruptedException e) {
            e.printStackTrace();
        }


        return 1000;
    }
}
