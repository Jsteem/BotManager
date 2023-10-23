package quests;

import common.PostRequest;
import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.*;
import util.Constants;

public class GoblinDiplomacy {
    enum Stages{
        CHECK_REQ,
        QUEST,
    }
    private Stages stage = Stages.CHECK_REQ;


    public int loop(MethodContext ctx)  {
        try{
            switch (stage){
                case CHECK_REQ -> {
                    System.out.println("check req with varbit: " + ctx.client.getVarbitValue(2378));
                    if(ctx.client.getVarbitValue(2378) > 0){
                        stage = Stages.QUEST;
                        return 0;
                    }



                    if(ctx.inventory.getItems("Blue dye", "Red dye", "Yellow dye").length != 3){
                        if(!Constants.GRANDEXCHANGE_AREA.contains(ctx.players.getMyPlayer().getLocation())) {
                            System.out.println("not in GE area");

                            if(ctx.players.getMyPlayer().getAnimation() == -1){
                                DaxWalker.walkToBank(RunescapeBank.GRAND_EXCHANGE);
                            }
                        }
                        else if(ctx.bank.isOpen()){

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


                        }
                        else if(ctx.grandExchange.isOpen()){
                            //todo: handle if the BERRIES are not buying

                            //todo, only buy the item that's not in the inventory yet
//                            ctx.grandExchange.buyItem(BERRIES, 1, true);
//                            Thread.sleep(1000);
//                            MyGrandExchange.collectFromSlot(ctx, MyGrandExchange.getSlotNumber(ctx, BERRIES));
//                            Thread.sleep(1000);
//                            ctx.grandExchange.close();

                        }
                        else{
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
                    else{
                        stage = Stages.QUEST;
                    }
                }
                case QUEST -> {
                    //DaxWalker.walkTo(new RSTile(2955, 3497));

                    System.out.println("QUESTING");
                    int progress = ctx.client.getVarbitValue(2378);
                    System.out.println(progress);
                    switch(progress) {
                        case 0 -> {
                            int mailCount = ctx.inventory.getCount("Goblin mail");

                            if (mailCount == 0) {
                                RSTile mail1 = new RSTile(2955, 3497);
                                //todo: climb up stairs
                            } else if (mailCount == 1) {
                                RSTile mail2 = new RSTile(2952, 3508);
                            } else if (mailCount == 2) {
                                RSTile mail3 = new RSTile(2960, 3514);
                                RSObject create = ctx.objects.getNearest("Crate");
                                create.doAction("Search");
                            } else if (mailCount == 3) {
                                //start quest

                                //todo: use blue on mail, combine red and yellow and use on second, leave third plain!

                                RSTile general = new RSTile(2957, 3511);

                                RSNPC npc = ctx.npcs.getNearest("General Wartface");
                                ctx.camera.turnTo(npc) ;
                                Thread.sleep(2000);
                                npc.doAction("Talk-to");

                                for(int i = 0; i < 12; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                //pick color for you
                                ctx.interfaces.getComponent(219,1).getDynamicComponent(3).doClick();

                                Thread.sleep(1000);

                                for(int i = 0; i < 3; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }
                                //different color
                                ctx.interfaces.getComponent(219,1).getDynamicComponent(3).doClick();

                                for(int i = 0; i < 10; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                //yes (start the quest)
                                ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();

                                for(int i = 0; i < 10; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }


//                                ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();

                            }


                        }
                        case 3 ->{
                            if(ctx.inventory.getItem("Blue goblin mail") == null){
                                ctx.inventory.useItem("Blue dye", "Goblin mail");
                                Thread.sleep(700);
                            }
                            if(ctx.inventory.getItem("Orange dye") == null){
                                ctx.inventory.useItem("Yellow dye", "Red dye");
                                Thread.sleep(700);
                            }
                            if(ctx.inventory.getItem("Orange goblin mail") == null){
                                ctx.inventory.useItem("Orange dye", "Goblin mail");
                                Thread.sleep(700);
                            }

                            RSNPC npc = ctx.npcs.getNearest("General Wartface");
                            ctx.camera.turnTo(npc) ;
                            Thread.sleep(2000);
                            npc.doAction("Talk-to");

                            for(int i = 0; i < 10; i++){
                                ctx.interfaces.clickContinue();
                                Thread.sleep(1000);
                            }

                            //pick color for you
                            ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();


                            Thread.sleep(700);
                            for(int i = 0; i < 30; i++){
                                ctx.interfaces.clickContinue();
                                Thread.sleep(1000);

                            }
                            Thread.sleep(1000);

                        }
                        case 4 -> {
                            RSNPC npc = ctx.npcs.getNearest("General Wartface");
                            ctx.camera.turnTo(npc) ;
                            Thread.sleep(2000);
                            npc.doAction("Talk-to");

                            for(int i = 0; i < 10; i++){
                                ctx.interfaces.clickContinue();
                                Thread.sleep(1000);
                            }

                            //pick color for you
                            ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();


                            Thread.sleep(700);
                            for(int i = 0; i < 30; i++){
                                ctx.interfaces.clickContinue();
                                Thread.sleep(1000);

                            }
                            Thread.sleep(1000);
                        }
                        case 5 -> {
                            RSNPC npc = ctx.npcs.getNearest("General Wartface");
                            ctx.camera.turnTo(npc) ;
                            Thread.sleep(2000);
                            npc.doAction("Talk-to");

                            for(int i = 0; i < 14; i++){
                                ctx.interfaces.clickContinue();
                                Thread.sleep(1000);
                            }

                            //pick color for you
                            ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();


                            Thread.sleep(700);
                            for(int i = 0; i < 30; i++){
                                ctx.interfaces.clickContinue();
                                Thread.sleep(1000);

                            }
                            Thread.sleep(1000);
                        }
                        case 6 -> {


                            PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"goblinDiplomacyComplete", true});
                            request.getResult();
                        }

                    }





//                            if(!Constants.ROMEO_AREA.contains(ctx.players.getMyPlayer().getLocation())){
//                                DaxWalker.walkTo(Constants.ROMEO_AREA.getCentralTile());
//                            }
//                            else{
//                                RSNPC npc = ctx.npcs.getNearest("Romeo");
//                                ctx.camera.turnTo(npc) ;
//
//                                Thread.sleep(2000);
//                                npc.doAction("Talk-to");
//
//
//                                ctx.interfaces.clickContinue();
//                                Thread.sleep(2000);
//
//                                RSWidget w = ctx.interfaces.getComponent(219,1);
//                                w.getDynamicComponent(3).doClick();
//
//
//                                for(int i = 0; i < 15; i++){
//                                    ctx.interfaces.clickContinue();
//                                    Thread.sleep(1000);
//                                }
//
//                                w = ctx.interfaces.getComponent(219,1);
//                                w.getDynamicComponent(1).doClick();
//
//                                for(int i = 0; i < 5; i++){
//                                    ctx.interfaces.clickContinue();
//                                    Thread.sleep(1000);
//                                }
//
//                            }
//
//                        }
//                        case 10 -> {
//                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 0){
//                                System.out.println("finding stairs");
//                                RSTile stairs = new RSTile(3159,3435,0);
//                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 5){
//                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                        DaxWalker.walkTo(stairs);
//                                    }
//                                }
//                                else{
//                                    RSObject ob = ctx.objects.getNearest("Staircase");
//                                    ob.doAction("Climb-up");
//                                    Thread.sleep(1000);
//                                }
//                            }
//                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
//                                RSTile juliet = new RSTile(3159,3425,0);
//                                if(juliet.distanceTo(ctx.players.getMyPlayer().getLocation()) > 1){
//                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                        DaxWalker.walkTo(juliet);
//                                    }
//                                }
//                                else{
//                                    RSNPC npc = ctx.npcs.getNearest("Juliet");
//                                    npc.doAction("Talk-to");
//                                    for(int i = 0; i < 10; i++){
//                                        ctx.interfaces.clickContinue();
//                                        Thread.sleep(2000);
//                                    }
//
//                                }
//
//                            }
//
//
//                        }
//                        case 20 -> {
//                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
//                                System.out.println("finding stairs");
//                                RSTile stairs = new RSTile(3155,3435,0);
//                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 2){
//                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                        DaxWalker.walkTo(stairs);
//                                    }
//                                }
//                                else{
//                                    RSObject ob = ctx.objects.getNearest("Staircase");
//                                    ob.doAction("Climb-down");
//                                    Thread.sleep(1000);
//                                }
//                            }
//                            else{
//
//                                if(!Constants.ROMEO_AREA.contains(ctx.players.getMyPlayer().getLocation())){
//                                    DaxWalker.walkTo(Constants.ROMEO_AREA.getCentralTile());
//                                }
//                                else{
//                                    RSNPC npc = ctx.npcs.getNearest("Romeo");
//                                    ctx.camera.turnTo(npc) ;
//
//                                    Thread.sleep(2000);
//                                    npc.doAction("Talk-to");
//
//                                    for(int i = 0; i < 40; i++){
//                                        forceContinue(ctx);
//                                        Thread.sleep(2000);
//                                    }
//                                }
//
//                            }
//                        }
//                        case 30 -> {
//                            RSTile lawrence = new RSTile(3255,3483,0);
//                            if(lawrence.distanceTo(ctx.players.getMyPlayer().getLocation()) > 3){
//                                if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                    DaxWalker.walkTo(lawrence);
//                                }
//                            }
//                            else{
//                                RSNPC npc = ctx.npcs.getNearest("Father Lawrence");
//                                npc.doAction("Talk-to");
//                                for(int i = 0; i < 20; i++){
//                                    ctx.interfaces.clickContinue();
//                                    Thread.sleep(2000);
//                                }
//
//                            }
//
//                        }
//                        case 40 -> {
//
//                            RSTile apo = new RSTile(3194,3403,0);
//                            if(apo.distanceTo(ctx.players.getMyPlayer().getLocation()) > 3){
//                                if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                    DaxWalker.walkTo(apo);
//                                }
//                            }
//                            else{
//                                RSNPC npc = ctx.npcs.getNearest("Apothecary");
//                                npc.doAction("Talk-to");
//                                ctx.interfaces.clickContinue();
//                                Thread.sleep(2000);
//
//                                RSWidget w = ctx.interfaces.getComponent(219,1);
//                                w.getDynamicComponent(2).doClick();
//
//                                Thread.sleep(4000);
//
//                                w = ctx.interfaces.getComponent(219,1);
//                                w.getDynamicComponent(1).doClick();
//
//                                for(int i = 0; i < 10; i++){
//                                    forceContinue(ctx);
//                                    Thread.sleep(1000);
//
//                                }
//
//                            }
//                        }
//                        case 50 -> {
////                            RSNPC npc = ctx.npcs.getNearest("Apothecary");
////                            npc.doAction("Talk-to");
////                            for(int i = 0; i < 4; i++){
////                                forceContinue(ctx);
////                                Thread.sleep(1000);
////
////                            }
//                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 0){
//                                System.out.println("finding stairs");
//                                RSTile stairs = new RSTile(3159,3435,0);
//                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 5){
//                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                        DaxWalker.walkTo(stairs);
//                                    }
//                                }
//                                else{
//                                    RSObject ob = ctx.objects.getNearest("Staircase");
//                                    ob.doAction("Climb-up");
//                                    Thread.sleep(1000);
//                                }
//                            }
//                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
//                                RSTile juliet = new RSTile(3159,3425,0);
//                                if(juliet.distanceTo(ctx.players.getMyPlayer().getLocation()) > 1){
//                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                        DaxWalker.walkTo(juliet);
//                                    }
//                                }
//                                else{
//                                    RSNPC npc = ctx.npcs.getNearest("Juliet");
//                                    npc.doAction("Talk-to");
//                                    for(int i = 0; i < 15; i++){
//                                        ctx.interfaces.clickContinue();
//                                        Thread.sleep(1000);
//                                    }
//                                    forceContinue(ctx);
//                                    for(int i = 0; i < 30; i++){
//                                        ctx.interfaces.clickContinue();
//                                        Thread.sleep(1000);
//                                    }
//                                }
//
//                            }
//
//                        }
//                        case 60 -> {
//                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
//                                System.out.println("finding stairs");
//                                RSTile stairs = new RSTile(3155,3435,0);
//                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 2){
//                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
//                                        DaxWalker.walkTo(stairs);
//                                    }
//                                }
//                                else{
//                                    RSObject ob = ctx.objects.getNearest("Staircase");
//                                    ob.doAction("Climb-down");
//                                    Thread.sleep(1000);
//                                }
//                            }
//                            else{
//
//                                if(!Constants.ROMEO_AREA.contains(ctx.players.getMyPlayer().getLocation())){
//                                    DaxWalker.walkTo(Constants.ROMEO_AREA.getCentralTile());
//                                }
//                                else{
//                                    RSNPC npc = ctx.npcs.getNearest("Romeo");
//                                    ctx.camera.turnTo(npc) ;
//
//                                    Thread.sleep(2000);
//                                    npc.doAction("Talk-to");
//
//                                    for(int i = 0; i < 30; i++){
//                                        ctx.interfaces.clickContinue();
//                                        Thread.sleep(2000);
//                                    }
//                                }
//
//                            }
//                        }
//                        case 100 -> {
//                            //todo: api - quest done set the boolean
//                        }
//
//
//                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }





        return 1000;
    }

    public void forceContinue(MethodContext ctx){
        ctx.mouse.click(250,443, true);
    }

}
