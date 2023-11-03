package net.runelite.client.plugins.autozulrah;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.RuneBotApi.Items.Food;
import com.example.RuneBotApi.Items.PotionType;
import com.example.RuneBotApi.Items.Potions;
import com.example.RuneBotApi.LocalPlayer.LocationInformation;
import com.example.RuneBotApi.LocalPlayer.StatInformation;
import com.example.RuneBotApi.LocalPlayer.StatType;
import com.example.RuneBotApi.MapSquare;
import com.example.RuneBotApi.Npcs.NpcAction;
import com.example.RuneBotApi.RBApi;
import net.runelite.api.*;

import java.util.Objects;
import java.util.Optional;


public class ZulrahController extends StateController {


    private final int[] zulrahIds = {2042, 2043, 2044};
    private NPC zulrah = null;

    private boolean jadInit = true;
    private JadPrayerType jadOverhead;
    private int jadDuration;





    @Override
    StateChange eventLoop() {

        if (Objects.requireNonNull(state) == State.SWAPPING_GEAR) swapGearAndPrayer(gearType);

        if (!checkHp()) return StateChange.BANKING;     // Teleports if no food or prayer
        if (!checkPrayer()) return StateChange.BANKING; // Teleports if no food or prayer
        checkRanged();
        checkAntiVenom();
        checkImbuedHeart();

        getZulrahNpc().ifPresent(zul -> this.zulrah = zul);


        if (zulrah != null && zulrah.getHealthRatio() == 0) return StateChange.LOOTING;

        /*
        this is specifically to yield to timeout events for child classes
        since we want to always execute ZulrahController logic regardless
        of the state of the children
         */
        if (0 < timeout--) return StateChange.TIMEOUT;
        return StateChange.CONT;
    }

    private boolean eatFoodAndResume()
    {
        int food = Food.eatBestFood();
        System.out.println("food = " + food);
//        boolean didEat = (Food.eatBestFood() == 0);
        boolean didEat = (food == 0);
        System.out.println("didEat = " + didEat);
        if (!EthanApiPlugin.isMoving()) attackZulrah(); // we don't want to try to attack Zulrah yet if we are running to a new location
        return didEat;
    }

    protected boolean checkHp()
    {
        if (StatInformation.getHp() < hpThreshold)
        {
            if (!eatFoodAndResume()) {
                return false;
            }
            if (LocationInformation.getMapSquareId() != MapSquare.ZULRAH.getId()) {
                Optional<TileObject> boat = TileObjects.search().withId(10068).first();
                boat.ifPresent(daBoat -> TileObjectInteraction.interact(daBoat, "Quick-Board"));
            }
        }
        return true;
    }

    private boolean drinkPotionAndResume(PotionType type)
    {
        boolean didDrink = (Potions.drinkPotion(type));
        if (!EthanApiPlugin.isMoving()) attackZulrah();
        return didDrink;
    }

    private boolean checkPrayer()
    {
        if (StatInformation.getLevel(Skill.PRAYER, StatType.BOOSTED) < prayerThreshold)
        {
            boolean drank = (drinkPotionAndResume(PotionType.PRAYER) || drinkPotionAndResume(PotionType.SUPER_RESTORE));
            if (!drank) {
                return false;
            }
            if (LocationInformation.getMapSquareId() != MapSquare.ZULRAH.getId()) {
                Optional<TileObject> boat = TileObjects.search().withId(10068).first();
                boat.ifPresent(daBoat -> TileObjectInteraction.interact(daBoat, "Quick-Board"));
            }
        }
        return true;
    }

    private boolean checkRanged()
    {
        if (StatInformation.getLevel(Skill.RANGED, StatType.BOOSTED) < config.rangedThreshold()) {
            return drinkPotionAndResume(PotionType.RANGING);
        }
        if (LocationInformation.getMapSquareId() != MapSquare.ZULRAH.getId()) {
            Optional<TileObject> boat = TileObjects.search().withId(10068).first();
            boat.ifPresent(daBoat -> TileObjectInteraction.interact(daBoat, "Quick-Board"));
        }
        return true;
    }

    private boolean checkAntiVenom()
    {
        if (client.getVarpValue(102) > -38) {
            return drinkPotionAndResume(PotionType.SUPER_ANTI_VENOM);
        }
        if (LocationInformation.getMapSquareId() != MapSquare.ZULRAH.getId()) {
            Optional<TileObject> boat = TileObjects.search().withId(10068).first();
            boat.ifPresent(daBoat -> TileObjectInteraction.interact(daBoat, "Quick-Board"));
        }

        return true;
    }

    private boolean checkImbuedHeart()
    {
        // TODO: add imbued heart logic
        if (LocationInformation.getMapSquareId() != MapSquare.ZULRAH.getId()) {
            Optional<TileObject> boat = TileObjects.search().withId(10068).first();
            boat.ifPresent(daBoat -> TileObjectInteraction.interact(daBoat, "Quick-Board"));
        }

        return true;
    }

    private Optional<NPC> getZulrahNpc()
    {
        for (int id : zulrahIds)
        {
            if (NPCs.search().withId(id).first().isPresent()) return NPCs.search().withId(id).first();
        }
        return Optional.empty();
    }

    protected Optional<NPC> getZulrah()
    {
        return Optional.of(zulrah);
    }

    protected void attackZulrah()
    {
        RBApi.runOnClientThread(() -> EthanApiPlugin.sendClientMessage("in attackZulrah() at t=" + client.getTickCount()));
        this.getZulrahNpc().ifPresent(zul -> NpcAction.queueNPCAction(zul, "Attack"));
    }

    protected boolean prayAgainstJad(JadPrayerType startPrayer, int duration)
    {
        if (jadInit)
        {
            this.jadDuration = 0;
            this.jadOverhead = startPrayer;
            this.jadInit = false;
            if (jadOverhead == JadPrayerType.MAGE)
            {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 35454997, -1, -1);
            }
            return false;
        }

        if (jadDuration == 2) attackZulrah();

        if (jadDuration % 3 == 0)
        {
            if (jadOverhead == JadPrayerType.MAGE)
            {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 35454998, -1, -1);
                this.jadOverhead = JadPrayerType.RANGED;
            }
            else
            {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 35454997, -1, -1);
                this.jadOverhead = JadPrayerType.MAGE;
            }
        }

        if (jadDuration++ >= duration)
        {
            this.jadInit = true;
            return true;
        }

        return false;
    }


    protected enum JadPrayerType
    {
        RANGED,
        MAGE
    }
}
