package To30;

import dax_api.api_lib.DaxWalker;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.*;

import java.util.Arrays;

public class FishingAndMiningTo30 {
    java.util.Random random;
    public FishingAndMiningTo30(){
        random = new java.util.Random();
    }

    enum Stages{
        CHECK_REQ,
        BANK,
        OPERATE
    }
    private Stages stage = Stages.CHECK_REQ;
    private String FISHING_NET = "Small fishing net";
    private String METHOD_NET = "Net";
    private String PICKAXE = "Bronze pickaxe";


    RSTile area = new RSTile(3237,3149);

    private String method = METHOD_NET;

    private BankLumbridge bank = new BankLumbridge();

    public int loop(MethodContext ctx) {
        //todo: from level 20 we go to the ge and buy feathers + fishing rod -> trout and salmon in barb village
        try {
            RSPlayer p = ctx.players.getMyPlayer();

            System.out.println("Stage: " + stage);
            switch (stage) {
                case CHECK_REQ -> {

                    if(p.getLocation().distanceTo(area) < 20){
                        //only bank if were far away from the spot, else we might have items that are allowed,
                        //we shouldnt go back to the bank however if we have shrimps or ore in the inventory
                        stage = Stages.OPERATE;
                    }
                    else if(ctx.inventory.getItems().length == 2 &&
                            ctx.inventory.contains(FISHING_NET) &&
                            ctx.inventory.contains(PICKAXE)){
                        stage = Stages.OPERATE;

                    }
                    else{
                        System.out.println("should bank");
                        stage = Stages.BANK;
                    }

                    return 0;
                }
                case BANK -> {
                    if(!(p.getLocation().getPlane() == 2)){
                        bank.toLumbridgeBank(ctx);
                    }
                    else{
                        if(!ctx.bank.isOpen()) {
                            ctx.objects.getNearest("Bank booth").doClick();
                        }

                        //note: bank tutorial
                        RSWidget w = ctx.interfaces.getComponent(664,29);
                        if(w.isVisible()){
                            w.doClick();
                        }
                        Thread.sleep(500);

                        //note: want more bank space?
                        ctx.bank.open();
                        w = ctx.interfaces.getComponent(289,7);
                        if(w.isVisible()){
                            w.doClick();
                        }

                        Thread.sleep(500);
                        ctx.bank.depositAll();
                        Thread.sleep(500);
                        ctx.bank.withdraw(FISHING_NET, 1);
                        Thread.sleep(500);
                        ctx.bank.withdraw(PICKAXE, 1);

                        ctx.bank.close();

                        stage = Stages.CHECK_REQ;

                        return 0;
                    }
                }

                case OPERATE -> {
                    if((p.getLocation().getPlane() == 2) || p.getLocation().getPlane() == 1){
                        return bank.fromLumbridgeBank(ctx);
                    }


                        if(ctx.client.getRealSkillLevel(Skill.FISHING) < 30){
                            area  = new RSTile(3243,3150);
                            RSNPC fishingSpot = ctx.npcs.getNearest("Fishing spot");


                            if(p.getAnimation() == -1) {
                                System.out.println("clicking entity");
                                if (fishingSpot != null) {
                                    ctx.camera.turnTo(fishingSpot);
                                    fishingSpot.doClick();

                                }
                            }
                        }
                        else{
                            area  = new RSTile(3229,3146);
                            RSObject miningRock = ctx.objects.getNearest("Copper rocks");



                            if(p.getAnimation() == -1) {
                                System.out.println("clicking entity");
                                if (miningRock != null) {

                                    miningRock.doAction("Mine");

                                }
                            }
                        }



                        if(ctx.inventory.getCount() == 28){
                            if(!ctx.inventory.isOpen()){
                                ctx.inventory.open();
                            }
                            ctx.inventory.dropAllExcept(FISHING_NET, PICKAXE);
                        }

                    if(!p.isLocalPlayerMoving() && p.getLocation().distanceTo(area) > 10){
                        DaxWalker.walkTo(area);
                        System.out.println("walking to area");
                    }

                }

            }

            return 5000;




        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}