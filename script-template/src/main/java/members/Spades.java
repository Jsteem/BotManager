package members;

import dax_api.api_lib.DaxWalker;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSItem;
import net.runelite.rsb.wrappers.RSNPC;
import net.runelite.rsb.wrappers.RSTile;
import net.runelite.rsb.wrappers.RSWidget;
import util.MyBank;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Spades {
    public enum Stage {
        CHECK_RESOURCES,
        BANK,
        EXCHANGE,
        BUY_SPADES,
        HOP_WORLD,
        PICK_UP,
        DROP_NOTE
    }

    private Boolean selectedAll = false;
    private Stage stage = Stage.CHECK_RESOURCES;
    private Boolean popupShowedFirstTime = false;
    public Spades(){

    }


    public int loop(MethodContext ctx) {
        try{
            System.out.println("Stage spades: " + stage);
            switch (stage) {

                case BANK -> {
                    return doBank(ctx);
                }
                case CHECK_RESOURCES -> {
                    return checkResources(ctx);
                }
                case BUY_SPADES -> {
                    return buySpades(ctx);
                }
                case EXCHANGE -> {
                    return depositSpades(ctx);
                }
                case HOP_WORLD -> {
                    return hopWorld(ctx);
                }
                case PICK_UP -> {
                    if(!foundSpadesOnGroundAndPickUp(ctx)){
                        stage = Stage.BUY_SPADES;
                    }
                }
                case DROP_NOTE -> {
                    if(!checkInventoryForSpadesAndDrop(ctx)){
                        stage = Stage.EXCHANGE;
                    }
                }

            }
        }
        catch(Exception e ){
            e.printStackTrace();
        }


        return 1000;
    }

    public boolean checkInventoryForSpadesAndDrop(MethodContext ctx){
        if(!ctx.inventory.isOpen()){
            ctx.inventory.open();
        }




        AtomicReference<Boolean> dropped = new AtomicReference<>(false);
        Arrays.stream(ctx.inventory.getItems()).forEach(i -> {
            if(i.getName().equals("Spade") && i.getStackSize() > 1) {
                System.out.println("noted spades found");
                i.doAction("Drop");
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(i.getStackSize() > 1000){

                }

                if(!popupShowedFirstTime){
                    dropped.set(true);
                    if(ctx.interfaces.getComponent(193,0).isVisible()) {
                        popupShowedFirstTime = true;

                    }
                }
                else{
                    if(ctx.interfaces.getComponent(193,0).isVisible()){
                        ctx.interfaces.getComponent(193,0).getDynamicComponent(2).doClick();
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //put it down?
                        if(ctx.interfaces.getComponent(219,1).isVisible()){
                            ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            dropped.set(true);
                        }

                    }
                }


            }});

        return dropped.get();

    }

    public boolean foundSpadesOnGroundAndPickUp(MethodContext ctx){
        //look for existing bank notes and pick them up
        AtomicReference<Boolean> foundNoted = new AtomicReference<>(false);
        Arrays.stream(ctx.groundItems.getAll()).forEach(
                it -> {
                    if(it.getItem().getName().equals("Spade")){
                        System.out.println("found noted spades on ground");
                        foundNoted.set(true);
                        it.doAction("Take");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        return foundNoted.get();
    }

    public int hopWorld(MethodContext ctx) throws InterruptedException {
        //before hopping, make sure no items are on the ground!

        if(!foundSpadesOnGroundAndPickUp(ctx)){
            World[] worlds = ctx.client.getWorldList();
            Random r = new Random();


            int excludeMask = WorldType.toMask(EnumSet.of(
                    WorldType.PVP,
                    WorldType.PVP_ARENA,
                    WorldType.HIGH_RISK,
                    WorldType.QUEST_SPEEDRUNNING,
                    WorldType.BETA_WORLD,
                    WorldType.DEADMAN

            ));

            int includeMask =  WorldType.toMask(EnumSet.of(WorldType.MEMBERS));

            int count = (int)Arrays.stream(worlds)
                    .filter(world -> {
                        int worldMask = WorldType.toMask(world.getTypes());
                        return (worldMask & excludeMask) == 0 && (worldMask & includeMask) != 0;
                    }).count();

            Optional<World> world = Arrays.stream(worlds)
                    .filter(w -> {
                        int worldMask = WorldType.toMask(w.getTypes());
                        return (worldMask & excludeMask) == 0 && (worldMask & includeMask) != 0;
                    }).skip(r.nextInt(count)).findFirst();



            System.out.println("hopping to: " + world.get().getId());
            ctx.worldHopper.hop(world.get().getId());


            ctx.client.hopToWorld(world.get());
            Thread.sleep(1000);
            RSWidget w =  ctx.interfaces.getComponent(193,0);
            if (w != null) {
                RSWidget switchWorld = w.getDynamicComponent(3);
                if(switchWorld != null){

                    selectedAll = false;
                    System.out.println("switching!!");
                    switchWorld.doClick();

                    stage = Stage.BUY_SPADES;

                }
            }
            else{
                System.out.println("switching widget null!");
            }


        }
        return 0;
    }
    public int doBank(MethodContext ctx) throws InterruptedException {
        //close the vanessa shop
        if(ctx.interfaces.getComponent(300,1).isVisible()){
            ctx.interfaces.getComponent(300,1).getDynamicComponent(11).doClick();
        }


        popupShowedFirstTime = false;
        //make sure we dont bank if there are noted items on the ground
        if(!foundSpadesOnGroundAndPickUp(ctx)){
            if (!(ctx.players.getMyPlayer().getLocation().distanceTo(new RSTile(2809,3440)) < 3)) {
                System.out.println("not in catherby bank");


                if (ctx.players.getMyPlayer().getAnimation() == -1) {
                    DaxWalker.walkTo(new RSTile(2809,3440));
                }
            }


            if (MyBank.bank(ctx) == 0) {
                stage = Stage.CHECK_RESOURCES;
                return 0;
            }

        }

        return 0;
    }
    public int checkResources(MethodContext ctx){
        if (!ctx.bank.isOpen()) {
            stage = Stage.BANK;
            return 1000;
        }
        if(ctx.inventory.getCount() > 3 || ctx.inventory.contains("Spade")){
            ctx.bank.depositAll();
        }
        if(!ctx.inventory.contains("Coins")){
            ctx.bank.withdraw("Coins", ctx.bank.getItem("Coins").getStackSize());
        }
        if(ctx.inventory.getCount("Stamina potion(4)") != 2){
            ctx.bank.withdraw("Stamina potion(4)",2);
        }
        if(ctx.inventory.contains("Coins") && ctx.inventory.getCount("Stamina potion(4)") == 2){
            stage = Stage.BUY_SPADES;
        }



        return 1000;

    }
    public int buySpades(MethodContext ctx) throws InterruptedException {
        checkExitCondition(ctx);
        if (!(ctx.players.getMyPlayer().getLocation().distanceTo(new RSTile(2816,3462)) < 8)) {
            System.out.println("not in the shop");


            if (ctx.players.getMyPlayer().getAnimation() == -1) {
                DaxWalker.walkTo(new RSTile(2820,3461));
            }
        }
        //if the amount of spades is not equal to the max amount of spades, buy the spades
//        int countNonSpades = ctx.inventory.getCountExcept("Spade");
//        System.out.println("non spades count" + countNonSpades);
//
//        int posibbleSpadesSlots = 28 - countNonSpades;


        //close leprechaun window
        if(ctx.interfaces.getComponent(125,1).isVisible()){
            ctx.interfaces.getComponent(125,1).getDynamicComponent(11).doClick();
        }



                if(ctx.interfaces.getComponent(300,1).isVisible()){
                    RSWidget spadesWidget = ctx.interfaces.getComponent(300,16).getDynamicComponent(4);
                    if(spadesWidget.getStackSize() >= 20){
                        spadesWidget.doAction("Buy 50");

                        //close the shop
                        if(ctx.interfaces.getComponent(300,1).isVisible()){
                            ctx.interfaces.getComponent(300,1).getDynamicComponent(11).doClick();
                            stage = Stage.DROP_NOTE;
                        }

                    }
                    else{

                        //shop doesnt contain enough spades, hopping!


                        //close the shop
                        if(ctx.interfaces.getComponent(300,1).isVisible()){
                            ctx.interfaces.getComponent(300,1).getDynamicComponent(11).doClick();
                        }

                        stage = Stage.HOP_WORLD;



                    }
                }
                else{
                    RSNPC vanessa = ctx.npcs.getNearest("Vanessa");
                    if(vanessa != null){
                        vanessa.doAction("Trade");
                    }

        }

        return 100;
    }
    public int depositSpades(MethodContext ctx){
        System.out.println("Start deposit spades");

        //close the vanessa shop
        if(ctx.interfaces.getComponent(300,1).isVisible()){
            ctx.interfaces.getComponent(300,1).getDynamicComponent(11).doClick();
        }


        if(!ctx.inventory.isOpen()){
            ctx.inventory.open();
        }




            //open the leprechaun interface
            if(!ctx.interfaces.getComponent(125,0).isVisible()){
                RSNPC toolLeprechaun = ctx.npcs.getNearest("Tool Leprechaun");
                if(toolLeprechaun != null){
                    toolLeprechaun.doAction("Exchange");
                }
            }
            else{



                //stored spades: check if the storage is full, then withdraw notes and go to dropping procedure
                if(ctx.interfaces.getComponent(125,10).isVisible()){

                    //select all
                    if(!selectedAll){
                        ctx.interfaces.getComponent(125,7).doClick();
                        selectedAll = true;
                    }


                    //inventory spades
                    if( ctx.interfaces.getComponent(126,3).isVisible()){

                        int numberOfSpadesInventory = Integer.parseInt(ctx.interfaces.getComponent(126,3).getDynamicComponent(10).getText());
                        if (numberOfSpadesInventory > 0) {
                            ctx.interfaces.getComponent(126,3).doClick();
                        }
                        else{

                            stage = Stage.BUY_SPADES;
                        }

                    }

                    String numberOfStoredSpadesString = ctx.interfaces.getComponent(125,10).getDynamicComponent(10).getText();
                    String[] fraction = numberOfStoredSpadesString.split("/");
                    int numberOfStoredSpades = Integer.parseInt(fraction[0]);

                    System.out.println("number of stored spades: " + numberOfStoredSpades);


                    if(numberOfStoredSpades >= 40){
                        ctx.interfaces.getComponent(125,10).doAction("Banknotes");
                        //close leprechaun window
                        ctx.interfaces.getComponent(125,1).getDynamicComponent(11).doClick();


                        stage = Stage.PICK_UP;






                    }
                }

        }

        return 100;
    }
    public void checkExitCondition(MethodContext ctx){

        //adjust run energy
        if(ctx.walking.getEnergyPercentage() < 40){
            RSItem[] potions = ctx.inventory.getItems(
                    "Stamina potion(4)",
                    "Stamina potion(3)",
                    "Stamina potion(2)",
                    "Stamina potion(1)");

            //exit condition: stamina potions are empty or there are no stamina potions anymore
            if(potions == null || potions.length == 0){
                    System.out.println("banking!!!");
                    stage = Stage.BANK;
            }
            else{
                potions[0].doAction("Drink");

            }
        }

    }
}
