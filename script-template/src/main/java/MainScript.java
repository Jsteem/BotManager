import To30.*;
import com.google.common.eventbus.Subscribe;
import common.PostRequest;
import common.Skills;
import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.DaxCredentials;
import dax_api.api_lib.models.DaxCredentialsProvider;
import dax_api.walker_engine.WalkingCondition;
import members.*;
import moneylow.Ashes;
import moneylow.PieDish;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.events.ChatboxInput;
import net.runelite.rsb.event.listener.PaintListener;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.script.Script;
import net.runelite.rsb.script.ScriptManifest;
import net.runelite.rsb.wrappers.RSNPC;
import net.runelite.rsb.wrappers.RSWidget;
import quests.*;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

@ScriptManifest(
        authors = { "Test" }, name = "Main", version = 0.1

)
public class MainScript extends Script implements PaintListener{

    Timer timerHeartbeat;
    Timer timerSkillUpdate;
    //todo: subscribe to the runelite event for this and handle it when you get notified (not polling)
    Timer eventDismisser;



    int timersSkillUpdateIntervalInMinutes = 30;
    int timerHeartbeatIntervalInMinutes = 1;
    int timerEventDismisserIntervalInMinutes = 3;

    private LoginBot loginBot = new LoginBot();
    private TutIsland tutIsland = new TutIsland();
    private FiremakerTo30 firemakerTo30 = new FiremakerTo30();
    private WoodcutterTo30 woodcutterTo30 = new WoodcutterTo30();
    private CookingTo30 cookingTo30 = new CookingTo30();
    private PieDish pieDish = new PieDish();
    private Ashes ashes = new Ashes();
    private FishingAndMiningTo30 fishingAndMiningTo30 = new FishingAndMiningTo30();
    private RomeoAndJuliet romeoAndJuliet = new RomeoAndJuliet();
    private GoblinDiplomacy goblinDiplomacy = new GoblinDiplomacy();
    private ChickenFighter chickenFighter = new ChickenFighter();
    private Fletching fletching = new Fletching();
    private DruidicRitual druidicRitual = new DruidicRitual();
    private Herblore herblore = new Herblore();
    private WaterfallQuest waterfall = new WaterfallQuest();
    private Crafting crafting = new Crafting();
    private Agility agility = new Agility();
    private Spades spades = new Spades();


    @Subscribe
    public void onChatBoxInput(ChatboxInput event) {
        System.out.println(event.getValue());
    }

    @Override
    public int loop() {

        try{
            //System.out.println("LOGIN index: " + ctx.client.getLoginIndex());

            //ctx.account.setStartScript("fletching");

            int val = loginBot.loop(ctx);

            if(val == 2000){
                if(!ctx.client.getUsername().equals("")){

                    if(ctx.account.getStartScript() != null){

                        switch(ctx.account.getStartScript()){
                            case "tutorialIsland" -> {
                                val =  tutIsland.loop(ctx);
                            }
                            case "firemakingTo30" -> {
                                val = firemakerTo30.loop(ctx);
                            }
                            case "woodcuttingTo30" -> {
                                val = woodcutterTo30.loop(ctx);
                            }
                            case "cookingTo30" -> {
                                val = cookingTo30.loop(ctx);
                            }
                            case "pieDish" -> {
                                val = pieDish.loop(ctx);
                            }
                            case "fishingAndMiningTo30" -> {
                                val = fishingAndMiningTo30.loop(ctx);
                            }
                            case "romeoAndJuliet" -> {
                                val = romeoAndJuliet.loop(ctx);
                            }
                            case "goblinDiplomacy" -> {
                                val = goblinDiplomacy.loop(ctx);
                            }
                            case "chickenFighter" ->{
                                val = chickenFighter.loop(ctx);
                            }
                            case "fletching" -> {
                                val = fletching.loop(ctx);
                            }
                            case "druidicRitual" -> {
                                val = druidicRitual.loop(ctx);
                            }
                            case "herblore" ->{
                                val = herblore.loop(ctx);
                            }
                            case "waterfall" -> {
                                val = waterfall.loop(ctx);
                            }
                            case "crafting" -> {
                                val = crafting.loop(ctx);
                            }
                            case "agility" -> {
                                val = agility.loop(ctx);
                            }
                            case "spades" ->{
                                val = spades.loop(ctx);
                            }

                        }
                    }
                }
            }
            //return value of -1 will stop the script!, return value of 0 will run script without any delay.
            return val;
        }
        catch(Exception e){
            e.printStackTrace();
        }
       return 1000;
    }


    @Override
    public void onFinish(){
        timerHeartbeat.cancel();
        timerSkillUpdate.cancel();
        eventDismisser.cancel();
        DaxWalker.setGlobalWalkingCondition(() -> this.isActive() ? WalkingCondition.State.CONTINUE_WALKER : WalkingCondition.State.EXIT_OUT_WALKER_FAIL);

    }



    @Override
    public boolean onStart() {
        DaxWalker.setGlobalWalkingCondition(() -> this.isActive() ? WalkingCondition.State.CONTINUE_WALKER : WalkingCondition.State.EXIT_OUT_WALKER_FAIL);

        timerHeartbeat = new Timer();
        timerSkillUpdate = new Timer();
        eventDismisser = new Timer();

        timerHeartbeat.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!ctx.client.getUsername().equals("")){
                    System.out.println("making post request for heartbeat with interval: " + timerHeartbeatIntervalInMinutes);
                    PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername() + "/heartbeat", timerHeartbeatIntervalInMinutes);

                    String s = (String) request.getResult();
                    System.out.println("setting script to : " + s);
                    ctx.account.setStartScript(s);
                }

            }
        }, 0,  timerHeartbeatIntervalInMinutes * 60 * 1000);

        timerSkillUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!ctx.client.getUsername().equals("") && ctx.game.getClientState() == GameState.LOGGED_IN){

                    PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername()  + "/skills", Skills.build(ctx));
                    request.getResult();
                    System.out.println("updating skills");


                    request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"displayName", ctx.players.getMyPlayer().getName()});
                    request.getResult();
                    System.out.println("updating displayName: " + ctx.players.getMyPlayer().getName());



                    int daysLeft = memberShipDaysLeft(ctx);
                    request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"membershipDaysLeft", daysLeft});
                    request.getResult();
                    System.out.println("updating membership days left: " + daysLeft);

                }

            }
        }, 0,   timersSkillUpdateIntervalInMinutes * 60 * 1000);





        //Pass DaxWalker credentials
        DaxWalker.setCredentials(new DaxCredentialsProvider() {
            @Override
            public DaxCredentials getDaxCredentials() {
                return new DaxCredentials("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY");
            }
        });
        return true;
    }


    @Override
    public void onRepaint(Graphics g) {

    }

    public int memberShipDaysLeft(MethodContext ctx){
        try{
            RSWidget w = ctx.interfaces.getComponent(109,25);
            if(w != null){
                String membershipString = w.getText();

                String daysString = membershipString.replaceAll("\\D+", "");

                if(membershipString.contains("None")){
                    return 0;
                }
                else{
                    return Integer.parseInt(daysString);
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 0;

    }
}
