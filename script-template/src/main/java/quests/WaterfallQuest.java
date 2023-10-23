package quests;

import common.GetRequest;
import common.PostRequest;
import common.Skills;
import dax_api.api_lib.DaxWalker;
import net.runelite.api.widgets.Widget;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.script.Random;
import net.runelite.rsb.wrappers.*;

import java.util.Arrays;


public class WaterfallQuest {

    public WaterfallQuest(){

    }
    private RSTile almeraArea = new RSTile(2521,3495,0);
    RSTile ropeArea = new RSTile(2512,3481);
    RSTile stairCase = new RSTile(2518,3431);


    private RSTile raftArea = new RSTile(2510,3494,0);
    public int loop(MethodContext ctx) {
        try {

            int progress = ctx.client.getVarpValue(65);
            System.out.println(progress);
            switch(progress) {
                case 0 -> {
                    if(!ctx.players.getMyPlayer().isMoving() &&
                            (ctx.players.getMyPlayer().getLocation().distanceTo(almeraArea) > 100)){
                        ctx.inventory.getItem("Games necklace").doAction("Rub");
                        ctx.interfaces.getComponent(219,1).getDynamicComponent(2).doClick();
                    }
                     else if(!ctx.players.getMyPlayer().isMoving() &&
                            (ctx.players.getMyPlayer().getLocation().distanceTo(almeraArea) > 5)){
                        DaxWalker.walkTo(almeraArea);
                    }
                     else{
                         talkToNpc(ctx, "Almera", 5, 0);

                        ctx.interfaces.getComponent(229,2).doClick();
                        Thread.sleep(500);

                        ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();
                        for(int i = 0; i< 5; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(1000);
                        }

                    }

                }
                case 1 ->{

                    if( ctx.players.getMyPlayer().getLocation().distanceTo(raftArea) < 2){
                        RSObject o = ctx.objects.getNearest("Log raft");
                        if(o != null){
                            o.doClick();
                        }
                    }
                    else if(ctx.players.getMyPlayer().getLocation().distanceTo(ropeArea) < 2){
                        talkToNpc(ctx, "Hudon", 10, 0);

                    }
                    else if(!ctx.players.getMyPlayer().isMoving() &&
                            (ctx.players.getMyPlayer().getLocation().distanceTo(raftArea) > 2)){
                        DaxWalker.walkTo(raftArea);
                    }
                }
                case 2 -> {

                    if(ctx.players.getMyPlayer().getLocation().distanceTo(ropeArea) < 5){
                        RSObject o = ctx.objects.getNearest("Rock");
                        if(o != null){
                            ctx.camera.turnTo(o);
                            o.doClick();
                        }
                    }
                    else if(!ctx.players.getMyPlayer().isMoving() &&
                            (ctx.players.getMyPlayer().getLocation().distanceTo(stairCase) > 10)){
                            DaxWalker.walkTo(stairCase);
                    }
                    else if(ctx.players.getMyPlayer().getLocation().getPlane() == 0){

                        RSObject o = ctx.objects.getNearest("Staircase");
                        if(o != null){
                            ctx.camera.turnTo(o);
                            o.doClick();
                        }
                    }
                    else if(!ctx.inventory.contains("Book on baxtorian")){

                            RSObject o = ctx.objects.getNearest(1989);
                            if(o != null){
                                ctx.camera.turnTo(o);
                                o.doClick();
                            }


                    }
                    else{
                        ctx.inventory.getItem("Book on baxtorian").doClick();
                    }


                }
                case 3 -> {
                    if(ctx.interfaces.getComponent(392,7) != null){
                        ctx.interfaces.getComponent(392,7).doClick();
                    }

                    if(ctx.players.getMyPlayer().getLocation().getPlane() == 1) {
                        RSObject o = ctx.objects.getNearest("Staircase");
                        if(o != null){
                            ctx.camera.turnTo(o);
                            o.doClick();
                        }
                    }
                    else if(ctx.players.getMyPlayer().getLocation().getWorldY() < 9000 && !ctx.players.getMyPlayer().isMoving() &&
                            (ctx.players.getMyPlayer().getLocation().distanceTo(new RSTile(2535,3156)) > 1)){
                        DaxWalker.walkTo(new RSTile(2535,3156));

                    }
                    else if(ctx.players.getMyPlayer().getLocation().getWorldY() > 9000){

                        if(!ctx.inventory.contains("Key") && !ctx.inventory.contains("Glarial's pebble")){
                            if(    !ctx.players.getMyPlayer().isMoving() &&
                                    (ctx.players.getMyPlayer().getLocation().distanceTo(new RSTile(2548,9566)) > 5)) {
                                System.out.println("walking to crate");
                                DaxWalker.walkTo(new RSTile(2548,9566));
                            }

                            else{
                                RSObject crate = ctx.objects.getNearest(1990);
                                if(crate != null){
                                    crate.doClick();
                                }
                            }

                        }
                        else if(ctx.inventory.contains("Key") && ctx.inventory.contains("Glarial's pebble")){
                            if(    !ctx.players.getMyPlayer().isMoving() &&
                                    (ctx.players.getMyPlayer().getLocation().distanceTo(new RSTile(2514,9581)) > 5)) {
                                System.out.println("walking to glory");
                                DaxWalker.walkTo(new RSTile(2514,9581));
                                System.out.println("test");
                            }
                            else{
                                talkToNpc(ctx, "Golrie", 10, 0);
                            }


                        }
                        else{
                            //only pebble left
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

    public void talkToNpc(MethodContext ctx, String npcString, int times, int dialogOption) throws InterruptedException {
        RSNPC npc = ctx.npcs.getNearest(npcString);

        if(npc == null){
            return;
        }
        npc.doAction("Talk-to");
        Thread.sleep(1000);


        for(int i = 0; i< times; i++){
            ctx.interfaces.clickContinue();
            Thread.sleep(1000);
        }
        RSWidget w = ctx.interfaces.getComponent(193,0);
        if(dialogOption > 0 && w!= null){
            w.getDynamicComponent(dialogOption).doClick();
            Thread.sleep(2000);
        }


    }







}
