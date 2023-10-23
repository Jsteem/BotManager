package common;


import lombok.Getter;

@Getter

public class TradeRequest {
    private String displaySender;
    private String displayReceiver;
    private int amountToTrade;
    private int[] location;
    private int world;

}

