package members;

import net.runelite.api.Skill;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSArea;
import net.runelite.rsb.wrappers.RSObject;
import net.runelite.rsb.wrappers.RSPlayer;
import net.runelite.rsb.wrappers.RSTile;
import util.Processing;


public class Agility {

//    RSArea one = new RSArea(2488, 3436, 2472,3439);
//    RSArea two =new RSArea(2476, 3422, 2470,3424);
//    RSArea three =new RSArea(2488, 3419, 2483,3425);
//    RSArea four = new RSArea(2488, 3427, 2483,3431);


    RSTile one = new RSTile(2480, 3437);
    RSTile two =new RSTile(2472, 3420);
    RSTile three =new RSTile(2486, 3420);
    RSTile four = new RSTile(2485, 3429);


    public Agility(){

    }
    public int loop(MethodContext ctx){
        try{
            RSPlayer p = ctx.players.getMyPlayer();
            RSTile t = p.getLocation();

            if(t.distanceTo(one) < 6){
                System.out.println("one contains");
                RSObject o = ctx.objects.getNearest("Log balance");
                if(o != null){
                    o.doClick();
                }
            }

            if(t.distanceTo(two) < 10){
                System.out.println("two contains");
                if(t.getPlane() == 0){
                    RSObject o = ctx.objects.getNearest("Obstacle net");
                    if(o != null){
                        o.doClick();
                    }
                }
                else if(t.getPlane() == 1){
                    RSObject o = ctx.objects.getNearest("Tree branch");
                    if(o != null){
                        o.doClick();
                    }
                }
                else if(t.getPlane() == 2){
                    RSObject o = ctx.objects.getNearest("Balancing rope");
                    if(o != null){
                        o.doClick();
                    }
                }

            }
            if(t.distanceTo(three) < 6){
                System.out.println("three contains");
                if(t.getPlane() == 2){
                    RSObject o = ctx.objects.getNearest("Tree branch");
                    if(o != null){
                        o.doClick();
                    }
                }
                else if(t.getPlane() == 0){
                    RSObject o = ctx.objects.getNearest("Obstacle net");
                    if(o != null){
                        o.doClick();
                    }
                }
            }
            if( t.distanceTo(four) < 6){
                System.out.println("four contains");
                RSObject o = ctx.objects.getNearest("Obstacle pipe");
                if(o != null){
                    o.doClick();
                }
            }



        }
        catch(Exception e ){
            e.printStackTrace();
        }

        return 1000;
    }

}
