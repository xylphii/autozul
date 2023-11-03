package net.runelite.client.plugins.autozulrah;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.RuneBotApi.LocalPlayer.LocationInformation;
import com.example.RuneBotApi.MapSquare;
import com.example.RuneBotApi.Movement;
import com.example.RuneBotApi.RBApi;
import com.example.RuneBotApi.RBRandom;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;

public class StartKill extends ZulrahController {

    private State state;
    private Phase phase;
    private MeleeState meleeState = MeleeState.WAIT_START;
    private boolean loop = false;

    StartKill()
    {
        state = State.RETURN_TO_ZULANDRA;
        phase = Phase.UNK;
    }

    StartKill(boolean loop)
    {
        state = State.AWAIT_CUTSCENE;
        phase = Phase.UNK;
        this.loop = loop;
    }

    @Override
    StateChange eventLoop()
    {
//        StateChange eventLoopState = super.eventLoop();
//        if (eventLoopState == StateChange.LOOTING) return StateChange.LOOTING;
//        if (eventLoopState != null) return StateChange.START_KILL;
        switch (super.eventLoop())
        {
            case LOOTING: return StateChange.LOOTING;
            case BANKING: return StateChange.BANKING;
            case TIMEOUT: return StateChange.START_KILL;
        }

        switch (eventControl)
        {
            case EXEC: return StateChange.START_KILL;
            case RESET:
            case YIELD:
        }

        switch (state)
        {
                   case RETURN_TO_ZULANDRA: returnToZulandra();
            break; case BOARD_BOAT: boardBoat();
            break; case AWAIT_CUTSCENE: awaitCutscene();
            break; case BEGIN_KILL: beginKill();
            break; case DETERMINE_PHASE: determinePhase();
            break; case MELEE_PHASE: meleePhase();
        }

        if (phase != Phase.UNK) {
            System.out.println("timeout!");
            timeout = 5;
        }

        switch (phase)
        {
            case UNK: return StateChange.START_KILL;
            case MAGE: return StateChange.MAGE;
            case RANGE: return StateChange.RANGE;
            case MELEE_SOUTH: return StateChange.MELEE_SOUTH;
            case MELEE_WEST: return StateChange.MELEE_WEST;
        }

        return StateChange.START_KILL;
    }

    private void returnToZulandra()
    {
        if (LocationInformation.getMapSquareId() != MapSquare.ZUL_ANDRA.getId()) {
            InventoryInteraction.useItem(Inventory.search().withId(ItemID.ZULANDRA_TELEPORT).first().orElseThrow(), "Teleport");
            timeout = 4;
        }
        //if in zul andra
        state = State.BOARD_BOAT;
    }

    private void boardBoat()
    {
        Optional<TileObject> boat = TileObjects.search().withId(10068).first();
        boat.ifPresent(daBoat -> TileObjectInteraction.interact(daBoat, "Quick-Board"));
        timeout = RBRandom.randRange(15, 17);
        state = State.AWAIT_CUTSCENE;
    }

    private void awaitCutscene()
    {
        // the template's id
        if (ArrayUtils.contains(client.getMapRegions(), MapSquare.ZULRAH.getId()))
        {
            Movement.regionMove(41, 37);
            state = State.BEGIN_KILL;
            timeout = 6;
        }
    }

    private void beginKill()
    {
        attackZulrah();
        // if splash handler maybe
        timeout = 23; // 34
        state = State.DETERMINE_PHASE;
        if (this.loop) {
            timeout += 11;
            EthanApiPlugin.sendClientMessage("In StartKill(true) from LoopHelper.class");
            return;
        }
        enablePrayers(PrayerType.MAGE);
    }

    private void determinePhase()
    {
        if (getZulrah().isPresent())
        {
            NPC zulrah = getZulrah().get();
            switch (zulrah.getId())
            {
                       case 2042: phase = Phase.RANGE;
                break; case 2043: phase = Phase.UNK;
                break; case 2044: phase = Phase.MAGE;
            }

            if (phase == Phase.UNK) {
                state = State.MELEE_PHASE;
            }
        }
    }

    private void meleePhase()
    {
        switch (meleeState)
        {
            case WAIT_START:
                timeout = 2;
                if (this.loop) { // need to manage this when we loop it seems to be messing up somehow
                    // it sems like it's skipping 1t for the first phase after the ranger?
                    timeout = 1;
                }
                meleeState = MeleeState.MELEE_START;
            case MELEE_START:
                attackZulrah();
                meleeState = MeleeState.DODGE_ONE;
                timeout = 2;
            break; case DODGE_ONE:
                Movement.moveRelative(1, 0);
                meleeState = MeleeState.ATTACK_AFTER_DODGE_ONE;
            break; case ATTACK_AFTER_DODGE_ONE:
                attackZulrah();
                meleeState = MeleeState.DODGE_TWO;
                timeout = 5;
            break; case DODGE_TWO:
                Movement.moveRelative(-2, 1);
                meleeState = MeleeState.ATTACK_AFTER_DODGE_TWO;
                timeout = 1;
            break; case ATTACK_AFTER_DODGE_TWO:
                attackZulrah();
                meleeState = MeleeState.SWAP_FOR_MAGE;
                timeout = 4;
            break; case SWAP_FOR_MAGE:
                swapGearAndPrayer(GearType.RANGED);
                meleeState = MeleeState.ATTACK_MAGE;
                timeout = 4;
            break; case ATTACK_MAGE:
                attackZulrah();
                meleeState = MeleeState.REPOSITION_FOR_NEXT_PHASE;
                timeout = 8;
            break; case REPOSITION_FOR_NEXT_PHASE:
                Movement.regionMove(31, 32);
                swapGearAndPrayer(GearType.MAGE);
                meleeState = MeleeState.GET_NEXT_PHASE;
                timeout = 8;
            break; case GET_NEXT_PHASE:
                // if we are further west than Zulrah's southwest tile
                if (LocationInformation.getTile().getX() < getZulrah().get().getWorldLocation().getX()) {
                    phase = Phase.MELEE_SOUTH;
                } else {
                    phase = Phase.MELEE_WEST;
                }

        }
    }

    private enum MeleeState
    {
        WAIT_START,
        MELEE_START,
        DODGE_ONE,
        ATTACK_AFTER_DODGE_ONE,
        DODGE_TWO,
        ATTACK_AFTER_DODGE_TWO,
        SWAP_FOR_MAGE,
        ATTACK_MAGE,
        REPOSITION_FOR_NEXT_PHASE,
        GET_NEXT_PHASE
    }

    private enum State
    {
        RETURN_TO_ZULANDRA,
        BOARD_BOAT,
        AWAIT_CUTSCENE,
        BEGIN_KILL,
        DETERMINE_PHASE,
        MELEE_PHASE,
        CENTER_MAGE,
        DETERMINE_MELEE_PHASE,
    }

    private enum Phase
    {
        MAGE,
        MELEE_SOUTH,
        MELEE_WEST,
        RANGE,
        UNK
    }
}
