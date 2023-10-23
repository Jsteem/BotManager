package util;

import common.TradeRequest;
import dax_api.api_lib.DaxWalker;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSPlayer;
import net.runelite.rsb.wrappers.RSTile;

public class Trade {
    private TradeRequest tradeRequest = null;

    public int loop(MethodContext ctx) {
        try{
            if(tradeRequest == null){
                //todo: ask for a traderequest with api

            }
            else{
                RSTile meetingPoint = new RSTile(tradeRequest.getLocation()[0], tradeRequest.getLocation()[1]);
                if(ctx.players.getMyPlayer().getPosition().distanceTo(meetingPoint) > 5){
                    DaxWalker.walkTo(meetingPoint);
                }
                else{
                    int amountOfGold = tradeRequest.getAmountToTrade();
                    boolean shouldGive = tradeRequest.getDisplaySender().equals(ctx.players.getMyPlayer().getName());

                    String displayNameToTrade = shouldGive? tradeRequest.getDisplayReceiver() : tradeRequest.getDisplaySender();
                    RSPlayer playerToTrade = ctx.players.getNearest(displayNameToTrade);

                    if(shouldGive && ctx.inventory.getItem("Coins") == null){
                        //todo: retrieve the gold from the bank and put it in the inventory
                        MyBank.bank(ctx);
                    }

                    //todo: look for the player and wait


                    if (playerToTrade == null || (playerToTrade.getPosition().distanceTo(ctx.players.getMyPlayer().getPosition()) > 3)) {
                        return 10000;
                    }
                    else{
                        //todo: make the trade
                        ctx.trade.tradePlayer(playerToTrade );
                        Thread.sleep(1000);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return 1000;
    }

}
