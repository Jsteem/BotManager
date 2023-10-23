package dax_api.walker.handlers.move_task.impl;

import dax_api.walker.handlers.move_task.MoveTaskHandler;
import dax_api.walker.handlers.passive_action.PassiveAction;
import dax_api.walker.models.MoveTask;
import dax_api.walker.models.enums.MoveActionResult;
import dax_api.walker.utils.AccurateMouse;
import dax_api.walker.utils.path.DaxPathFinder;
import net.runelite.rsb.methods.Web;
import net.runelite.rsb.util.StdRandom;

import java.util.List;

public class DefaultWalkHandler implements MoveTaskHandler {

    @Override
    public MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList) {
        if (!AccurateMouse.clickMinimap(moveTask.getDestination())) {
            return MoveActionResult.FAILED;
        }
        int initialDistance = DaxPathFinder.distance(moveTask.getDestination());

        if (!waitFor(() -> {
            int currentDistance = DaxPathFinder.distance(moveTask.getDestination());
            return currentDistance <= 2 || initialDistance - currentDistance > getDistanceOffset();
        }, 3500, passiveActionList).isSuccess()) {
            return MoveActionResult.FAILED;
        }

        return MoveActionResult.SUCCESS;
    }

    private int getDistanceOffset() {
        return  Web.methods.walking.isRunEnabled() ? (int) StdRandom.gaussian(3, 10, 7, 2) : (int) StdRandom.gaussian(2, 10, 5, 2);
    }

}
