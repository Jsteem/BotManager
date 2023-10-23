package dax_api.walker_engine.navigation_utils;

import dax_api.Filters;
import dax_api.shared.helpers.InterfaceHelper;
import dax_api.walker_engine.WaitFor;
import dax_api.walker_engine.interaction_handling.InteractionHelper;
import net.runelite.rsb.methods.Web;
import net.runelite.rsb.util.StdRandom;
import net.runelite.rsb.wrappers.RSWidget;
import net.runelite.rsb.wrappers.RSTile;

import java.util.Arrays;


public class GnomeGlider {

    private static final int GNOME_GLIDER_MASTER_INTERFACE = 138;

    public enum Location {
        TA_QUIR_PRIW ("Ta Quir Priw", 2465, 3501, 3),
        GANDIUS ("Gandius", 2970, 2972, 0),
        LEMANTO_ANDRA ("Lemanto Andra", 3321, 3430, 0),
        KAR_HEWO ("Kar-Hewo", 3284, 3211, 0),
        SINDARPOS ("Sindarpos", 2850, 3498, 0)
        ;

        private int x, y, z;
        private String name;
        Location(String name, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public RSTile getRSTile(){
            return new RSTile(x, y, z);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }

    public static boolean to(Location location) {
        if (!Web.methods.interfaces.get(GNOME_GLIDER_MASTER_INTERFACE).isValid()
                && !InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.actionsContains("Glider")), "Glider", () -> Web.methods.interfaces.get(GNOME_GLIDER_MASTER_INTERFACE).isValid() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
            return false;
        }

        RSWidget option = InterfaceHelper.getAllInterfaces(GNOME_GLIDER_MASTER_INTERFACE).stream().filter(rsInterface -> {
            String[] actions = rsInterface.getActions();
            return actions != null && Arrays.stream(actions).anyMatch(s -> s.contains(location.getName()));
        }).findAny().orElse(null);

        if (option == null){
            return false;
        }

        if (!option.doClick()){
            return false;
        }

        if (WaitFor.condition(StdRandom.uniform(5400, 6500), () -> location.getRSTile().distanceTo(new RSTile(Web.methods.players.getMyPlayer().getLocation())) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(250, 500);
            return true;
        }
        return false;
    }

}
