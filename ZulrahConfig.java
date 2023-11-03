package net.runelite.client.plugins.autozulrah;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import javax.swing.*;

@ConfigGroup("Zulrah")
public interface ZulrahConfig extends Config {

    @ConfigItem(
            keyName = "exportWornGear",
            name = "Export Gear",
            description = "exports gear as csv for mage/range gear config",
            position = 2
    )
    default JMenuItem exportGear()
    {
        JMenuItem exportButton = new JMenuItem("Export Gear");
        exportButton.addActionListener((e) ->
        {
            System.out.println("awoo!");
        });
        return exportButton;
    }

    @ConfigItem(
            keyName = "mageSwitch",
            name = "Mage Switch",
            description = "what is your mage gear?",
            position = 2
    )
    default String mageSwitch()
    {
        return "";
    }

    @ConfigItem(
            keyName = "rangeSwitch",
            name = "Range Switch",
            description = "what is your range gear?",
            position = 3
    )
    default String rangeSwitch()
    {
        return "";
    }

    @ConfigItem(
            keyName = "inventoryItems",
            name = "Inventory Items",
            description = "super restore(4): 2, Teleport to house: ab1, shark: all",
            position = 4
    )
    default String inventoryItems()
    {
        return "";
    }

    @ConfigItem(
            keyName = "rigour",
            name = "Use rigour?",
            description = ":3",
            position = 5
    )
    default boolean rigour() {
        return false;
    }
    @ConfigItem(
            keyName = "augury",
            name = "Use augury?",
            description = "^_^",
            position = 6
    )
    default boolean augury() {
        return false;
    }

    @ConfigItem(
            keyName = "repotRanged",
            name = "Repot ranged level",
            description = "what lvl to repot ranged at",
            position = 7
    )
    default int rangedThreshold() {
        return 110;
    }

    @ConfigItem(
            keyName = "killAgain",
            name = "Minimum food to kill again",
            description = "How much food to attempt another kill in the same trip?",
            position = 8
    )
    default int minFood() {
        return 10;
    }

}
