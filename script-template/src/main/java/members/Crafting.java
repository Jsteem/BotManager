package members;

import net.runelite.api.Skill;
import net.runelite.rsb.methods.MethodContext;
import util.Processing;


public class Crafting {


    Processing processing = new Processing();
    public Crafting(){

    }
    public int loop(MethodContext ctx){
        try{

//            //1 - 20: opals 450
//            System.out.println(ctx.client.getRealSkillLevel(Skill.CRAFTING));
//            if(ctx.client.getRealSkillLevel(Skill.CRAFTING) < 20){
//                return processing.process(ctx,1755, "Uncut opal", "Opal",
//                        null, null,0, new int[]{270,14}, null);
//            }
//            else if(ctx.client.getRealSkillLevel(Skill.CRAFTING) < 27){
//                return processing.process(ctx,1755, "Uncut sapphire", "Sapphire",
//                        null, null,0, new int[]{270,14}, null);
//            }
//            else if(ctx.client.getRealSkillLevel(Skill.CRAFTING) < 34){
//                return processing.process(ctx,1755, "Uncut emerald", "Emerald",
//                        null, null,0, new int[]{270,14}, null);
//            }
//            else if(ctx.client.getRealSkillLevel(Skill.CRAFTING) < 54){
//                return processing.process(ctx,1755, "Uncut ruby", "Ruby",
//                        null, null,0, new int[]{270,14}, null);
//            }

            //20 - 27: sapphires 200

            //27 - 34 emeralds 300

            //34 - 54 rubies 17775



        }
        catch(Exception e ){
            e.printStackTrace();
        }

        return 1000;
    }

}
