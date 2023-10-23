package util;

import net.runelite.rsb.internal.globval.GlobalWidgetInfo;
import net.runelite.rsb.internal.globval.WidgetIndices;
import net.runelite.rsb.methods.Menu;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import net.runelite.rsb.wrappers.RSWidget;

import java.util.Arrays;
import java.util.Optional;

public class MyGrandExchange {

    //return the slot number (1 to 8) and returns -1 if it is not found
    public static int getSlotNumber(MethodContext ctx, String name){
        for(int childIndex = 7; childIndex < 15; childIndex++){
            RSWidget w = ctx.interfaces.getComponent(465, childIndex);

            if(name.equals(w.getDynamicComponent(19).getText()) && !getSlotTitle(ctx,(childIndex - 6)).equals("Empty")){
                System.out.println("name : " + name + " : index: " + (childIndex - 6));
                return childIndex - 6;
            }
        }
        return -1;
    }

    public static String getSlotTitle(MethodContext ctx, int slotNr){
        RSWidget w = ctx.interfaces.getComponent(465, slotNr + 6);
        return w.getDynamicComponent(16).getText();
    }

    //checks the first free slot or 0 if none are free
    public static int getFreeSlotNr(MethodContext ctx){
        for(int i = 1; i < 9; i++){
            if(getSlotTitle(ctx, i).equals("Empty") && !ctx.grandExchange.checkSlotLocked(i)){
                return i;
            }
        }

        return 0;
    }

    public static int getNumberOfFreeSlots(MethodContext ctx){
        int number = 0;
        for(int i = 1; i < 9; i++){
            if(getSlotTitle(ctx, i).equals("Empty") && !ctx.grandExchange.checkSlotLocked(i)){
                number++;
            }
        }
        return number;
    }

    //returns the slot it was sold in or 0 if no slots are free
    public static int sellItem(MethodContext ctx, int item) throws InterruptedException {
        if(getFreeSlotNr(ctx) == 0){
            System.out.println("no free slots");
            return 0;
        }
        else{
            //click on the item in the inventory
            RSItem rsItem = ctx.inventory.getItem(item);
            if(rsItem != null){
                rsItem.doClick();

                Thread.sleep(1000);

                //todo: check if were in the Set up offer window
                //confirm button
                ctx.interfaces.getComponent(465,25).getDynamicComponent(54).doClick();
            }
            else{
                System.out.println("item you want to sell not found in inventory");
                return 0;
            }


            return getFreeSlotNr(ctx);
        }
    }

    //returns the total price
    public static int buyAndCollectItem(MethodContext ctx, String item, int amount, int priceChange) throws InterruptedException {
        int freeSlot = getFreeSlotNr(ctx);
        RSWidget buyButton = ctx.interfaces.getComponent(WidgetIndices.GrandExchange.PARENT_CONTAINER,
               6 + freeSlot).getDynamicComponent(WidgetIndices.DynamicComponents.GrandExchangeSlot.BUY_ICON_SPRITE);

        if(buyButton.isValid() && buyButton.isVisible()){
            buyButton.doAction("Create");
            RSWidget chatbox = ctx.interfaces.getComponent(GlobalWidgetInfo.GRAND_EXCHANGE_SEARCH_INPUT);
            Thread.sleep(1000);

            //type in the item
            if (chatbox.isValid() && chatbox.isVisible()) {
                System.out.println("chatbox visible");
                ctx.keyboard.sendText(item, false);
                Thread.sleep(2000);
                RSWidget[] items = ctx.interfaces.getComponent(GlobalWidgetInfo.GRAND_EXCHANGE_SEARCH_DYNAMIC_CONTAINER).getComponents();
                Optional<RSWidget> widgetOptional = Arrays.stream(items).filter((x) -> Menu.stripFormatting(x.getName()).equals(item)).findFirst();
                if (widgetOptional.isPresent()) {
                    if (widgetOptional.get().doAction("Select") ) {
                        Thread.sleep(2000);
                    };
                }
            }
            else{
                System.out.println("not visible");
            }

            //adjust the settings (amount and +- price)
            if (Arrays.stream(ctx.interfaces.getComponent(GlobalWidgetInfo.GRAND_EXCHANGE_OFFER_WINDOW).getComponents())
                    .anyMatch((x) -> x.getText().equals(item))) {

                if(ctx.grandExchange.createOffer(amount, priceChange)){
                        System.out.println("collecting");
                        RSWidget w = ctx.interfaces.getComponent(465, 24);
                        //collect square product
                        w.getDynamicComponent(0).doClick();
                        Thread.sleep(500);
                        //collect square coins
                        w.getDynamicComponent(1).doClick();
                        Thread.sleep(500);
                        //go back
                        ctx.interfaces.getComponent(465, 4).doClick();
                        Thread.sleep(500);
                        ctx.interfaces.getComponent(465, 4).doClick();
                        Thread.sleep(500);
                    return 0;
                }
            }
        }
        ctx.interfaces.getComponent(465, 4).doClick();
        Thread.sleep(500);



        return 0;
    }

