package util;

import common.PostRequest;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import net.runelite.rsb.wrappers.RSNPC;
import net.runelite.rsb.wrappers.RSWidget;


public class MyBank {

    public static int bank(MethodContext ctx)   throws InterruptedException{
        RSNPC bank = ctx.npcs.getNearest("Banker");
        if (bank == null) {
            System.out.println("Cannot find a bank");
            return 1000;
        } else {
            if(!ctx.bank.isOpen()){
                System.out.println("Opening a bank");
                if(ctx.inventory.isItemSelected()){
                    bank.doClick();
                }

                Thread.sleep(100);
                bank.doAction("Bank");
                Thread.sleep(500);



            }
            else{
                //todo: this isnt a very accurate representation, gold can be in the GE
                sendPostData(ctx);
            }


        }
        return 0;
    }
    public static double parseBankAmount(String amount) {
        String valueString = amount.replaceAll("[^0-9.]", "");
        double value = Double.parseDouble(valueString);
        double multiplier = getMultiplier(amount);
        return value * multiplier;
    }

    private static double getMultiplier(String substring) {
        if (substring.contains("K")) {
            return 1_000.0;
        } else if (substring.contains("M")) {
            return 1_000_000.0;
        } else if (substring.contains("B")) {
            return 1_000_000_000.0;
        } else {
            return 1.0; // Default multiplier if substring is not recognized
        }
    }
    public static String formatBankAmount(double value) {
        String formattedValue = "";

        if (value >= 1_000_000_000.0) {
            value /= 1_000_000_000.0;
            formattedValue = String.format("%.1fB", value);
        } else if (value >= 1_000_000.0) {
            value /= 1_000_000.0;
            formattedValue = String.format("%.1fM", value);
        } else if (value >= 1_000.0) {
            value /= 1_000.0;
            formattedValue = String.format("%.1fK", value);
        } else {
            formattedValue = Double.toString(value);
        }

        System.out.println(formattedValue   );
        return formattedValue;
    }

    public static void sendPostData(MethodContext ctx){
        RSWidget w = ctx.interfaces.getComponent(12,3);

        if(w != null){
            int amount = (int) parseBankAmount(w.getText());


            PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"totalValueBank", amount});
            request.getResult();




            //todo: there is a bug when there are no coins in the bank, work with ids instead?
            RSItem gold = ctx.bank.getItem(995);
            RSItem goldInventory = ctx.inventory.getItem(995);
            int cashStack = 0;
            if(gold != null){
                cashStack = gold.getStackSize();

            }
            if(goldInventory != null){
                cashStack += goldInventory.getStackSize();
            }

            request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"cashStack", cashStack});
            request.getResult();

            String totalValueBankString = formatBankAmount(amount);
            if(totalValueBankString != null && !totalValueBankString.equals("")){
                request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"totalValueBankString",totalValueBankString});

                System.out.println(  "result: " +     request.getResult());
            }
            String cashStackString = formatBankAmount(cashStack);
            if(cashStackString != null && !cashStackString.equals("")) {
                request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"cashStackString", cashStackString});
                request.getResult();
            }

        }

    }

}


