package members;

import net.runelite.rsb.methods.MethodContext;
import util.Processing;

public class Herblore {


    Processing processing = new Processing();
    public Herblore(){

    }
    public int loop(MethodContext ctx){
        try{

            //1 - 25 Grimy guam leaf 3200 guam
            //return cleanHerbs(ctx, "Grimy guam leaf", "Guam leaf");

            //25 - 35 Grimy rannar weed
            //return processing.clean(ctx, "Grimy ranarr weed", "Ranarr weed");

            //35 -> make rannar pots
//            return processing.process(ctx, -1, "Grimy ranarr weed",
//                    "Ranarr weed", "Vial of water", "Ranarr potion (unf)",
//                    8000,null, new int[]{270,14}  );


        }
        catch(Exception e ){
            e.printStackTrace();
        }

        return 1000;
    }

}
