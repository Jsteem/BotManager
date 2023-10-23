package quests;

import common.GetRequest;
import common.PostRequest;
import common.Skills;
import dax_api.api_lib.DaxWalker;
import dax_api.api_lib.models.RunescapeBank;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.script.Random;
import net.runelite.rsb.wrappers.*;

import java.util.Arrays;

public class TutIsland {

    public TutIsland(){

    }

    public int loop(MethodContext ctx) {
        try {

            int progress = ctx.client.getVarpValue(281);
            System.out.println(progress);
            switch(progress){
                case 1 -> {
                    String displayName ="";
                    Widget nameWidget = ctx.client.getWidget(558,13);
                    if(nameWidget != null){
                        String response = nameWidget.getText();
                        System.out.println(response);


                        if(response.contains("Please") || response.contains("Sorry")) {


                            String url = "names/first";
                            GetRequest request = new GetRequest(url, String.class);
                            displayName = (String) request.getResult();

                            if(displayName != null){
                                System.out.println("Trying the following name: " + displayName);
                                Thread.sleep(5000);
                                resetText(ctx);

                                ctx.keyboard.sendText(displayName, false);

                                Thread.sleep(5000);

                                Widget lookup = ctx.client.getWidget(558,18);
                                RSWidget rsLookup = new RSWidget(ctx, lookup);
                                rsLookup.doClick();


                            }
                            return 5000;

                        }
                        else{
                            Widget lookup = ctx.client.getWidget(558,18);
                            RSWidget rsLookup = new RSWidget(ctx, lookup);
                            rsLookup.doClick();
                        }
                    }
                    else{

                        PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername()  + "/displayName", ctx.players.getMyPlayer().getName());
                        System.out.println(request.getResult());


                        for(int button = 0; button < 7; button++){
                            Widget design = ctx.client.getWidget(679, 13 + button * 4);
                            RSWidget rsDesign = new RSWidget(ctx, design);
                            int num = Random.random(2, 10);
                            for(int i = 0; i < num; i++){
                                rsDesign.doClick();
                                Thread.sleep(Random.random(0,500));
                            }
                            Thread.sleep(Random.random(1000,2000));

                        }

                        for(int button = 0; button < 5; button++){
                            Widget design = ctx.client.getWidget(679, 44 + button * 4);
                            RSWidget rsDesign = new RSWidget(ctx, design);
                            int num = Random.random(0, 5);
                            for(int i = 0; i < num; i++){
                                rsDesign.doClick();
                                Thread.sleep(Random.random(0,500));
                            }
                            Thread.sleep(Random.random(1000,2000));

                        }

                        Thread.sleep(Random.random(1000,2000));

                        new RSWidget(ctx, ctx.client.getWidget(679, 68)).doClick();
                    }
                }
                case 2 ->{

                    //Friend system busy
                    RSWidget cont = ctx.interfaces.getComponent(162, 42);
                    if(cont != null){
                        cont.doClick();
                    }

                    RSNPC guide = ctx.npcs.getNearest("Gielinor Guide");
                    guide.doAction("Talk-to");
                    Thread.sleep(Random.random(2000,4000));


                    for(int i = 0; i < 4; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                    RSWidget w = ctx.interfaces.getComponent(229,2);
                    if(w != null){
                        w.doClick();
                        Thread.sleep(1000);
                    }

                     w = ctx.interfaces.getComponent(219,1);
                    if(w != null){
                        w.getDynamicComponent(1).doClick();
                        Thread.sleep(1000);
                    }


                    w = ctx.interfaces.getComponent(219,1);
                    if(w != null){
                        w.getDynamicComponent(1).doClick();
                        Thread.sleep(1000);
                    }

                    for(int i = 0; i < 2; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                    return 1000;
                }
                case 3 -> {

                    Widget menu = ctx.client.getWidget(164, 40);
                    new RSWidget(ctx, menu).doClick();
                    Thread.sleep(1000);
                }
                case 7 -> {
                    RSNPC guide = ctx.npcs.getNearest("Gielinor Guide");
                    guide.doAction("Talk-to");
                    Thread.sleep(Random.random(2000,4000));

                    for(int i = 0; i < 4; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }

                }
                case 10 -> {
                    ctx.camera.turnTo(ctx.objects.getNearest("Door"));
                    ctx.objects.getNearest("Door").doAction("Open");
                    Thread.sleep(1000);

                }
                case 20 -> {
                    //todo: if in area closed in: open door and move


                    //todo: if in area outside but not in survival area then walk


                    ctx.walking.walkTo(new RSTile(3103,3095, 0));
                    Thread.sleep(Random.random(4000,8000));

                    RSNPC guide = ctx.npcs.getNearest("Survival Expert");
                    guide.doAction("Talk-to");


                }
                case 30 -> {
                    RSNPC guide = ctx.npcs.getNearest("Survival Expert");
                    guide.doAction("Talk-to");
                    for(int i = 0; i < 4; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                    Widget menu = ctx.client.getWidget(164, 54);
                    RSWidget rsMenu = new RSWidget(ctx, menu);
                    rsMenu.doClick();
                }
                case 40 -> {
                    RSTile fishSpot = new RSTile(3101,3093);
                    if(ctx.players.getMyPlayer().getLocation().distanceTo(fishSpot)> 3){
                        if(!ctx.players.getMyPlayer().isMoving()){
                            ctx.walking.walkTo(fishSpot);
                        }

                    }
                    System.out.println("fish");
                    ctx.npcs.getNearest("Fishing spot").doAction("Net");
                }
                case 50 -> {
                    Widget skillIcon = ctx.client.getWidget(164, 52);
                    new RSWidget(ctx, skillIcon).doClick();

                }
                case 60 -> {
                    RSNPC guide = ctx.npcs.getNearest("Survival Expert");
                    guide.doAction("Talk-to");
                    ctx.interfaces.clickContinue();
                }
                case 70 -> {
                    System.out.println("chop tree");
                    RSObject tree = ctx.objects.getNearest("Tree");
                    tree.doAction("Chop down");
                    Thread.sleep(3000);
                }
                case 80 -> {
                    //todo: check if the place it tries to light on, is empty
                    Widget skillIcon = ctx.client.getWidget(193, 0);
                    new RSWidget(ctx, skillIcon).doClick();
                    Thread.sleep(3000);
                    RSItem logs = ctx.inventory.getItem("Logs");
                    ctx.inventory.selectItem("Logs");

                    logs.doAction("use");
                    Thread.sleep(3000);
                    ctx.inventory.selectItem("Tinderbox");

                }
                case 90 -> {
                    //todo: make it so it renews the fire
                    //todo: it uses the shrimp on the bounding box of the fire, what if the survival expert is on top?
                    RSObject fire = ctx.objects.getNearest("Fire");
                    RSItem shrimps =  ctx.inventory.getItem("Raw shrimps");
                    shrimps.doAction("Use");
                    Thread.sleep(3000);
                    fire.getModel().doClick(true);
                    return 2000;

                }
                case 120 -> {
                    ctx.walking.walkTo(new RSTile(3091,3092,0));
                    RSObject gate = ctx.objects.getNearest("Gate");
                    if (gate != null) {
                        gate.doAction("Open");

                    }

                    Thread.sleep(3000);
                }
                case 130 -> {
                    RSObject door = ctx.objects.getNearest("Door");
                    ctx.walking.walkTo(new RSTile(3079,3084,0));
                    if(door != null){
                        door.doAction("Open");
                    }

                    Thread.sleep(3000);
                }
                case 140 -> {
                    RSNPC guide = ctx.npcs.getNearest("Master Chef");
                    ctx.camera.turnTo(guide);
                    Thread.sleep(1000);
                    guide.doAction("Talk-to");
                    Thread.sleep(1000);

                    //nothing interesting happens dialogue?
                    Widget cont = ctx.client.getWidget(162, 42);
                    RSWidget rsCont = new RSWidget(ctx, cont);
                    rsCont.doClick();

                    Thread.sleep(3000);
                    for(int i = 0; i < 4; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }

                }
                case 150 -> {
                    RSItem logs = ctx.inventory.getItem("Pot of flour");
                    ctx.inventory.selectItem(logs);
                    logs.doAction("use");
                    Thread.sleep(3000);
                    ctx.inventory.selectItem("Bucket of water");
                    Thread.sleep(3000);
                }

                case 160 -> {
                    RSObject range = ctx.objects.getNearest("Range");
                    RSItem dough =  ctx.inventory.getItem("Bread dough");
                    ctx.camera.turnTo(range);
                    Thread.sleep(3000);
                    dough.doAction("Use");
                    Thread.sleep(3000);
                    range.getModel().doClick(true);
                    Thread.sleep(3000);
                }
                case 170 -> {
                    ctx.walking.walkTo(new RSTile(3073,3090,0));
                    Thread.sleep(3000);
                    ctx.objects.getNearest("Door").doAction("Open");
                    Thread.sleep(3000);

                }
                case 200, 210 ->  {
                    RSPlayer p = ctx.players.getMyPlayer();
                    RSTile tile = new RSTile(3086, 3126);
                    if((p.getLocation().distanceTo(tile) > 3)){
                        if(!p.isMoving()){
                            DaxWalker.walkTo(tile);
                        }
                    }
                    else{
                        ctx.objects.getNearest("Door").doAction("Open");
                        Thread.sleep(2000);
                    }
                }
                case 220,230 -> {
                    RSNPC guide = ctx.npcs.getNearest("Quest Guide");
                    guide.doAction("Talk-to");
                    Thread.sleep(3000);


                    ctx.interfaces.clickContinue();
                    Thread.sleep(1000);

                    Widget icon = ctx.client.getWidget(164, 60);
                    new RSWidget(ctx, icon).doClick();
                }


                case 240 -> {

                    System.out.println(ctx.client.getMouseCanvasPosition());
                    ctx.mouse.click(250,443, true);



                    RSNPC guide = ctx.npcs.getNearest("Quest Guide");
                    guide.doAction("Talk-to");
                    Thread.sleep(3000);
                    for(int i = 0; i < 10; i++){
                        Widget w = ctx.client.getWidget(193, 0);
                        if(w != null){
                            new RSWidget(ctx, w).doClick();
                        }
                        else{
                            ctx.interfaces.clickContinue();
                        }
                        Thread.sleep(1000);
                    }

                }
                case 250 ->  {
                    RSObject ladder = ctx.objects.getNearest("Ladder");
                    ctx.camera.turnTo(ladder);
                    Thread.sleep(3000);
                    ladder.doClick();
                    Thread.sleep(3000);

                    RSPlayer p = ctx.players.getMyPlayer();
                    RSTile tile = new RSTile(3079,9504,0);
                    if((p.getLocation().distanceTo(tile) > 3)){
                        if(!p.isMoving()){
                            DaxWalker.walkTo(tile);
                        }
                    }
                    else{
                        RSNPC guide = ctx.npcs.getNearest("Mining Instructor");
                        guide.doAction("Talk-to");

                        for(int i = 0; i < 10; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(2000);
                        }
                    }


                }
                case 260 -> {

                    RSPlayer p = ctx.players.getMyPlayer();
                    RSTile tile = new RSTile(3079,9504,0);
                    if((p.getLocation().distanceTo(tile) > 3)){
                        if(!p.isMoving()){
                            DaxWalker.walkTo(tile);
                        }
                    }
                    else{
                        RSNPC guide = ctx.npcs.getNearest("Mining Instructor");
                        guide.doAction("Talk-to");

                        for(int i = 0; i < 10; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(2000);
                        }
                    }


                }
                case 270 -> {
                    ctx.walking.walkTo(new RSTile(3077,9505,0));
                    Thread.sleep(4000);

                }
                case 300 -> {
                    RSObject rock = ctx.objects.getNearest("Tin rocks");


                    rock.doAction("Mine");

                    Thread.sleep(4000);
                }
                case 310 -> {
                    RSObject rock = ctx.objects.getNearest("Copper rocks");


                    rock.doAction("Mine");

                    Thread.sleep(4000);
                }
                case 320 -> {
                    RSObject ladder = ctx.objects.getNearest("Furnace");
                    ctx.camera.turnTo(ladder);
                    Thread.sleep(3000);
                    ladder.doClick();
                    Thread.sleep(3000);
                }
                case 330 -> {
                    ctx.mouse.click(250,443, true);
                    RSNPC guide = ctx.npcs.getNearest("Mining Instructor");
                    ctx.camera.turnTo(guide);
                    guide.doAction("Talk-to");
                    Thread.sleep(2000);
                    for(int i = 0; i < 10; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(2000);
                    }

                    //todo: solve this with dynamic widget
                    //this.forceContinue(ctx);
                }
                case 340 -> {
                    RSObject ladder = ctx.objects.getNearest("Anvil");
                    ctx.camera.turnTo(ladder);
                    Thread.sleep(3000);
                    ladder.doClick();
                    Thread.sleep(3000);
                }
                case 350 -> {
                    Widget w = ctx.client.getWidget(312, 9);
                    if(w != null){
                        new RSWidget(ctx, w).doClick();
                        Thread.sleep(3000);
                    }
                    else{
                        RSObject ladder = ctx.objects.getNearest("Anvil");
                        ctx.camera.turnTo(ladder);
                        Thread.sleep(3000);
                        ladder.doClick();
                    }

                }
                case 360 -> {
                    //todo: check if you are already in the area
                    ctx.walking.walkTo(new RSTile(3093,9502,0));
                    Thread.sleep(3000);
                    RSObject ladder = ctx.objects.getNearest("Gate");
                    ctx.camera.turnTo(ladder);

                    ladder.doClick();
                    Thread.sleep(3000);

                    //todo: possibility to lock oneself out again?

                }
                case 370 -> {
                    ctx.walking.walkTo(new RSTile(3106,9508,0));
                    RSNPC guide = ctx.npcs.getNearest("Combat Instructor");
                    if(guide != null){
                        ctx.camera.turnTo(guide);
                        guide.doAction("Talk-to");
                        Thread.sleep(2000);
                        for(int i = 0; i < 10; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(2000);
                        }
                        Thread.sleep(2000);
                    }

                }
                case 390 -> {
                    Widget w = ctx.client.getWidget(164, 55);
                    new RSWidget(ctx, w).doClick();
                }
                case 400 -> {
                    Widget w = ctx.client.getWidget(387, 2);
                    new RSWidget(ctx, w).doClick();
                }
                case 405 -> {
                    Widget w = ctx.client.getWidget(84, 3);
                    if (w != null) {
                        new RSWidget(ctx, w).getDynamicComponent(11).doClick();
                        Thread.sleep(2000);
                    }
                    else{
                        RSItem dagger = ctx.inventory.getItem("Bronze dagger");
                        dagger.doClick(true);
                    }

                    Thread.sleep(2000);
                }
                default -> {
                    return 1000;
                }

                case 410 -> {
                    RSNPC guide = ctx.npcs.getNearest("Combat Instructor");
                    ctx.camera.turnTo(guide);
                    guide.doAction("Talk-to");
                    Thread.sleep(2000);
                    for(int i = 0; i < 5; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(2000);
                    }

                }
                case 420 -> {
                    RSItem dagger = ctx.inventory.getItem("Bronze sword");
                    if(dagger != null){
                        dagger.doClick(true);
                        Thread.sleep(2000);
                    }

                    RSItem shield = ctx.inventory.getItem("Wooden shield");
                    if(shield != null){
                        shield.doClick(true);
                        Thread.sleep(2000);
                    }

                }
                case 430 -> {
                    //click the combat widget
                    Widget w = ctx.client.getWidget(164, 51);
                    if (w != null) {
                        new RSWidget(ctx, w).doClick();
                        Thread.sleep(2000);
                    }
                }
                case 440 -> {
                    RSObject gate =  ctx.objects.getNearest("Gate");
                    if (gate == null) {
                        ctx.walking.walkTo(new RSTile(3111,9518,0));
                        Thread.sleep(2000);

                    }
                    else{
                        gate.doAction("Open");
                    }

                    Thread.sleep(3000);
                }
                case 450, 460 -> {
                    //todo: check if the rat is in combat, else choose another one..
                    RSNPC guide = ctx.npcs.getNearest("Giant rat");
                    if(guide != null && !ctx.players.getMyPlayer().isInCombat()){
                        System.out.println("attack");
                        ctx.camera.turnTo(guide);
                        Thread.sleep(3000);
                        guide.doAction("Attack");
                    }

                }
                case 470 -> {
                    //todo: check for the area else you would be locked inside again
                    //todo: bugs where it searches for the gate when it should be talking to instructor...
                    RSNPC guide = ctx.npcs.getNearest("Combat Instructor");
                    ctx.camera.turnTo(guide);
                    if(ctx.players.getMyPlayer().getLocation().distanceTo(guide) < 2 && !ctx.players.getMyPlayer().isMoving()){
                        DaxWalker.walkTo(guide);
                    }
                    else{

                        guide.doAction("Talk-to");
                        Thread.sleep(2000);

                        RSWidget w =ctx.interfaces.getComponent(162,42);

                        if (w != null) {
                            w.doClick();
                        }

                        for(int i = 0; i < 5; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(1000);
                        }

                    }


                }
                case 480,490 -> {

                    ctx.inventory.open();

                    RSItem dagger = ctx.inventory.getItem("Bronze arrow");
                    if(dagger != null){
                        dagger.doClick(true);
                        Thread.sleep(2000);
                    }

                    RSItem shield = ctx.inventory.getItem("Shortbow");
                    if(shield != null){
                        shield.doClick(true);
                        Thread.sleep(2000);
                    }


                    RSNPC guide = ctx.npcs.getNearest("Giant rat");
                    if(guide != null && !ctx.players.getMyPlayer().isInCombat()){
                        System.out.println("attack");
                        ctx.camera.turnTo(guide);
                        Thread.sleep(3000);
                        guide.doAction("Attack");
                    }

                }
                case 500 -> {

                    ctx.walking.walkTo(new RSTile(3111,9524,0));
                    Thread.sleep(2000);
                    RSObject ladder = ctx.objects.getNearest("Ladder");


                        ctx.camera.turnTo(ladder);
                        ladder.doClick();
                        Thread.sleep(3000);

                }

                case 510,520 -> {

                    RSObject ladder = ctx.objects.getNearest("Bank booth");
                    if(ladder == null){
                        ctx.walking.walkTo(new RSTile(3122,3123,0));
                        Thread.sleep(2000);
                    }
                    else{
                        ctx.camera.turnTo(ladder);
                        ladder.doClick();
                        Thread.sleep(3000);
                    }
                    if(ctx.bank.isOpen()){
                        //note: want more bank space?
                        ctx.bank.open();
                        RSWidget w = ctx.interfaces.getComponent(289,7);
                        if(w.isVisible()){
                            w.doClick();
                        }

                        ctx.bank.depositAll();
                        Thread.sleep(3000);
                        ctx.bank.close();
                        Thread.sleep(1000);

                        //you have nothing to deposit
                        w = ctx.interfaces.getComponent(162,42);
                        if(w.isVisible()){
                            w.doClick();
                        }


                        Arrays.stream(ctx.objects.getAllAt(new RSTile(3119, 3121, 0))).forEach(i ->i.doClick());


                        for(int i = 0; i < 5; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(2000);
                        }

                        w = ctx.interfaces.getComponent(193,0);
                        w.getDynamicComponent(2).doClick();


                        Thread.sleep(1000);
                    }
                    else{
                        ladder = ctx.objects.getNearest("Bank booth");
                        ladder.doClick();
                    }

                    //todo: widget with custom id?

                }
                case 525 -> {
                    //todo: click the right door...
                    ctx.walking.walkTo( new RSTile(3124, 3124, 0));
                    Thread.sleep(2000);
                    RSObject ladder = ctx.objects.getNearest("Door");
                    ladder.doClick();
                }
                case 530 -> {
                    RSNPC guide = ctx.npcs.getNearest("Account Guide");
                    ctx.camera.turnTo(guide);
                    guide.doAction("Talk-to");
                    for(int i = 0; i < 10; i++){

                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                }
                case 531 -> {
                    //todo: widget doesnt work
                    ctx.interfaces.clickContinue();
                    Thread.sleep(1000);
                    ctx.interfaces.getComponent(164,38).doClick();


                }

                case 532 -> {
                    RSNPC guide = ctx.npcs.getNearest("Account Guide");
                    ctx.camera.turnTo(guide);
                    guide.doAction("Talk-to");
                    for(int i = 0; i < 20; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                }
                case 540 -> {
                    ctx.walking.walkTo( new RSTile(3129, 3124, 0));
                    Thread.sleep(3000);
                    RSObject ladder = ctx.objects.getNearest("Door");
                    ladder.doClick();

                }
                case 550 -> {
                    RSNPC guide = ctx.npcs.getNearest("Brother Brace");
                    if(guide == null){
                        ctx.walking.walkTo( new RSTile(3127, 3107, 0));
                        Thread.sleep(3000);
                    }
                    else{

                        ctx.camera.turnTo(guide);
                        guide.doAction("Talk-to");

                        for(int i = 0; i < 20; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(1000);
                        }
                    }



                }
                case 560 -> {
                    //prayer icon
                    ctx.interfaces.getComponent(164,56).doClick();
                }

                case 570 -> {
                    RSNPC guide = ctx.npcs.getNearest("Brother Brace");
                    ctx.camera.turnTo(guide);
                    guide.doAction("Talk-to");

                    for(int i = 0; i < 5; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                }
                case 580 -> {
                    //friend list icon
                    ctx.interfaces.getComponent(164,39).doClick();
                }
                case 600 -> {
                    RSNPC guide = ctx.npcs.getNearest("Brother Brace");
                    ctx.camera.turnTo(guide);
                    guide.doAction("Talk-to");

                    for(int i = 0; i < 4; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(1000);
                    }
                }
                case 610 -> {


                    RSObject ladder = ctx.objects.getNearest("Door");
                    ladder.doClick();
                }
                case 620, 640 -> {
                    RSNPC guide = ctx.npcs.getNearest("Magic Instructor");
                    if(guide == null){
                        ctx.walking.walkTo( new RSTile(3141, 3087, 0));
                        Thread.sleep(3000);
                    }
                    else{

                        ctx.camera.turnTo(guide);
                        guide.doAction("Talk-to");

                        for(int i = 0; i < 10; i++){
                            ctx.interfaces.clickContinue();
                            Thread.sleep(1000);
                        }
                    }
                }
                case 630 -> {
                    //magic icon
                    ctx.interfaces.getComponent(164,57).doClick();
                }
                case 650 -> {
                    printMouse(ctx);
                    RSNPC guide = ctx.npcs.getNearest("Chicken");
                    if(guide != null){
                        RSWidget w = ctx.interfaces.getComponent(218,7);

                        w.doClick();

                        Thread.sleep(2000);
                        ctx.camera.turnTo(guide);
                        guide.doClick();

                        System.out.println("chicken found");
                    }
                    else{
                        System.out.println("no chicken found");
                    }

                }

                case 670 -> {

                    RSNPC guide = ctx.npcs.getNearest("Magic Instructor");

                    guide.doAction("Talk-to");
                    Thread.sleep(2000);

                    ctx.interfaces.clickContinue();
                    Thread.sleep(2000);

                    //go to the mainland
                    ctx.interfaces.getComponent(219,1).getDynamicComponent(1).doClick();
                    Thread.sleep(2000);

                    ctx.interfaces.clickContinue();
                    Thread.sleep(2000);

                    //look for text: become an ironman, they sometimes change the position of this
                    for(int i = 1; i < 4; i++){
                        RSWidget w = ctx.interfaces.getComponent(219,1).getDynamicComponent(i);
                        System.out.println(w.getText());
                        if(w.getText().equals("No, I'm not planning to do that.")){
                            w.doClick();
                        }
                        else if(w.getText().equals("Yes, send me to the mainland")){
                            w.doClick();
                        }
                    }

                    Thread.sleep(2000);

                    for(int i = 0; i< 5; i++){
                        ctx.interfaces.clickContinue();
                        Thread.sleep(2000);
                    }

                    ctx.interfaces.getComponent(193,0).getDynamicComponent(2).doClick();
                    Thread.sleep(2000);

                }
                case 1000 -> {
                    PostRequest request = new PostRequest("accounts/" + ctx.client.getUsername() + "/setAccountValue", new Object[]{"tutorialIslandComplete", true});
                    System.out.println(request.getResult());

                    request = new PostRequest("accounts/" + ctx.client.getUsername()  + "/skills", Skills.build(ctx));
                    System.out.println(request.getResult());

                    return 100000;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return 1000;
    }

    public void printMouse(MethodContext ctx){
        System.out.println(ctx.client.getMouseCanvasPosition());
    }
    public void resetText(MethodContext ctx) throws InterruptedException {
        Widget displayName = ctx.client.getWidget(558,12);
        RSWidget rsDisplayName = new RSWidget(ctx, displayName);
        rsDisplayName.doClick();
        Thread.sleep(1000);

        String text = ctx.client.getWidget(558,12).getText();
        if(text.length() > 1){
            for (int i = 0; i < text.length(); i++) {
                ctx.keyboard.sendKey('\b');
                Thread.sleep(500);
            }
        }

        Thread.sleep(5000);
    }






}
