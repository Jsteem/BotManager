import common.GetRequest;
import common.PostRequest;
import common.Skills;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.runelite.api.WorldType;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.script.Random;
import net.runelite.rsb.wrappers.RSWidget;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

@Getter
@Setter
public class LoginBot {
    boolean startup = true;
    public LoginBot() {
    }


    @Deprecated
    public int loop(MethodContext ctx) {

        try {

            if (ctx.game.getClientState() == GameState.LOGIN_SCREEN) {
                if(startup){
                    ctx.mouse.click(50, 486, true);
                    Thread.sleep(2000);
                    ctx.mouse.click(734, 13, true);
                    Thread.sleep(2000);

                    int excludeMask = WorldType.toMask(EnumSet.of(
                            WorldType.PVP,
                            WorldType.PVP_ARENA,
                            WorldType.HIGH_RISK,
                            WorldType.QUEST_SPEEDRUNNING,
                            WorldType.BETA_WORLD,
                            WorldType.DEADMAN,
                            WorldType.SKILL_TOTAL

                    ));

                    World[] worlds = ctx.client.getWorldList();
                    java.util.Random r = new java.util.Random();

                    int includeMask =  WorldType.toMask(EnumSet.of(WorldType.MEMBERS));
                    boolean isMember = ctx.account.getIsMember();
                    int count = (int)Arrays.stream(worlds)
                            .filter(world -> {
                                int worldMask = WorldType.toMask(world.getTypes());
                                return isMember? (worldMask & excludeMask) == 0 && (worldMask & includeMask) != 0 : (worldMask & excludeMask) == 0 && (worldMask == 0);
                            }).count();

                    System.out.println(count);
                    Optional<World> world = Arrays.stream(worlds)
                            .filter(w -> {
                                int worldMask = WorldType.toMask(w.getTypes());
                                return isMember? (worldMask & excludeMask) == 0 && (worldMask & includeMask) != 0 : (worldMask & excludeMask) == 0 && (worldMask == 0);
                            }).skip(r.nextInt(count)).findFirst();

                    ctx.client.changeWorld(world.get());
                    startup = false;
                }



                System.out.println("LOGIN index: " + ctx.client.getLoginIndex());
                switch (ctx.client.getLoginIndex()) {
                    case 0:
                        ctx.keyboard.sendKey('\n');
                        return Random.random(1000, 5000);
                    case 1:

                        return 10000;
                    case 2:
                        //System.out.println("login field: " + ctx.client.getCurrentLoginField());
                        switch (ctx.client.getCurrentLoginField()) {

                            case 0 -> {
                                for (int i = 0; i < 30; i++) {
                                    ctx.keyboard.sendKey('\b');
                                    Thread.sleep(10);
                                }
                                ctx.keyboard.sendText(ctx.account.getLogin(), true);

                                return 1000;
                            }
                            case 1 -> {
                                for (int i = 0; i < 30; i++) {
                                    ctx.keyboard.sendKey('\b');
                                    Thread.sleep(10);

                                }
                                ctx.keyboard.sendText(ctx.account.getPassword(), true);

                                return 10000;
                            }
                            default -> {
                                throw new Exception("Unknown login state");
                            }
                        }
                    case 3:

                        //incorrect username or password
                        ctx.mouse.click(382, 279, true);
                        return 1000;
                    case 4:

                        return 1000;
                    case 24:
                        //you were disconnected from the server
                        //client is updated
                        ctx.mouse.click(382, 303, true);
                        return 1000;

                    case 14:
                        //your account has been involved in some serious rule breaking
                        PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"isBanned", true});
                        request.getResult();
                        System.out.println("setting ban status to true: " + ctx.client.getUsername());
                        ctx.mouse.click(384, 329, true);
//                    case 24:
//                        //You need a members account to log in to this world
//                        request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"isMember", false});
//                        request.getResult();
//                        System.out.println("setting isMember status to false: " + ctx.client.getUsername());
//                        ctx.mouse.click(384, 329, true);


                }
            }

            if (ctx.game.getClientState() == GameState.LOGGED_IN) {

                if(ctx.camera.getPitch() != -31){
                    for(int i = 0; i < 10; i++){
                        ctx.camera.setPitch(true);
                    }
                }
                if(ctx.client.getMinimapZoom() != 3.5){
                    ctx.client.setMinimapZoom(3.5);
                }


                //System.out.println("zoom: " + ctx.client.getMinimapZoom());

                //System.out.println("zoom full map: " + ctx.client.get3dZoom());


                ctx.walking.setRun(true);



                RSWidget clickToPlay = ctx.interfaces.getComponent(378, 73);

                if (clickToPlay != null) {
                    if (clickToPlay.doClick()) {
                        return Random.random(800, 2000);
                    }

                }
            }


            return 2000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2000;
    }


}