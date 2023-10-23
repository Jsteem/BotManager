package moneylow;

import To30.BankLumbridge;
import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import util.Constants;
import util.MyBank;


public class PieDish {
    enum MakeDoughStages{
        READY_TO_MAKE,
        CHECK_RESOURCES,
        BANK,
        BUY_RESOURCES
    }
    String BUCKET_NAME = "Bucket of water";
    String POT_NAME = "Pot of flour";
    String DOUGH_NAME = "Pastry dough";
    String DISH_NAME = "Pie dish";
    String SHELL_NAME = "Pie shell";
    String APPLE_NAME = "Cooking apple";
    String APPLE_PIE = "Uncooked apple pie";
    int amountToMake = 0;
    int tries = 0;
    MakeDoughStages makeDoughStage;

    BankLumbridge bank = new BankLumbridge();
    public PieDish(){
        this.makeDoughStage = MakeDoughStages.BANK;
    }
    public int loop(MethodContext ctx){
        try{
            //RSWidget MoneyBank = ctx.interfaces.getComponent(12,3);
            //System.out.println(MoneyBank.getText());


            //return makeProduct(ctx, BUCKET_NAME, POT_NAME, DOUGH_NAME, 9, new int[]{270,15});

            //return makeProduct(ctx, DOUGH_NAME, DISH_NAME, SHELL_NAME, 14, new int[]{270,14});
            return makeProduct(ctx, SHELL_NAME, APPLE_NAME, APPLE_PIE, 14, new int[]{270,14});

        }
        catch(Exception e ){
            e.printStackTrace();
        }

        return 1000;
    }
    public int makeProduct(MethodContext ctx, String item1, String item2, String result, int maxAmount, int[] widget) throws InterruptedException {
        switch (this.makeDoughStage) {
            case BUY_RESOURCES -> {

                //todo: work in progress
                return -1;

            }
            case BANK -> {

                if(!Constants.GRANDEXCHANGE_AREA.contains(ctx.players.getMyPlayer().getLocation())) {
                    System.out.println("not in GE area");

                    if(ctx.players.getMyPlayer().getPosition().getPlane() == 2 || ctx.players.getMyPlayer().getPosition().getPlane() == 1){
                        bank.fromLumbridgeBank(ctx);
                    }



                    if(ctx.players.getMyPlayer().getAnimation() == -1){
                        DaxWalker.walkToBank(RunescapeBank.GRAND_EXCHANGE);
                    }
                }


                if(MyBank.bank(ctx) == 0){
                    makeDoughStage = MakeDoughStages.CHECK_RESOURCES;
                    return 0;
                }
            }
            case CHECK_RESOURCES -> {
                if (!ctx.bank.isOpen()) {
                    makeDoughStage = MakeDoughStages.BANK;
                    return 1000;
                }

                RSItem bucket;
                RSItem pot;
                bucket = ctx.bank.getItem(item1);
                pot = ctx.bank.getItem(item2);
                if(bucket == null || pot == null){
                    System.out.println("Not enough resources!");
                    this.makeDoughStage = MakeDoughStages.BUY_RESOURCES;
                    return 0;
                }

                amountToMake = Math.min(ctx.bank.getCount(bucket.getID()), ctx.bank.getCount(pot.getID()));
                amountToMake = Math.min(amountToMake, maxAmount);
                System.out.println("making x amount of items: " + amountToMake);


                if (amountToMake == 0) {
                        this.makeDoughStage = MakeDoughStages.BUY_RESOURCES;
                        System.out.println("not enough resources");
                        return 0;

                }
                if (ctx.inventory.getCount(result) > 0) {
                    System.out.println("Banking items");
                    ctx.bank.depositAll();
                    Thread.sleep(400);
                }

                if (ctx.inventory.getCount(item1) < amountToMake) {
                    Thread.sleep(200);
                    ctx.bank.withdraw(item1, amountToMake - ctx.inventory.getCount(item1));
                }
                if (ctx.inventory.getCount(item2) < amountToMake) {
                    Thread.sleep(200);
                    ctx.bank.withdraw(item2, amountToMake - ctx.inventory.getCount(item2));
                }
                if (ctx.inventory.getCount(item1) == amountToMake && ctx.inventory.getCount(item2) == amountToMake) {
                    ctx.bank.close();
                    makeDoughStage = MakeDoughStages.READY_TO_MAKE;
                    System.out.println("going to ready to make");
                    return 0;
                }
                else{
                    ctx.bank.depositAll();
                }
                return 1000;

            }
            case READY_TO_MAKE -> {
                System.out.println("making");


                if (ctx.inventory.getCount(item1) == 0 || ctx.inventory.getCount(item2) == 0) {
                    System.out.println("done");
                    makeDoughStage = MakeDoughStages.BANK;
                    return 0;
                }

                if(ctx.inventory.getCount(result) == 0){
                    tries++;
                    System.out.println("click use item ");
                    ctx.inventory.useItem(item1, item2);
                    Thread.sleep(700);
                    ctx.interfaces.getComponent(widget[0], widget[1]).doClick();
                    if(tries == 3 && ctx.inventory.getCount(result) == 0){
                        makeDoughStage = MakeDoughStages.BANK;
                        tries = 0;
                        return 0;
                    }
                    //todo: this value is variable and depends on the product we make
                    return 12000;
                }
                else{
                    tries = 0;
                }
                return 2000;


            }
        }
        return 5000;
    }


}
