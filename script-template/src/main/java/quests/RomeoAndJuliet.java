package quests;

import common.PostRequest;
import dax_api.api_lib.DaxWalker;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.*;
import util.Constants;

public class RomeoAndJuliet {
    enum Stages{
        CHECK_REQ,
        QUEST,
    }
    private Stages stage = Stages.CHECK_REQ;
    private String BERRIES = "Cadava berries";

    public int loop(MethodContext ctx)  {
        try{
            switch (stage){
                case CHECK_REQ -> {
                    if(ctx.client.getVarpValue(144) >= 50){
                        stage = Stages.QUEST;
                        return 0;
                    }


                    if(ctx.inventory.getItem(BERRIES) == null){
                        if(!Constants.GRANDEXCHANGE_AREA.contains(ctx.players.getMyPlayer().getLocation())) {
                            System.out.println("not in GE area");
                            //System.out.println()
                            if(ctx.players.getMyPlayer().getAnimation() == -1){
                                //DaxWalker.walkToBank(RunescapeBank.GRAND_EXCHANGE);
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
                            //todo: buy berries


                        }
                        else{
                            System.out.println("in GE AREA");
                            RSNPC bank = ctx.npcs.getNearest("Banker");
                            if (bank == null) {
                                System.out.println("Cannot find a bank");
                                return 1000;
                            } else {
                                System.out.println("Opening a bank");
                                //todo: after a few failed tries we need to reset the selected inventory (use item on bank wont work)
                                //bank.getModel().doClick(true);
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
                    System.out.println("QUESTING");
                    int progress = ctx.client.getVarpValue(144);
                    System.out.println(progress);
                    switch(progress){
                        case 0 -> {
                            if(!Constants.ROMEO_AREA.contains(ctx.players.getMyPlayer().getLocation())){
                                DaxWalker.walkTo(Constants.ROMEO_AREA.getCentralTile());
                            }
                            else{
                                RSNPC npc = ctx.npcs.getNearest("Romeo");
                                ctx.camera.turnTo(npc) ;

                                Thread.sleep(2000);
                                npc.doAction("Talk-to");


                                ctx.interfaces.clickContinue();
                                Thread.sleep(2000);

                                RSWidget w = ctx.interfaces.getComponent(219,1);
                                w.getDynamicComponent(3).doClick();


                                for(int i = 0; i < 15; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }

                                 w = ctx.interfaces.getComponent(219,1);
                                w.getDynamicComponent(1).doClick();

                                for(int i = 0; i < 5; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(1000);
                                }





                            }

                        }
                        case 10 -> {
                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 0){
                                System.out.println("finding stairs");
                                RSTile stairs = new RSTile(3159,3435,0);
                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 5){
                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                        DaxWalker.walkTo(stairs);
                                    }
                                }
                                else{
                                    RSObject ob = ctx.objects.getNearest("Staircase");
                                    ob.doAction("Climb-up");
                                    Thread.sleep(1000);
                                }
                            }
                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
                                RSTile juliet = new RSTile(3159,3425,0);
                                if(juliet.distanceTo(ctx.players.getMyPlayer().getLocation()) > 1){
                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                        DaxWalker.walkTo(juliet);
                                    }
                                }
                                else{
                                    RSNPC npc = ctx.npcs.getNearest("Juliet");
                                    npc.doAction("Talk-to");
                                    for(int i = 0; i < 10; i++){
                                        ctx.interfaces.clickContinue();
                                        Thread.sleep(2000);
                                    }

                                }

                            }


                        }
                        case 20 -> {
                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
                                System.out.println("finding stairs");
                                RSTile stairs = new RSTile(3155,3435,0);
                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 2){
                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                        DaxWalker.walkTo(stairs);
                                    }
                                }
                                else{
                                    RSObject ob = ctx.objects.getNearest("Staircase");
                                    ob.doAction("Climb-down");
                                    Thread.sleep(1000);
                                }
                            }
                            else{

                                if(!Constants.ROMEO_AREA.contains(ctx.players.getMyPlayer().getLocation())){
                                    DaxWalker.walkTo(Constants.ROMEO_AREA.getCentralTile());
                                }
                                else{
                                    RSNPC npc = ctx.npcs.getNearest("Romeo");
                                    ctx.camera.turnTo(npc) ;

                                    Thread.sleep(2000);
                                    npc.doAction("Talk-to");

                                    for(int i = 0; i < 40; i++){
                                        forceContinue(ctx);
                                        Thread.sleep(2000);
                                    }
                                }

                            }
                        }
                        case 30 -> {
                            RSTile lawrence = new RSTile(3255,3483,0);
                            if(lawrence.distanceTo(ctx.players.getMyPlayer().getLocation()) > 3){
                                if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                    DaxWalker.walkTo(lawrence);
                                }
                            }
                            else{
                                RSNPC npc = ctx.npcs.getNearest("Father Lawrence");
                                npc.doAction("Talk-to");
                                for(int i = 0; i < 20; i++){
                                    ctx.interfaces.clickContinue();
                                    Thread.sleep(2000);
                                }

                            }

                        }
                        case 40 -> {

                            RSTile apo = new RSTile(3194,3403,0);
                            if(apo.distanceTo(ctx.players.getMyPlayer().getLocation()) > 3){
                                if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                    DaxWalker.walkTo(apo);
                                }
                            }
                            else{
                                RSNPC npc = ctx.npcs.getNearest("Apothecary");
                                npc.doAction("Talk-to");
                                ctx.interfaces.clickContinue();
                                Thread.sleep(2000);

                                RSWidget w = ctx.interfaces.getComponent(219,1);
                                w.getDynamicComponent(2).doClick();

                                Thread.sleep(4000);

                                w = ctx.interfaces.getComponent(219,1);
                                w.getDynamicComponent(1).doClick();

                                for(int i = 0; i < 10; i++){
                                    forceContinue(ctx);
                                    Thread.sleep(1000);

                                }

                            }
                        }
                        case 50 -> {
//                            RSNPC npc = ctx.npcs.getNearest("Apothecary");
//                            npc.doAction("Talk-to");
//                            for(int i = 0; i < 4; i++){
//                                forceContinue(ctx);
//                                Thread.sleep(1000);
//
//                            }
                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 0){
                                System.out.println("finding stairs");
                                RSTile stairs = new RSTile(3159,3435,0);
                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 5){
                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                        DaxWalker.walkTo(stairs);
                                    }
                                }
                                else{
                                    RSObject ob = ctx.objects.getNearest("Staircase");
                                    ob.doAction("Climb-up");
                                    Thread.sleep(1000);
                                }
                            }
                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
                                RSTile juliet = new RSTile(3159,3425,0);
                                if(juliet.distanceTo(ctx.players.getMyPlayer().getLocation()) > 1){
                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                        DaxWalker.walkTo(juliet);
                                    }
                                }
                                else{
                                    RSNPC npc = ctx.npcs.getNearest("Juliet");
                                    npc.doAction("Talk-to");
                                    for(int i = 0; i < 15; i++){
                                        ctx.interfaces.clickContinue();
                                        Thread.sleep(1000);
                                    }
                                    forceContinue(ctx);
                                    for(int i = 0; i < 30; i++){
                                        ctx.interfaces.clickContinue();
                                        Thread.sleep(1000);
                                    }
                                }

                            }

                        }
                        case 60 -> {
                            if(ctx.players.getMyPlayer().getPosition().getPlane() == 1){
                                System.out.println("finding stairs");
                                RSTile stairs = new RSTile(3155,3435,0);
                                if(stairs.distanceTo(ctx.players.getMyPlayer().getLocation()) > 2){
                                    if(!ctx.players.getMyPlayer().isLocalPlayerMoving()){
                                        DaxWalker.walkTo(stairs);
                                    }
                                }
                                else{
                                    RSObject ob = ctx.objects.getNearest("Staircase");
                                    ob.doAction("Climb-down");
                                    Thread.sleep(1000);
                                }
                            }
                            else{

                                if(!Constants.ROMEO_AREA.contains(ctx.players.getMyPlayer().getLocation())){
                                    DaxWalker.walkTo(Constants.ROMEO_AREA.getCentralTile());
                                }
                                else{
                                    RSNPC npc = ctx.npcs.getNearest("Romeo");
                                    ctx.camera.turnTo(npc) ;

                                    Thread.sleep(2000);
                                    npc.doAction("Talk-to");

                                    for(int i = 0; i < 30; i++){
                                        ctx.interfaces.clickContinue();
                                        Thread.sleep(2000);
                                    }
                                }

                            }
                        }
                        case 100 -> {

                            PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"romeoAndJulietComplete", true});
                            request.getResult();
                        }


                    }
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
