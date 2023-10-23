package util;

import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSWidget;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BooleanSupplier;

import java.util.function.BooleanSupplier;

public class WaitUntil {
    public static void wait(BooleanSupplier condition, long timeoutMillis){
        long startTime = System.currentTimeMillis();
        int loopCount = 0;

        do {
            loopCount++;
            // Your loop logic goes here

            // Simulating some work using sleep
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check if the timeout has been reached
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= timeoutMillis) {
                System.out.println("Timeout reached. Exiting the loop.");
                break;
            }

        } while (condition.getAsBoolean());

        System.out.println("Loop executed " + loopCount + " times.");

    }
    public static void clickWidget(MethodContext ctx, int parentId, int id, long timeoutMillis){
        long startTime = System.currentTimeMillis();
        int loopCount = 0;

        do {
            loopCount++;
            // Your loop logic goes here
            RSWidget w = ctx.interfaces.getComponent(parentId, id);

            if(w != null && w.isVisible()){
                w.doClick();
                break;
            }

            // Simulating some work using sleep
            try {
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check if the timeout has been reached
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= timeoutMillis) {
                System.out.println("Timeout reached. Exiting the loop.");
                break;
            }

        } while (true);

        System.out.println("Loop executed " + loopCount + " times.");
    }

}