    public static void abortOffer(MethodContext ctx, String item) throws InterruptedException {
        int slotNr = getSlotNumber(ctx, item);
        abortSlot(ctx, slotNr);

    }



//    public static int getBuyPrice(MethodContext ctx, int slotNr){
//        RSWidget w = ctx.interfaces.getComponent(465, slotNr + 6);
//        String price = w.getDynamicComponent(25).getText();
//        String digitsOnly = price.replaceAll("\\D+", "");
//        return Integer.parseInt(digitsOnly);
//
//    }
//    public static boolean isBuying(MethodContext ctx, int slotNr) throws InterruptedException {
//        if(!ctx.grandExchange.isOpen()) {
//            ctx.grandExchange.open();
//        }
//        else{
//            if(!getSlotTitle(ctx, slotNr).equals("Buy")){
//                return false;
//            }
//            RSWidget title = ctx.interfaces.getComponent(465,2).getDynamicComponent(1);
//
//            if (!title.getText().equals("Grand Exchange: Offer status")){
//                ctx.interfaces.getComponent(465, slotNr + 6).doClick();
//                Thread.sleep(500);
//            }
//
//
//            RSWidget offerText = ctx.interfaces.getComponent(465, 23).getDynamicComponent(1);
//
//            Pattern pattern = Pattern.compile("\\d+");
//
//            // Create a matcher with the input string
//            Matcher matcher = pattern.matcher(offerText.getText());
//
//            matcher.find();
//            matcher.group();
//            matcher.find();
//            int amountBought = Integer.parseInt(matcher.group());
//
//            ctx.interfaces.getComponent(465, 4).doClick();
//            Thread.sleep(1000);
//            return amountBought > 0;
//
//            //todo: add parameter, should collect = true
//
//        }
//
//        return false;
//    }


    //return 0 if slot is empty or offer is not ready yet, else it will give the price per item
    public static int collectFromSlot(MethodContext ctx, int slotNr) throws InterruptedException {
        if(!ctx.grandExchange.isOpen()) {
            ctx.grandExchange.open();
        }
        else{
            if(getSlotTitle(ctx, slotNr).equals("Empty")){
                return 0;

            }
            RSWidget title = ctx.interfaces.getComponent(465,2).getDynamicComponent(1);

            System.out.println(title);
            if (!title.getText().equals("Grand Exchange: Offer status")){
                System.out.println("offer status not found, going to offer status now");
                ctx.interfaces.getComponent(465, slotNr + 6).doClick();
                Thread.sleep(500);

            }

            System.out.println("collecting");
            RSWidget w = ctx.interfaces.getComponent(465, 24);
            //collect square product
            w.getDynamicComponent(0).doClick();
            Thread.sleep(1000);
            //collect square coins
            w.getDynamicComponent(1).doClick();
            Thread.sleep(500);
            //go back
            ctx.interfaces.getComponent(465, 4).doClick();
            Thread.sleep(500);


//            //price per item
//            String coins = ctx.interfaces.getComponent(465, 15).getDynamicComponent(25).getText()
//                    .replaceAll("coin", "").replaceAll("s", "").trim();
//
//            return Integer.parseInt(coins);


        }
        return -1;
    }
    public static void abortSlot(MethodContext ctx, int slotNr) throws InterruptedException {
        if(!ctx.grandExchange.isOpen()) {
            ctx.grandExchange.open();
        }
        else{
            if(getSlotTitle(ctx, slotNr).equals("Empty")){
                return;
            }

            RSWidget title = ctx.interfaces.getComponent(465,2).getDynamicComponent(1);

            System.out.println(title);
            if (!title.getText().equals("Grand Exchange: Offer status")){
                ctx.interfaces.getComponent(465, slotNr + 6).doClick();
                System.out.println("offer status not found");
                Thread.sleep(500);
            }

            System.out.println("aborting");
            RSWidget w = ctx.interfaces.getComponent(465, 23);
            w.getDynamicComponent(0).doClick();
            Thread.sleep(500);

            w = ctx.interfaces.getComponent(465, 24);
            w.getDynamicComponent(0).doClick();
            Thread.sleep(500);

        }
    }
//    public static void buyItem(MethodContext ctx, String product) throws InterruptedException {
//
//
//            ctx.grandExchange.buyItem(product, 1, 3, true);
//
//            int slotnr = MyGrandExchange.getSlotNumber(ctx, product);
//            System.out.println("product: " + product + "slotnr: " + slotnr);
//            if (slotnr > 0) {
//
//                //abort it and place again with higher price change or collect
//
//                MyGrandExchange.abortSlot(ctx, slotnr);
//                Thread.sleep(3000);
//
//                MyGrandExchange.collectFromSlot(ctx, slotnr);
//
//            }
//
//
//
//    }



}
