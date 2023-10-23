package To30;

import net.runelite.api.Skill;
import net.runelite.rsb.methods.Bank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.script.Random;
import net.runelite.rsb.wrappers.RSObject;

import java.util.Arrays;
import java.util.RandomAccess;
import java.util.concurrent.atomic.AtomicBoolean;

public class WoodcutterTo30 {
    java.util.Random random;
    public WoodcutterTo30(){
        random = new java.util.Random();
    }

    public int loop(MethodContext ctx) {
        try {
            //todo: dax check if were at the lumbridge bank area
//            if(ctx.inventory.getItem("Tinderbox") == null || ctx.inventory.getItems().length == 28){
//                Bank.NearestBank b = ctx.bank.getNearest();
//                ctx.walking.walkTo(b.entityBank.getLocation());
//                Thread.sleep(1000);
//                ctx.bank.open();
//                Thread.sleep(500);
//                ctx.bank.depositAll();
//                Thread.sleep(200);
//                ctx.bank.withdraw("Tinderbox", 1);
//                ctx.bank.close();
//
//            }

            System.out.println("skil " + ctx.skills.getRealLevel(Skill.WOODCUTTING));

            String name = "Tree";
            if( ctx.skills.getRealLevel(Skill.WOODCUTTING) >= 15){
                name = "Oak";
            }
            RSObject tree = ctx.objects.getNearest(name);
            System.out.println("Looking for tree");


            if(ctx.inventory.getItems().length == 28){
                ctx.inventory.open();
                Arrays.stream(ctx.inventory.getItems()).forEach(i -> {
                    if(i.getName().contains("logs") || i.getName().contains("Logs")){
                        i.doAction("Drop");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else{
                if(tree != null && ctx.players.getMyPlayer().getAnimation() == -1){
                    if(tree.isOnScreen()){
                        tree.doAction("Chop down");

                    }



                    return 5000;
                }
            }



            return 1000;




        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}