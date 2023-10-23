package util;

import To30.BankLumbridge;
import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import lombok.Getter;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import net.runelite.rsb.wrappers.RSWidget;

import java.util.Arrays;
@Getter
public class Processing {

    public enum Stage {
        PRE_PROCESSING,
        PRE_PROCESSING_TOOL,
        PROCESSING,
        CHECK_RESOURCES,
        BANK,
        BUY_RESOURCES
    }

    Stage stage;
    BankLumbridge bank;

    public Processing() {
        stage = Stage.CHECK_RESOURCES;
        bank = new BankLumbridge();
    }
    public int process(MethodContext ctx, int toolId, int preItemString, int postItemId,
                       int lowItemString, int resultString, int sleepTime,  int[] widgetPreProcess, int[] widgetProcess) throws InterruptedException {

        switch (stage) {
            case BANK -> {
                return doBank(ctx);
            }
            case CHECK_RESOURCES -> {
                return doCheckResources(ctx, toolId, preItemString, postItemId,
                        lowItemString, resultString);
            }
            case PRE_PROCESSING -> {
                return preProcess(ctx, preItemString, postItemId);
            }
            case PRE_PROCESSING_TOOL ->{
                return preProcessWithTool(ctx, toolId, preItemString, postItemId, widgetPreProcess);
            }
            case PROCESSING -> {
                return processItem(ctx, postItemId, lowItemString, sleepTime , widgetProcess);
            }
        }
        return 0;
    }

//todo: if we would use the one on one this is the check resource for that
    //            case CHECK_RESOURCES -> {
//                if (!ctx.bank.isOpen()) {
//                    stage = Stage.BANK;
//                    return 1000;
//                }
//
//                RSItem item1Item = ctx.bank.getItem(item1);
//                RSItem item2Item = ctx.bank.getItem(item2);
//
//                if (item1Item == null || item2Item == null) {
//                    ctx.bank.depositAll();
//                    System.out.println("Not enough resources!");
//                    //stage = FletchingStage.BUY_RESOURCES;
//                    return 1000;
//                }
//
//
//                ctx.bank.withdraw(item1, item1Item.getStackSize());
//                ctx.bank.withdraw(item2, item2Item.getStackSize());
//
//                if (ctx.inventory.getCount(item1) == 1 && ctx.inventory.getCount(item2) == 1) {
//                    ctx.bank.close();
//                    stage = Stage.READY_TO_MAKE;
//                    System.out.println("going to ready to make");
//                    return 0;
//                }
//
//            }
    public int processOneOnOne(MethodContext ctx, String item1, String item2, int[] widget) throws InterruptedException {
        System.out.println("making");


        if (ctx.inventory.getItem(item1) == null || ctx.inventory.getItem(item2) == null) {
            System.out.println("done");
            stage = Stage.BANK;
            return 0;
        } else {
            System.out.println("click use item ");
            ctx.inventory.useItem(item1, item2);
            Thread.sleep(1000);
            ctx.interfaces.getComponent(widget[0], widget[1]).doClick();
        }

        return 5000;

    }

    public int doBank(MethodContext ctx) throws InterruptedException {
        if (!Constants.GRANDEXCHANGE_AREA.contains(ctx.players.getMyPlayer().getLocation())) {
            System.out.println("not in GE area");

            if (ctx.players.getMyPlayer().getPosition().getPlane() == 2 || ctx.players.getMyPlayer().getPosition().getPlane() == 1) {
                bank.fromLumbridgeBank(ctx);
            }


            if (ctx.players.getMyPlayer().getAnimation() == -1) {
                DaxWalker.walkToBank(RunescapeBank.GRAND_EXCHANGE);
            }
        }


        if (MyBank.bank(ctx) == 0) {
            stage = Stage.CHECK_RESOURCES;
            return 0;
        }
        return 0;
    }

