package To30;

import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.*;

public class BankLumbridge {

    public int toLumbridgeBank(MethodContext ctx) throws InterruptedException {
        RSPlayer p = ctx.players.getMyPlayer();
        RSArea area = new RSArea(new RSTile(3206, 3211),
                new RSTile(3208, 3209));

        if(p.getLocation().getPlane() == 0){

            if(DaxWalker.walkTo(area.getRandomTile())) {
                RSObject o = ctx.objects.getNearest("Staircase");
                ctx.camera.turnTo(o);
                o.doAction("Climb-up");

                return 1000;
            }

        }
        else if(p.getLocation().getPlane() == 1){
            RSObject o = ctx.objects.getNearest("Staircase");
            o.doAction("Climb-up");
            return 2000;
        }
        else if(p.getLocation().getPlane() == 2){
            if(!p.isLocalPlayerMoving()){
                if(DaxWalker.walkToBank(RunescapeBank.LUMBRIDGE_TOP)){


                }
                return 2000;
            }
        }
        return 2000;
    }
    public int fromLumbridgeBank(MethodContext ctx){
        RSPlayer p = ctx.players.getMyPlayer();
        RSArea area = new RSArea(new RSTile(3206, 3211),
                new RSTile(3208, 3209));


        //todo: dont use dax on the plane == 1, just find the stairs, it gets stuck (or maby try distance < 5) ?
        System.out.print(p.getLocation().getPlane());
        if(p.getLocation().getPlane() == 2 || p.getLocation().getPlane() == 1){
            if(!p.isLocalPlayerMoving()){
                if(DaxWalker.walkTo(area.getCentralTile())){
                    RSObject o = ctx.objects.getNearest("Staircase");
                    ctx.camera.turnTo(o);
                    o.doAction("Climb-down");
                }
            }
        }
        return 2000;

    }

}
