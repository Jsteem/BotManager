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
public class ProcessingExtra {

    public enum Stage {
        PRE_PROCESSING_TOOL,
        PROCESSING,
        CHECK_RESOURCES,
        BANK,
        BUY_RESOURCES
    }

    Stage stage;
    BankLumbridge bank;

    public ProcessingExtra() {
        stage = Stage.CHECK_RESOURCES;
        bank = new BankLumbridge();
    }

    public int process(MethodContext ctx, int toolId, int preItemId, int postItemId,
                       int lowItemId, int resultItemId, int[] widgetPreProcess, int[] widgetProcess) throws InterruptedException {

        switch (stage) {
            case BANK -> {
                return doBank(ctx);
            }
            case CHECK_RESOURCES -> {
                return doCheckResources(ctx, toolId, preItemId, postItemId,
                        lowItemId, resultItemId);
            }
            case PRE_PROCESSING_TOOL ->{
                return processItem(ctx, toolId, preItemId, widgetPreProcess);
            }
            case PROCESSING -> {
                return processItem(ctx, postItemId, lowItemId, widgetProcess);
            }

            case BUY_RESOURCES -> {
                return buyResources(ctx, preItemId, lowItemId, resultItemId);
            }
        }
        return 0;
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

    public int doCheckResources(MethodContext ctx, int toolId, int preItemId, int postItemId,
                                int lowItemId, int resultItemId) throws InterruptedException {

        if (ctx.grandExchange.isOpen()) {
            ctx.grandExchange.close();
        }

        RSWidget collectionBox = ctx.interfaces.getComponent(402, 2);
        if (collectionBox != null && collectionBox.isVisible()) {
            collectionBox.getDynamicComponent(11).doClick();
        }

        if (!ctx.bank.isOpen()) {
            stage = Stage.BANK;
            return 1000;
        }

        //check if the preItem is present
        RSItem preItem = ctx.bank.getItem(preItemId);
        RSItem toolItem = ctx.bank.getItem(toolId);
        if(toolItem == null){
            toolItem = ctx.inventory.getItem(toolId);
        }
        if (preItem != null && toolItem != null) {
            System.out.println("logs found!");
            if (ctx.inventory.getCount(toolId) == 0) {
                if (toolItem != null) {
                    toolItem.doClick();
                    WaitUntil.wait( () -> ctx.inventory.getCount(toolId) == 1, 2000);
                }
            }
            if(ctx.inventory.getItem(toolId) != null && ctx.inventory.getItem(postItemId) != null){
                ctx.inventory.getItem(postItemId).doClick();
            }


            if (ctx.inventory.getCount(preItemId) == 0 && ctx.inventory.getItem(toolId) != null) {
                preItem.doClick();
                WaitUntil.wait(() -> ctx.inventory.getCount(preItemId) > 0, 2000);
            }

            if (ctx.inventory.getCount(toolId) == 1 && ctx.inventory.getCount(preItemId) > 0) {
                ctx.bank.close();
                stage = Stage.PRE_PROCESSING_TOOL;
                return 0;
            }
            else{
                ctx.bank.depositAll();
            }

            return 0;

        }

        //if not check if the postItem + lowItem are present
        else if (postItemId != -1 && lowItemId != -1) {

            RSItem postItem = ctx.bank.getItem(postItemId);
            RSItem lowItem = ctx.bank.getItem(lowItemId);

            if (postItem != null && lowItem != null) {

                int amountToMake = Math.min(ctx.bank.getCount(postItemId), ctx.bank.getCount(lowItemId));
                amountToMake = Math.min(amountToMake, 14);
                System.out.println("making x amount of items: " + amountToMake);


                if (ctx.inventory.getItem(resultItemId) != null) {
                    System.out.println("Banking items");
                    ctx.bank.depositAll();
                    Thread.sleep(400);
                }

                if (ctx.inventory.getCount(postItemId) < amountToMake) {
                    Thread.sleep(200);
                    ctx.bank.withdraw(postItemId, amountToMake - ctx.inventory.getCount(postItemId));
                }
                if (ctx.inventory.getCount(lowItemId) < amountToMake) {
                    Thread.sleep(200);
                    ctx.bank.withdraw(lowItemId, amountToMake - ctx.inventory.getCount(lowItemId));
                }
                if (ctx.inventory.getCount(lowItemId) == amountToMake && ctx.inventory.getCount(postItemId) == amountToMake) {
                    ctx.bank.close();
                    stage = Stage.PROCESSING;
                    System.out.println("going to processing");
                    return 0;
                } else {
                    ctx.bank.depositAll();


                }
                return 1000;
            }
            else{
                ctx.bank.depositAll();
                Thread.sleep(1000);
                preItem  = ctx.bank.getItem(preItemId);
                lowItem = ctx.bank.getItem(lowItemId);
                if(preItem == null || lowItem == null){
                    stage = Stage.BUY_RESOURCES;
                }
            }


        }


        return 1000;
    }

    public int processItem(MethodContext ctx, int item1, int item2, int[] widget) throws InterruptedException {

        System.out.println("processing");
        if (ctx.bank.isOpen()) {
            ctx.bank.close();
        }

        if (ctx.grandExchange.isOpen()) {
            ctx.grandExchange.close();
        }

        if(!ctx.inventory.isOpen()){
            ctx.inventory.open();
        }

        if (ctx.inventory.getCount(item1) == 0 || ctx.inventory.getCount(item2) == 0) {
            System.out.println("done");
            stage = Stage.BANK;
            return 0;
        }



        if (ctx.inventory.getCount(item2) > 0) {

            if (ctx.players.getMyPlayer().getAnimation() == -1) {
                System.out.println("click use item ");
                ctx.inventory.getItem(item1).doClick();
                Thread.sleep(500);
                ctx.inventory.getItem(item2).doClick();

                WaitUntil.clickWidget(ctx, widget[0], widget[1], 2000);


                WaitUntil.wait(() -> ctx.inventory.getCount(item2) > 0, 20000);
            }

        }


        return 1000;
    }

    public int buyResources(MethodContext ctx, int preItemId, int lowItemId, int resultItemId) throws InterruptedException {
        System.out.println("buying");
        if(!ctx.bank.isOpen() && !ctx.grandExchange.isOpen()){
            ctx.bank.open();
        }
        if (ctx.bank.isOpen()) {
            ctx.bank.depositAll();

            RSItem gold = ctx.bank.getItem(995);
            RSItem resultItem = ctx.bank.getItem(resultItemId);

            if(gold != null){
                gold.doClick();
                Thread.sleep(1000);

            }
            ctx.bank.setWithdrawModeToNote();
            if(resultItem != null){
                resultItem.doClick();
                Thread.sleep(2000);
            }


            ctx.bank.close();
            Thread.sleep(1000);
            ctx.grandExchange.open();
            return 2000;
        }
        else if(ctx.grandExchange.isOpen()){
            int slot = MyGrandExchange.getSlotNumber(ctx, "Yew longbow");
            if(slot == -1){
                if(ctx.inventory.getItem(resultItemId + 1) != null){
                    MyGrandExchange.sellItem(ctx, resultItemId + 1);
                }
            }
            else{
                MyGrandExchange.collectFromSlot(ctx, slot);
            }


            if(ctx.inventory.getItem(preItemId + 1) == null){
                 slot = MyGrandExchange.getSlotNumber(ctx, "Yew logs");
                if(slot == -1){
                    System.out.println("buying yew logs");
                    MyGrandExchange.buyAndCollectItem(ctx, "Yew logs", 1000, 0);
                }
                else{
                    MyGrandExchange.collectFromSlot(ctx, slot);
                }
            }
            else if(ctx.inventory.getItem(lowItemId + 1) == null){
                 slot = MyGrandExchange.getSlotNumber(ctx, "Bow string");
                if(slot == -1){
                    System.out.println("buying bow strings");
                    MyGrandExchange.buyAndCollectItem(ctx, "Bow string", 1000, 1);
                }
                else{
                    MyGrandExchange.collectFromSlot(ctx, slot);
                }
            }
            else{

                stage = Stage.CHECK_RESOURCES;
            }

        }


        return 1000;
    }

}