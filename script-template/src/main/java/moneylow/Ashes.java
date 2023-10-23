package moneylow;


import net.runelite.rsb.methods.Bank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import net.runelite.rsb.wrappers.RSNPC;
import net.runelite.rsb.wrappers.RSObject;
import net.runelite.rsb.wrappers.RSTile;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class Ashes {
    enum Stages{
        BANK,
        CHECK_RESOURCES,
        WALK_TO_START,
        LIGHT_FIRE,
        COLLECT_ASHES;
    }

    Stages stage;
    int amount;
    int startX;
    int startY;
    public Ashes(){
        stage = Stages.COLLECT_ASHES;
        amount = 0;
        startX = 3188;
        startY = 3432;
    }

    public int loop(MethodContext ctx) {
        try{
            switch(this.stage){
                case BANK -> {
                    ctx.walking.walkTo(new RSTile(3185,3436, 0));
                    Thread.sleep(1000);
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

                    RSItem log;
                    try {
                        log = ctx.bank.getItem("Willow logs");
                    } catch (Exception e) {
                        System.out.println("Not enough resources!");
                        return 1000;
                    }

                    if (ctx.inventory.getCount("Ashes") > 0) {
                        System.out.println("Banking items");
                        ctx.bank.depositAll();
                        Thread.sleep(400);
                    }

                    if (ctx.inventory.getCount("Tinderbox") < 1) {
                        Thread.sleep(200);
                        ctx.bank.withdraw("Tinderbox", 1);
                    }

                    if (ctx.inventory.getCount("Willow logs") < 1) {
                        Thread.sleep(200);
                        ctx.bank.withdraw("Willow logs", 27);
                    }
                    if (ctx.inventory.getCount("Willow logs") > 0 && ctx.inventory.getCount("Tinderbox") > 0) {
                        ctx.bank.close();
                        this.stage = Stages.WALK_TO_START;
                        System.out.println("ready to start");
                        return 0;
                    }


                }
                case WALK_TO_START -> {
                    ctx.walking.walkTo(new RSTile(startX,startY, 0));
                    Thread.sleep(1000);
                    if(ctx.players.getMyPlayer().getLocation().equals(new RSTile(startX,startY, 0))){
                        this.stage = Stages.LIGHT_FIRE;
                        return 0;
                    }

                }
                case LIGHT_FIRE -> {

                    ctx.inventory.useItem("Tinderbox", "Willow logs");
                    amount++;
                    System.out.println(amount);
                    if(amount >= 10){
                        amount = 0;
                        startY--;
                        Thread.sleep(2000);
                        ctx.walking.walkTo(new RSTile(startX,startY, 0));
                        Thread.sleep(3000);
                    }
                    if(ctx.inventory.getCount("Willow logs") == 0){
                        this.stage = Stages.COLLECT_ASHES;
                        startX = 3188;
                        startY = 3432;

                        return 0;
                    }

                    return 1400;
                }
                case COLLECT_ASHES -> {

                    System.out.println("Collecting Ashes");

                    AtomicReference<Boolean> objectFound = new AtomicReference<>(false);


                    //todo: specify a distance since it will detect fires 10 tiles away!
                    RSObject fire = ctx.objects.getNearest("Fire");
                    if(fire != null){

                        System.out.println("Fire found" + fire.getLocation());
                        ctx.walking.walkTo(fire.getLocation());
                        objectFound.set(true);
                    }


                    Arrays.stream(ctx.groundItems.getAll()).forEach(
                            it -> {
                                if(it.getItem().getName().equals("Ashes")){
                                    objectFound.set(true);
                                    it.doAction("Take");
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                    if(objectFound.get() == false || ctx.inventory.getCount() == 28){
                        this.stage = Stages.BANK;

                    }
                }
            }


        }
        catch(Exception e){

        }

        return 1000;
    }
}
