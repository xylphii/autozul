package net.runelite.client.plugins.autozulrah;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.RuneBotApi.RBApi;

public class Banking extends MiscController {

    private boolean disablePrayers = true;

    @Override
    StateChange eventLoop()
    {
        if (super.eventLoop() != null) return StateChange.BANKING;

        if (Inventory.search().nameContainsInsensitive(RBApi.configCSVToHashSet(config.mageSwitch()).stream().findFirst().orElseThrow().replace("*", "")).first().isPresent()) {
            swapGearAndPrayer(GearType.MAGE);
            timeout = 2;
            return StateChange.BANKING;
        }

        // lol
        if (disablePrayers) {
            disablePrayers();
            disablePrayers = false;
        }

        if (bankController.eventLoop()) return StateChange.BANKING;


        return StateChange.START_KILL;
    }

}
