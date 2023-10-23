package dax_api.walker_engine.navigation_utils;

import dax_api.Filters;
import dax_api.shared.helpers.InterfaceHelper;
import dax_api.walker_engine.Loggable;
import dax_api.walker_engine.WaitFor;
import dax_api.walker_engine.interaction_handling.InteractionHelper;
import dax_api.walker_engine.interaction_handling.NPCInteraction;
import net.runelite.rsb.methods.Web;
import net.runelite.rsb.wrappers.RSArea;
import net.runelite.rsb.wrappers.RSWidget;
import net.runelite.rsb.wrappers.RSTile;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Charter implements Loggable {

    private static final int CHARTER_INTERFACE_MASTER = 72;
    private static Charter instance;

    private static Charter getInstance(){
        return instance != null ? instance : (instance = new Charter());
    }

    public static boolean to(LocationProperty locationProperty){
        if (locationProperty == null){
            return false;
        }
        if (!openCharterMenu()){
            getInstance().log("Failed to open charter menu.");
            return false;
        }
        HashMap<LocationProperty, Location> charterLocations = getCharterLocations();
        Location location = charterLocations.get(locationProperty);
        if (location == null){
            getInstance().log("Location: " + locationProperty + " is not available. " + charterLocations.keySet());
            return false;
        }
        if (!location.click()){
            getInstance().log("Failed to click charter location.");
            return false;
        }
        if (!NPCInteraction.waitForConversationWindow()){
            getInstance().log("Confirmation dialogue did not appear.");
        }
        NPCInteraction.handleConversation(new String[]{"Ok", "Okay"});
        return WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static boolean openCharterMenu() {
        return Web.methods.interfaces.get(CHARTER_INTERFACE_MASTER).isValid() ||
                InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.actionsEquals("Charter")), "Charter", () -> Web.methods.interfaces.get(CHARTER_INTERFACE_MASTER).isValid() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
    }

    private static HashMap<LocationProperty, Location> getCharterLocations(){
        HashMap<LocationProperty, Location> locations = new HashMap<>();
        InterfaceHelper.getAllInterfaces(CHARTER_INTERFACE_MASTER).stream().filter(

                rsInterface -> rsInterface != null
                && rsInterface.getFontID() == 495
                && !rsInterface.isVisible()
                && rsInterface.isTextShadowed())

                .collect(Collectors.toList())
                .forEach(rsInterface -> locations.put(
		                LocationProperty.stringToLocation(rsInterface.getText()), new Location(rsInterface)));
        return locations;
    }

    @Override
    public String getName() {
        return "Charter";
    }

    public enum LocationProperty {
        PORT_TYRAS ("Port Tyras", null),
        PORT_PHASMATYS ("Port Phasmatys", new RSTile(3702, 3503, 0)),
        CATHERBY ("Catherby", new RSTile(2796, 3414, 0)),
        SHIPYARD ("Shipyard", null),
        KARAMJA ("Musa Point", new RSTile(2956, 3146, 0)),
        BRIMHAVEN ("Brimhaven", new RSTile(2760, 3237, 0)),
        PORT_KHAZARD ("Port Khazard", new RSTile(2674, 3149, 0)),
        PORT_SARIM ("Port Sarim", new RSTile(3041, 3193, 0)),
        MOS_LE_HARMLESS ("Mos le'Harmless", null),
        CRANDOR ("Crandor", null);

        private String name;
        private RSArea area;

        LocationProperty(String name, RSTile center){
            this.name = name;
            if (center != null) {
                this.area = new RSArea(center, 15);
            }
        }

        public boolean valid(RSTile tile) {
            return area != null && tile != null && area.contains(tile);
        }

        public static LocationProperty stringToLocation(String name){
            for (LocationProperty locationProperty : values()){
                if (name.equals(locationProperty.name)){
                    return locationProperty;
                }
            }
            return null;
        }

        public static LocationProperty getLocation(RSTile tile){
            for (LocationProperty locationProperty : values()){
                if (locationProperty.valid(tile)){
                    return locationProperty;
                }
            }
            return null;
        }

        @Override
        public String toString(){
            return name;
        }
    }


    public static class Location {

        private String name;
        private RSWidget rsInterface;

        private Location(RSWidget rsInterface){
            this.name = rsInterface.getText();
            this.rsInterface = rsInterface;
        }

        public String getName() {
            return name;
        }

        public RSWidget getRsInterface() {
            return rsInterface;
        }

        public boolean click(String... options){
            for (String option : options) {
                if (rsInterface.doAction(option))
                    return true;
            }
            return false;
        }

        @Override
        public String toString(){
            return name;
        }
    }

}
