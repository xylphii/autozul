package net.runelite.client.plugins.autozulrah;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.RuneBotApi.LocalPlayer.StatInformation;
import com.example.RuneBotApi.LocalPlayer.StatType;
import com.example.RuneBotApi.RBApi;
import com.example.RuneBotApi.RBRandom;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.HashSet;

abstract class StateController {

    @Inject
    protected ZulrahConfig config;
    @Provides
    ZulrahConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ZulrahConfig.class);
    }
    protected final Client client = RBApi.getClient();

    protected State state = State.NONE;
    protected EventControl eventControl = EventControl.YIELD; // States for sub controllers
    protected GearType gearType = GearType.MAGE;
    private final ItemManager itemManager = RBApi.getItemManager();
    private int gearIndex = 0;


    protected int prayerThreshold = 15;
    protected int hpThreshold = StatInformation.getLevel(Skill.HITPOINTS, StatType.BASE) - 25;
    protected int timeout = 0;

    abstract StateChange eventLoop();

    StateController() {
        this.config = getConfig(RBApi.getConfigManager());
    }

    protected void disablePrayers() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 35454999, -1, -1);
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 35454999, -1, -1);
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 35454985, -1, -1);
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 35454985, -1, -1);
    }


    protected void swapGearAndPrayer(GearType type)
    {
        state = State.SWAPPING_GEAR;
        eventControl = EventControl.EXEC;

        this.gearType = type;

        HashSet<String> gear = (type == GearType.MAGE) ? RBApi.configCSVToHashSet(config.mageSwitch())
                : RBApi.configCSVToHashSet(config.rangeSwitch());


        switchGear(type, gear, gear.size());
    }

    private void switchGear(GearType type, HashSet<String> gear, int size)
    {
        ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
        if (container == null) return;

        for (int i = 0, inventoryIndex = 0; i < RBRandom.randRange(3, 6) && inventoryIndex <= 28; ++inventoryIndex)
        {

            Item item = container.getItem(inventoryIndex);
            if (item == null) continue;

            String itemName = itemManager.getItemComposition(item.getId()).getName();

            if (RBApi.configMatcher(gear, itemName))
            {
                if (!Inventory.search().withId(item.getId()).withAction("Wear").result().isEmpty())
                    InventoryInteraction.useItem(item.getId(), "Wear");
                else
                    InventoryInteraction.useItem(item.getId(), "Wield");

                ++i;
                ++this.gearIndex;
            }
        }

        if (gearIndex >= size)
        {
            forceEquipAll(gear);
            if (type == GearType.RANGED) enablePrayers(PrayerType.RANGED);
            else enablePrayers(PrayerType.MAGE);
            this.gearIndex = 0;
            this.state = State.NONE;
            this.eventControl = EventControl.YIELD;
        }
    }

    private void forceEquipAll(HashSet<String> gear)
    {
        ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
        if (container == null) return;

        for (int inventoryIndex = 0; inventoryIndex <= 28; ++inventoryIndex)
        {

            Item item = container.getItem(inventoryIndex);
            if (item == null) continue;

            String itemName = itemManager.getItemComposition(item.getId()).getName();

            if (RBApi.configMatcher(gear, itemName))
            {
                if (!Inventory.search().withId(item.getId()).withAction("Wear").result().isEmpty())
                    InventoryInteraction.useItem(item.getId(), "Wear");
                else
                    InventoryInteraction.useItem(item.getId(), "Wield");

                ++this.gearIndex;
            }
        }
    }

    /**
     * this is kinda annoying since overhead and offensive prayers are always opposite
     * enablePrayers refers to the offensive prayer type
     */
    protected void enablePrayers(PrayerType type)
    {
        int offensiveId;
        int defensiveId;
        if (type == PrayerType.MAGE)  {
            offensiveId = config.augury() ? 35455012 : 35455008;
            defensiveId = 35454998;
        } else {
            offensiveId = config.rigour() ? 35455009 : 35455005;
            defensiveId = 35454997;
        }

        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, offensiveId, -1, -1);
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, defensiveId, -1, -1);
    }


    protected enum State
    {
        SWAPPING_GEAR,
        NONE
    }

    protected enum EventControl
    {
        YIELD,
        EXEC,
        RESET
    }


    protected enum GearType
    {
        MAGE,
        RANGED
    }

    protected enum PrayerType
    {
        RANGED,
        MAGE
    }
}
