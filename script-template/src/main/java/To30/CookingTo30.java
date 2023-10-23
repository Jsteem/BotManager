package To30;

import net.runelite.api.Skill;
import net.runelite.rsb.methods.Bank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import net.runelite.rsb.wrappers.RSNPC;
import net.runelite.rsb.wrappers.RSObject;
import net.runelite.rsb.wrappers.RSTile;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class CookingTo30 {
    enum Stages{
        BANK,
        CHECK_RESOURCES,
        COOK
    }

    Stages stage;
    int amount;
    int startX;
    int startY;

    public CookingTo30(){

        stage = Stages.BANK;
        amount = 0;
        startX = 3188;
        startY = 3432;
    }

    public int loop(MethodContext ctx) {
        try{
            String fish =  "Raw shrimps";
            String cookedFish = "shrimps";
            if(ctx.skills.getRealLevel(Skill.COOKING) >= 15){
                fish = "Raw trout";
                cookedFish = "Trout";
            }

            switch(this.stage){
                case BANK -> {
                    //todo: sometimes gets lost if the bank is near, run to the bank with webwalker
                    RSNPC bank = ctx.npcs.getNearest("Banker");
                    if (bank == null) {
                        System.out.println("Cannot find a bank");
                        return 1000;
                    } else {
                        System.out.println("Opening a bank");
                        bank.getModel().doClick(true);
                        Thread.sleep(100);
                        bank.doAction("Bank");
                        Thread.sleep(500);
                        stage = Stages.CHECK_RESOURCES;
                    }
                }

                case CHECK_RESOURCES -> {
                    if (!ctx.bank.isOpen()) {
                        this.stage = Stages.BANK;
                        return 1000;
                    }

                    RSItem item;
                    try {
                        item = ctx.bank.getItem(fish);
                    } catch (Exception e) {
                        System.out.println("Not enough resources!");
                        return 1000;
                    }

                    if (ctx.inventory.getCount(fish) != 28) {
                        System.out.println("Banking items");
                        ctx.bank.depositAll();
                        Thread.sleep(400);
                    }

                    if (ctx.inventory.getCount(fish) < 28) {
                        Thread.sleep(200);
                        ctx.bank.withdraw(fish, 28);
                    }

                    if (ctx.inventory.getCount(fish) == 28) {
                        ctx.bank.close();
                        this.stage = Stages.COOK;
                        System.out.println("ready to start");
                        return 0;
                    }


                }
                case COOK -> {


                    //todo: if fire not found in reasonable range, then make one

                    if (ctx.inventory.getCount(fish) > 0) {
                        RSObject fire = ctx.objects.getNearest("Fire");
                        if(fire != null){
                            System.out.println("Fire found" + fire.getLocation());
                            ctx.walking.walkTo(fire.getLocation());
                            if(ctx.players.getMyPlayer().getAnimation() == -1){
                                ctx.inventory.selectItem(fish);
                                fire.doAction("Use");
                                Thread.sleep(1000);
                                ctx.interfaces.getComponent(270,14).doClick();
                            }


                            return 1000;

                        }
                    }
                    if(ctx.inventory.getCount(fish) == 0){
                        this.stage = Stages.BANK;

                        return 0;
                    }


                }
            }


        }
        catch(Exception e){

        }

        return 1000;
    }
}
