package quests;

import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.*;
import util.Constants;


public class DruidicRitual {
    enum Stages {
        CHECK_REQ,
        QUEST,
    }

    private Stages stage = Stages.CHECK_REQ;


    public int loop(MethodContext ctx) {
        try {
            switch (stage) {
                case CHECK_REQ -> {
                    System.out.println("check req with varplayer: " + ctx.client.getVarpValue(80));
                    if ( ctx.client.getVarpValue(80) > 0) {
                        stage = Stages.QUEST;
                        return 0;
                    }




                    if (ctx.inventory.getItem("Raw chicken") == null ||
                            ctx.inventory.getItem("Raw beef") == null ||
                            ctx.inventory.getItem("Raw bear meat") == null ||
                            ctx.inventory.getItem("Raw rat meat") == null) {

                        if (!Constants.GRANDEXCHANGE_AREA.contains(ctx.players.getMyPlayer().getLocation())) {
                            System.out.println("not in GE area");

                            if (ctx.players.getMyPlayer().getAnimation() == -1) {
                                DaxWalker.walkToBank(RunescapeBank.GRAND_EXCHANGE);
                            }
                        } else if (ctx.bank.isOpen()) {

                            ctx.bank.depositAll();
                            Thread.sleep(1000);
                            RSItem gold = ctx.bank.getItem("Coins");

                            if (gold != null) {
                                System.out.println(gold.getStackSize());
                                ctx.bank.withdraw("Coins", gold.getStackSize());

                            }
                            Thread.sleep(1000);
                            ctx.bank.close();

                            Thread.sleep(1000);

                            ctx.grandExchange.open();


                        } else if (ctx.grandExchange.isOpen()) {

                            //todo, only buy the item that's not in the inventory yet

//                            MyGrandExchange.buyItem(ctx, "Raw chicken");
//
//                            Thread.sleep(1000);
//
//                            MyGrandExchange.buyItem(ctx, "Raw beef");
//
//                            Thread.sleep(1000);
//
//                            MyGrandExchange.buyItem(ctx, "Raw rat meat");
//
//                            Thread.sleep(1000);
//
//                            MyGrandExchange.buyItem(ctx, "Raw bear meat");
//
//                            Thread.sleep(1000);

                        } else {
                            System.out.println("in GE AREA");
                            RSNPC bank = ctx.npcs.getNearest("Banker");
                            if (bank == null) {
                                System.out.println("Cannot find a bank");
                                return 1000;
                            } else {
                                System.out.println("Opening a bank");
                                Thread.sleep(100);
                                bank.doAction("Bank");
                                Thread.sleep(500);


                            }
                        }

                    }
                    else {
                        //todo: deposit gold

                        stage = Stages.QUEST;
                    }
                }
                case QUEST -> {
                    System.out.println("QUESTING");
                    int progress = ctx.client.getVarpValue(80);
                    System.out.println(progress);
                    RSPlayer p = ctx.players.getMyPlayer();
                    switch (progress) {
                        case 0 -> {

                            RSTile start = new RSTile(2925, 3485);
                            if(p.getLocation().distanceTo(start) > 5){
                                if(!p.isMoving()){
                                    DaxWalker.walkTo(start);
                                }
                            }
                            else{
                                RSNPC npc = ctx.npcs.getNearest("Kaqemeex");
                                npc.doAction("Talk-to");

                                for(int i = 0; i < 5; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                //pick color for you
                                ctx.interfaces.getComponent(219,1).getDynamicComponent(2).doClick();
                                for(int i = 0; i < 5; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();
                                for(int i = 0; i < 5; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                            }
                        }
                        case 1 -> {
                            if(p.getLocation().getPlane() == 0) {
                                RSTile stairs = new RSTile(2897, 3427);
                                if(p.getLocation().distanceTo(stairs) > 5){
                                    if(!p.isMoving()){
                                        DaxWalker.walkTo(stairs);
                                        System.out.println("walking");
                                    }
                                }
                                else{
                                    RSObject st = ctx.objects.getNearest("Staircase");
                                    st.doAction("Climb-up");
                                }

                            }

                            else {
                                RSNPC npc = ctx.npcs.getNearest("Sanfew");
                                npc.doAction("Talk-to");

                                for(int i = 0; i < 1; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();


                                for(int i = 0; i < 5; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                ctx.interfaces.getComponent(219,1).getDynamicComponent(2).doClick();
                            }

                        }
                        case 2 -> {

                            if(ctx.inventory.getItem("Raw rat meat") != null ||
                                    ctx.inventory.getItem("Raw chicken") != null ||
                                    ctx.inventory.getItem("Raw beef") != null ||
                                    ctx.inventory.getItem("Raw bear meat") != null){



                                    if(p.getLocation().getPlane() == 1) {
                                        RSObject st = ctx.objects.getNearest("Staircase");
                                        st.doAction("Climb-down");

                                    }
                                    else if (p.getLocation().getPlane() == 0){
                                        if(p.getLocation().getWorldY() < 9000){
                                            //not in the dungeon
                                            RSTile stairs = new RSTile(2882, 3397);
                                            if(p.getLocation().distanceTo(stairs) > 5){
                                                if(!p.isMoving()){
                                                    DaxWalker.walkTo(stairs);
                                                    System.out.println("walking");
                                                }
                                            }
                                            else{
                                                RSObject st = ctx.objects.getNearest("Ladder");
                                                st.doAction("Climb-down");
                                            }
                                        }
                                        else{
                                            //in the dungeon
                                                RSTile ratMeat = new RSTile(2893, 9830);
                                                if(p.getLocation().distanceTo(ratMeat) > 2){
                                                    System.out.println(p.getLocation());
                                                    if(!p.isMoving()){
                                                        DaxWalker.walkTo(ratMeat);
                                                        System.out.println("walking");
                                                    }
                                                }
                                                else{
                                                    putInCauldron(ctx,"Raw rat meat");
                                                    putInCauldron(ctx,"Raw chicken");
                                                    putInCauldron(ctx,"Raw beef");
                                                    putInCauldron(ctx,"Raw bear meat" );
                                                }
                                            }
                                    }

                            }
                            else{
                                if(p.getLocation().getWorldY() < 9000){
                                    //not in the dungeon

                                    if(p.getLocation().getPlane() == 0) {
                                        RSTile stairs = new RSTile(2897, 3427);
                                        if(p.getLocation().distanceTo(stairs) > 5){
                                            if(!p.isMoving()){
                                                DaxWalker.walkTo(stairs);
                                                System.out.println("walking");
                                            }
                                        }
                                        else{
                                            RSObject st = ctx.objects.getNearest("Staircase");
                                            st.doAction("Climb-up");
                                        }

                                    }

                                    else {
                                        RSNPC npc = ctx.npcs.getNearest("Sanfew");
                                        npc.doAction("Talk-to");

                                        for(int i = 0; i < 5; i++){
                                            ctx.interfaces.clickContinue();
                                            Thread.sleep(1000);
                                        }


                                    }

                                }
                                else {
                                    //in the dungeon
                                    RSTile stairs = new RSTile(2884, 9796);
                                    if(p.getLocation().distanceTo(stairs) > 5){
                                        if(!p.isMoving()){
                                            DaxWalker.walkTo(stairs);
                                            System.out.println("walking");
                                        }
                                    }
                                    else{
                                        RSObject st = ctx.objects.getNearest("Ladder");
                                        st.doAction("Climb-up");
                                    }

                                }


                            }

                        }
                        case 3 -> {

                            if (p.getLocation().getPlane() == 1){
                                RSObject st = ctx.objects.getNearest("Staircase");
                                st.doAction("Climb-down");

                            }
                            else{
                                RSTile start = new RSTile(2925, 3485);
                                if(p.getLocation().distanceTo(start) > 5){
                                    if(!p.isMoving()){
                                        DaxWalker.walkTo(start);
                                    }
                                }
                                else {
                                    RSNPC npc = ctx.npcs.getNearest("Kaqemeex");
                                    npc.doAction("Talk-to");
                                    for(int i = 0; i < 15; i++){
                                        ctx.interfaces.clickContinue();
                                        Thread.sleep(1000);
                                    }


                                }
                            }

                        }
                        case 4 -> {
                            //todo druidicRitualComplete = true

                        }

                    }

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1000;
    }
    public void putInCauldron(MethodContext ctx, String meat){
        if(ctx.inventory.contains(meat)) {
            ctx.inventory.selectItem(meat);
            ctx.objects.getNearest("Cauldron of Thunder").doClick();
        }
    }
}