package dax_api.walker;

import dax_api.walker.handlers.move_task.impl.DefaultObjectHandler;
import dax_api.walker.handlers.move_task.impl.DefaultWalkHandler;
import dax_api.walker.handlers.passive_action.PassiveAction;
import dax_api.walker.handlers.special_cases.SpecialCaseHandler;
import dax_api.walker.handlers.special_cases.SpecialCaseHandlers;
import dax_api.walker.models.DaxLogger;
import dax_api.walker.models.MoveTask;
import dax_api.walker.models.enums.MoveActionResult;
import dax_api.walker.models.enums.Situation;
import dax_api.walker.utils.path.DaxPathFinder;
import dax_api.walker.utils.path.PathUtils;
import net.runelite.rsb.methods.Web;
import net.runelite.rsb.wrappers.RSTile;

import java.util.ArrayList;
import java.util.List;

import static dax_api.walker.models.enums.Situation.COLLISION_BLOCKING;

public class DaxWalkerEngine implements DaxLogger {

    private List<PassiveAction> passiveActions;

    public DaxWalkerEngine() {
        passiveActions = new ArrayList<>();
    }

    public void addPassiveAction(PassiveAction passiveAction) {
        passiveActions.add(passiveAction);
    }

    public List<PassiveAction> getPassiveActions() {
        return passiveActions;
    }

    public boolean walkPath(List<RSTile> path) {
        int failAttempts = 0;

        while (failAttempts < 3) {
            MoveActionResult moveActionResult = walkNext(path);
            if (reachedEnd(path)) return true;
            if (moveActionResult == MoveActionResult.FATAL_ERROR) break;
            if (moveActionResult == MoveActionResult.SUCCESS) {
                failAttempts = 0;
            } else {
                log(String.format("Failed action [%d]", ++failAttempts));
            }
        }

        return false;
    }

    private boolean reachedEnd(List<RSTile> path) {
        if (path == null || path.size() == 0) return true;
        RSTile tile = new RSTile(Web.methods.walking.getDestination());
        return tile != null && tile.equals(path.get(path.size() - 1));
    }

    private MoveActionResult walkNext(List<RSTile> path) {
        MoveTask moveTask = determineNextAction(path);
        debug("Move task: " + moveTask);

        SpecialCaseHandler specialCaseHandler = SpecialCaseHandlers.getSpecialCaseHandler(moveTask);
        if (specialCaseHandler != null) {
            log(String.format("Overriding normal behavior with special handler: %s", specialCaseHandler.getName()));
            return specialCaseHandler.handle(moveTask, passiveActions);
        }

        switch (moveTask.getSituation()) {

            case COLLISION_BLOCKING:
            case DISCONNECTED_PATH:
                return new DefaultObjectHandler().handle(moveTask, passiveActions);

            case NORMAL_PATH_HANDLING:
                return new DefaultWalkHandler().handle(moveTask, passiveActions);

            case PATH_TOO_FAR:

            default:
                return MoveActionResult.FAILED;
        }
    }

    private MoveTask determineNextAction(List<RSTile> path) {
        RSTile furthestClickable = PathUtils.getFurthestReachableTileInMinimap(path);
        if (furthestClickable == null) {
            return new MoveTask(Situation.PATH_TOO_FAR, null, null);
        }

        RSTile next;
        try {
            next = PathUtils.getNextTileInPath(furthestClickable, path);
        } catch (PathUtils.NotInPathException e) {
            return new MoveTask(Situation.PATH_TOO_FAR, null, null);
        }

        if (next != null) {
            if (furthestClickable.distanceToDouble(next) >= 2D) {
                return new MoveTask(Situation.DISCONNECTED_PATH, furthestClickable, next);
            }

            if (!DaxPathFinder.canReach(next)) {
                return new MoveTask(COLLISION_BLOCKING, furthestClickable, next);
            }
        }

        return new MoveTask(Situation.NORMAL_PATH_HANDLING, furthestClickable, next);
    }


}
