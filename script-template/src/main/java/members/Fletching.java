package members;

import net.runelite.api.Skill;
import net.runelite.rsb.methods.MethodContext;
import util.Processing;
import util.ProcessingExtra;


public class Fletching {

    int KNIFE = 946;


    Processing processing = new Processing();
    ProcessingExtra processingExtra = new ProcessingExtra();

    public Fletching(){

    }
    public int loop(MethodContext ctx){
        try{

            //Logs 600
            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 5){
                return processing.process(ctx, KNIFE, 1511, 52,  -1,
                        -1, 15000, new int[]{270,14},  null);
            }

            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 10){
                return processing.process(ctx, KNIFE, 1511, 50,  -1,
                        -1, 15000, new int[]{270,16},  null);
            }

            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 20){
                return processing.process(ctx, KNIFE, 1511, 48,  -1,
                        -1, 15000, new int[]{270,17},  null);
            }

            //oak logs 800
            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 25){
                return processing.process(ctx, KNIFE, 1521, 54,  -1,
                        -1, 15000, new int[]{270,15},  null);
            }

            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 35){
                return processing.process(ctx, KNIFE, 1521, 56,  -1,
                        -1, 15000, new int[]{270,16},  null);
            }

            //Willow 2100 logs
            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 40){
                return processing.process(ctx, KNIFE, 1519, 60,  -1,
                        -1, 15000, new int[]{270,15},  null);
            }

            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 50){
                return processing.process(ctx, KNIFE, 1519, 58,  -1,
                        -1, 15000, new int[]{270,16},  null);
            }

            //Maple 6200 logs
            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 55){
                return processing.process(ctx, KNIFE, 1517, 64,  -1,
                        -1, 15000, new int[]{270,15},  null);
            }

            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 65){
                return processing.process(ctx, KNIFE, 1517, 62,  -1,
                        -1, 15000, new int[]{270,16},  null);
            }

            //4300 yew logs to longbow on lvl 70
            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 70){
                return processing.process(ctx, KNIFE, 1515, 68,  1777,
                        857, 15000, new int[]{270,15},  new int[]{270,14});

            }
            else{
                return processingExtra.process(ctx, KNIFE, 1515, 66,  1777,
                        855, new int[]{270,16},  new int[]{270,14});
            }

//            if(ctx.client.getRealSkillLevel(Skill.FLETCHING) < 85){
//
//            }

            //note: magic longbows are only 114 when yews are 138
//            else{
//                return processing.process(ctx, KNIFE, 1513, 70, 1777,
//                        859, 15000, new int[]{270, 16}, new int[]{270, 14});
//
//            }
        }
        catch(Exception e ){
            e.printStackTrace();
        }

        return 1000;
    }


}