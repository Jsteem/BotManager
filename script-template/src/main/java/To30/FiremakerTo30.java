package To30;
import common.PostRequest;
import common.Skills;
import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import net.runelite.api.World;
import net.runelite.rsb.methods.Bank;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSPlayer;
import net.runelite.rsb.wrappers.RSWidget;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class FiremakerTo30 {

    private BankLumbridge bank = new BankLumbridge();

    public Stages stage = Stages.FIRE;
    enum Stages{
        BANK,
        FIRE,
    }


    public int loop(MethodContext ctx) {
        try {


            (ctx.runeLite.getLoader()).setVisible(true);
            switch(stage){
                case BANK -> {
                    RSPlayer p = ctx.players.getMyPlayer();
                    if(!(p.getLocation().getPlane() == 2)){
                        return bank.toLumbridgeBank(ctx);
                    }
                    else{
                        if(!ctx.bank.isOpen()) {
                            ctx.objects.getNearest("Bank booth").doClick();
                        }
                        else{

                            //note: bank tutorial
                            RSWidget w = ctx.interfaces.getComponent(664,29);
                            if(w.isVisible()){
                                w.doClick();
                            }
                            Thread.sleep(500);

                            //note: want more bank space?
                            w = ctx.interfaces.getComponent(289,7);
                            if(w.isVisible()){
                                w.doClick();
                            }

                            Thread.sleep(500);
                            ctx.bank.depositAll();
                            Thread.sleep(500);
                            ctx.bank.withdraw("Tinderbox", 1);
                            ctx.bank.close();
                            stage = Stages.FIRE;
                        }
                    }

                }
                case FIRE -> {
                    RSPlayer p = ctx.players.getMyPlayer();
                    if(!(p.getLocation().getPlane() == 2)){
                        stage = Stages.BANK;
                    }
                    if(ctx.bank.isOpen()){
                        ctx.bank.close();
                    }
                    if(!ctx.inventory.isOpen()){
                        ctx.inventory.open();
                        if(ctx.inventory.getItem("Tinderbox") == null || ctx.inventory.getItems().length == 28){
                            stage = Stages.BANK;
                        }

                    }

                    AtomicBoolean foundLog = new AtomicBoolean(false);
                    Arrays.stream(ctx.groundItems.getAll()).forEach(i -> {

                        if(i.getItem().getName().equals("Logs")){
                            AtomicBoolean foundFire = new AtomicBoolean(false);
                            Arrays.stream(ctx.objects.getAllAt(i.getLocation())).forEach(
                                    it -> {
                                        if(it.getName().equals("Fire")){

                                            System.out.println("Found fire!");

                                            foundFire.set(true);
                                        }
                                    }
                            );
                            if(!foundFire.get()){

                                foundLog.set(true);
                                if( ctx.players.getMyPlayer().getAnimation() == -1){
                                    i.doAction("Light");
                                    //todo: turn to it with random intervals!
                                    ctx.camera.turnTo(i.getLocation());
                                }
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                    });

                    RSWidget levelUp = ctx.interfaces.getComponent(233,3);
                    if(levelUp != null){
                        levelUp.doClick();
                    }
                    if(!foundLog.get() ){
                        World[] worlds = ctx.client.getWorldList();
                        Random r = new Random();
                        int count = (int) Arrays.stream(worlds).filter(w -> w.getTypes().isEmpty()).count();
                        Optional<World> world = Arrays.stream(worlds).filter(w -> w.getTypes().isEmpty()).skip(r.nextInt(count)).findFirst();

                        System.out.println("hopping to: " + world.get().getId());
                        ctx.worldHopper.hop(world.get().getId());


                        ctx.client.hopToWorld(world.get());
                        System.out.println(ctx.client.getMouseCanvasPosition());
                        Thread.sleep(1000);


                        RSWidget switchWorld = ctx.interfaces.getComponent(193,0).getDynamicComponent(3);
                        if(switchWorld != null){
                            switchWorld.doClick();
                        }



                        return 9000;
                    }

                }
            }
            return 1000;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 1000;
    }
}
