package net.runelite.client.plugins.autozulrah;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.TileItemPackets;
import com.example.RuneBotApi.Items.InventoryItems;
import com.example.RuneBotApi.RBApi;

import java.util.Optional;

public class Looting extends MiscController {

    private boolean startedLooting = false;
    private boolean startUp = false;
    private State state = State.LOOTING;

    @Override
    StateChange eventLoop() {

        if (super.eventLoop() != null) return StateChange.LOOTING;

        switch (state)
        {
            case INTERACT:
                TileObjectInteraction.interact(11701, "Read");
                state = State.AWAIT_MSG;
                return StateChange.LOOTING;
            case AWAIT_MSG:
                if (!Widgets.search().withTextContains("shrine").result().isEmpty())
                    state = State.RETURN_TO_ZULANDRA;
                return StateChange.LOOTING;
            case RETURN_TO_ZULANDRA:
                RBApi.sendKeystroke('1');
                timeout = 3;
                state = State.SWAP_GEAR;
                return StateChange.LOOTING;
            case SWAP_GEAR:
                if (Inventory.search().nameContainsInsensitive(RBApi.configCSVToHashSet(config.mageSwitch()).stream().findFirst().orElseThrow().replace("*", "")).first().isPresent()) {
                    swapGearAndPrayer(GearType.MAGE);
                    timeout = 3;
                }
                state = State.RESTART_KILL;
                return StateChange.LOOTING;
            case RESTART_KILL:
                disablePrayers();
                return StateChange.START_KILL;
        }

        if (!startUp) {
            if (gearType == GearType.RANGED) swapGearAndPrayer(GearType.MAGE);
            startUp = true;
        }

        Optional<ETileItem> eItem = TileItems.search().first();
        if (eItem.isPresent())
        {
            if (!startedLooting)
            {
                disablePrayers();
                int emptySlots = 28 - Inventory.getEmptySlots();
                if (emptySlots < 3) InventoryItems.makeInvSpace(3 - emptySlots);

            }
            TileItemPackets.queueTileItemAction(eItem.get(), false);
            startedLooting = true;
        }

        if (startedLooting && eItem.isEmpty()) {
            if (config.minFood() > InventoryItems.getFoodInInv()) return StateChange.BANKING;
            else state = State.INTERACT;
        }

        return StateChange.LOOTING;
    }

    private enum State
    {
        LOOTING,
        INTERACT,
        AWAIT_MSG,
        RETURN_TO_ZULANDRA,
        SWAP_GEAR,
        AWAIT_GEAR_SWAP_LOL,
        RESTART_KILL
    }
}