    public int doCheckResources(MethodContext ctx, int toolId, int preItemString, int postItemId,
                                int lowItemString, int resultString) throws InterruptedException {

        if(ctx.grandExchange.isOpen()){
            ctx.grandExchange.close();
        }

        RSWidget collectionBox = ctx.interfaces.getComponent(402,2);
        if(collectionBox != null && collectionBox.isVisible()){
            collectionBox.getDynamicComponent(11).doClick();
        }

        if (!ctx.bank.isOpen()) {
            stage = Stage.BANK;
            return 1000;
        }

        //check if the preItem is present
        RSItem preItem = ctx.bank.getItem(preItemString);
        if (preItem != null) {
            if (toolId != -1) {
                if (ctx.inventory.getCount(toolId) == 1 && ctx.inventory.getCount(preItemString) > 0) {
                    ctx.bank.close();
                    stage = Stage.PRE_PROCESSING_TOOL;
                    return 0;
                } else {

                    if (ctx.inventory.getItem(postItemId) != null) {
                        System.out.println("deposit all except tool");
                        ctx.inventory.getItem(postItemId).doClick();
                        Thread.sleep(1000);
                    }
                    if (ctx.inventory.getCount() > 1) {
                        ctx.bank.depositAll();
                        Thread.sleep(500);
                    }

                    if (ctx.inventory.getCount(toolId) == 0) {
                        RSItem toolItem = ctx.bank.getItem(toolId);
                        if(toolItem != null){
                            toolItem.doClick();
                            Thread.sleep(500);
                        }


                    }
                    if (ctx.inventory.getCount(preItemString) == 0) {
                        preItem.doClick();

                    }

                }

            } else {
                //note: cleaning the herbs dont require a tool
                if (ctx.inventory.getCount(postItemId) > 0) {
                    ctx.inventory.getItem(postItemId).doClick();
                }


                ctx.bank.withdraw(preItemString, 28);


                if (ctx.inventory.getCount(preItemString) > 0) {
                    ctx.bank.close();
                    stage = Stage.PRE_PROCESSING;
                    System.out.println("start pre processing");
                    return 0;
                }
                stage = Stage.PRE_PROCESSING;
            }

            return 1000;
        }

        //if not check if the postItem + lowItem are present
        if(postItemId == -1 || lowItemString == -1){

            return 1000;
        }

        RSItem postItem = ctx.bank.getItem(postItemId);
        RSItem lowItem = ctx.bank.getItem(lowItemString);

        if (postItem != null && lowItem != null) {
            //todo: if there are three ingredients the max amount changes, but then the function would have a third item?
            int amountToMake = 0;
            amountToMake = Math.min(ctx.bank.getCount(postItem.getID()), ctx.bank.getCount(lowItem.getID()));
            amountToMake = Math.min(amountToMake, 14);
            System.out.println("making x amount of items: " + amountToMake);


            if (ctx.inventory.getCount(resultString) > 0) {
                System.out.println("Banking items");
                ctx.bank.depositAll();
                Thread.sleep(400);
            }

            if (ctx.inventory.getCount(postItemId) < amountToMake) {
                Thread.sleep(200);
                ctx.bank.withdraw(postItem.getID(), amountToMake - ctx.inventory.getCount(postItemId));
            }
            if (ctx.inventory.getCount(lowItemString) < amountToMake) {
                Thread.sleep(200);
                ctx.bank.withdraw(lowItem.getID(), amountToMake - ctx.inventory.getCount(lowItemString));
            }
            if (ctx.inventory.getCount(lowItemString) == amountToMake && ctx.inventory.getCount(postItemId) == amountToMake) {
                ctx.bank.close();
                stage = Stage.PROCESSING;
                System.out.println("going to processing");
                return 0;
            } else {
                ctx.bank.depositAll();
            }
            return 1000;
        }


        return 1000;
    }


    public int preProcessWithTool(MethodContext ctx, int tool, int item, int result, int[] widget) throws InterruptedException {

        if (ctx.bank.isOpen()) {
            ctx.bank.close();
        }


        if(ctx.grandExchange.isOpen()){
            ctx.grandExchange.close();
        }
        if(!ctx.inventory.isOpen()){
            ctx.inventory.open();
        }

        System.out.println("making");
        if (ctx.inventory.getCount(tool) == 0) {
            stage = Stage.BANK;
        }


        if (ctx.inventory.getCount(item) == 0) {
            System.out.println("done");
            stage = Stage.BANK;
            return 0;
        }

        if (ctx.inventory.getCount(item) > 0) {

            if (ctx.players.getMyPlayer().getAnimation() == -1) {
                System.out.println("click use item ");
                ctx.inventory.getItem(tool).doClick();


                ctx.inventory.getItem(item).doClick();
                Thread.sleep(700);
                ctx.interfaces.getComponent(widget[0], widget[1]).doClick();
            }

        }

        return 2000;


    }

    public int processItem(MethodContext ctx, int item1, int item2, int sleepTime, int[] widget) throws InterruptedException {

        System.out.println("processing");
        if (ctx.bank.isOpen()) {
            ctx.bank.close();
        }
        if(ctx.grandExchange.isOpen()){
            ctx.grandExchange.close();
        }

        if (ctx.inventory.getCount(item1) == 0 || ctx.inventory.getCount(item2) == 0) {
            System.out.println("done");
            stage = Stage.BANK;
            return 0;
        }


        //make sure there are no bow string over (watch out (u) and normal bows have the same id?)
        if (ctx.inventory.getCount(item2) > 0 ) {
            if (ctx.players.getMyPlayer().getAnimation() == -1) {
                RSWidget w = ctx.interfaces.getComponent(widget[0], widget[1]);
                if (w.isVisible()) {
                    System.out.println("visible thus clicking");
                    w.doClick();
                    return sleepTime;
                }


                ctx.inventory.useItem(item1, item2);




            }


        }

        return 1000;
    }

    public int preProcess(MethodContext ctx, int item1, int result) throws InterruptedException {
        if (ctx.bank.isOpen()) {
            ctx.bank.close();
        }
        System.out.println("making");
        if(!ctx.inventory.isOpen()){
            ctx.inventory.open();
        }



        if (ctx.inventory.getItem(item1) == null) {
            System.out.println("done");
            stage = Stage.BANK;
            return 0;
        } else {
            //ctx.inventory.getItem(item1).doClick();

            Arrays.stream(ctx.inventory.getItems()).forEach(i -> {
                if (i.getName().equals(item1)) {
                    i.doClick();

                }
            });
        }

        return 0;


    }
}
//todo: first open bank drop everything from inventory inside, get every item thats relevant out of the bank,
//todo:then open GE collect everything, then calculate what we need, make offers, collect, wait and sleep for 10 mins or try other bank method
//todo: call the api and ask for other script, also buy resources already for the other money makers!
   // public int buyResources(MethodContext ctx, String item1, String item2, String result) throws InterruptedException {
   //     return -1;
//        System.out.println("buying");
//        if (ctx.bank.isOpen()) {
//            ctx.bank.depositAll();
//
//            RSItem gold = ctx.bank.getItem("Coins");
//            RSItem r = ctx.bank.getItem(result);
//            RSItem i1 = ctx.bank.getItem(item1);
//            RSItem i2 = ctx.bank.getItem(item2);
//
//            if(gold != null){
//                System.out.println(gold.getStackSize());
//                ctx.bank.withdraw("Coins", gold.getStackSize());
//                Thread.sleep(4000);
//
//            }
//            ctx.bank.setWithdrawModeToNote();
//            Thread.sleep(2000);
//            if(r != null){
//                ctx.bank.withdraw(result, r.getStackSize());
//            }
//            if(i1 != null){
//                ctx.bank.withdraw(item1, i1.getStackSize());
//            }
//            if(i2 != null){
//                ctx.bank.withdraw(item2, i2.getStackSize());
//            }
//
//            ctx.bank.close();
//            Thread.sleep(1000);
//            ctx.grandExchange.open();
//            return 2000;
//        }
        //  if (ctx.grandExchange.isOpen()) {

        //    System.out.println("free slot: " + MyGrandExchange.getFreeSlotNr(ctx));
        //System.out.println(MyGrandExchange.sellItem(ctx, "Yew longbow"));


        //System.out.println( MyGrandExchange.buyAndCollectItem(ctx, "Ranarr weed", 1));


//            if(resultItem != null){
//                MyGrandExchange.sellItem(ctx, resultItem);
//                Thread.sleep(6000);
//
//            }
//
//            int price1 = 0;
//            int price2 = 0;
//
//            int totalAmountToBuy = (gold.getStackSize() / (price1 + price2));
//
//            System.out.println("I can buy this many items: " + totalAmountToBuy);
//
//            //todo: algoritme houdt rekening met inventory om hoeveelheid te berekenen

//            ctx.grandExchange.buyItem(item1, 10, true);
//            Thread.sleep(5000);
//            MyGrandExchange.collectFromSlot(ctx, slot1);
//            Thread.sleep(2000);
//
//            ctx.grandExchange.buyItem(item2, 10, true);
//            Thread.sleep(5000);
//            MyGrandExchange.collectFromSlot(ctx, slot2);
//            Thread.sleep(2000);
//            stage = FletchingStage.CHECK_RESOURCES;
//            ctx.grandExchange.close();
        //return 1000;


//        if (!ctx.bank.isOpen() && !ctx.grandExchange.isOpen()) {
//            stage = FletchingStage.BANK;
//        }

    //   return 1000;
    //   }