package net.runelite.client.plugins.autozulrah;

import com.example.RuneBotApi.Movement;

public class MeleeSouth extends ZulrahController {

    private State state;
    private int stutterCounter = 0;
    private boolean stutterAttack = true;

    MeleeSouth()
    {
        state = State.ATTACK_PHASE_ONE;
    }
    @Override
    StateChange eventLoop()
    {
//        StateChange eventLoopState = super.eventLoop();
//        if (eventLoopState == StateChange.LOOTING) return StateChange.LOOTING;
//        if (eventLoopState != null) return StateChange.MELEE_SOUTH;
        switch (super.eventLoop())
        {
            case LOOTING: return StateChange.LOOTING;
            case BANKING: return StateChange.BANKING;
            case TIMEOUT: return StateChange.MELEE_SOUTH;
        }

        switch (eventControl)
        {
            case EXEC: return StateChange.MELEE_SOUTH;
            case RESET:
            case YIELD:
        }

        switch (state)
        {
            case ATTACK_PHASE_ONE:
                attackZulrah();
                timeout = 39;
                state = State.ATTACK_PHASE_TWO;
            break; case ATTACK_PHASE_TWO:
                attackZulrah();
                timeout = 14;
                state = State.SWAP_FOR_MAGE;
            break; case SWAP_FOR_MAGE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 6;
                state = State.ATTACK_MAGE;
            break; case ATTACK_MAGE:
                attackZulrah();
                timeout = 11;
                state = State.REPOSITION_AFTER_MAGE;
            break; case REPOSITION_AFTER_MAGE:
                Movement.moveRelative(10, -1);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 8;
                state = State.ATTACK_PHASE_THREE;
            break; case ATTACK_PHASE_THREE:
                attackZulrah();
                timeout = 24;
                state = State.SWAP_FOR_MAGE_TWO;
            break; case SWAP_FOR_MAGE_TWO:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 3;
                state = State.ATTACK_MAGE_TWO;
            break; case ATTACK_MAGE_TWO:
                attackZulrah();
                timeout = 7;
                state = State.MOVE_TO_STUTTER;
            break; case MOVE_TO_STUTTER:
                Movement.moveRelative(-2, -1);
                state = State.STUTTER_WEST;
            break; case STUTTER_WEST:
                if (stutterCounter++ == 9) {
                    state = State.REATTACK_PHASE_FOUR;
                    break;
                }
                if (stutterAttack) attackZulrah();
                else Movement.moveRelative(-2, 0);
                stutterAttack = !stutterAttack;
            break; case REATTACK_PHASE_FOUR:
                attackZulrah();
                timeout = 12;
                state = State.SWAP_PHASE_FIVE;
            break; case SWAP_PHASE_FIVE:
                swapGearAndPrayer(GearType.MAGE);
                timeout = 3;
                state = State.SWAP_JAD_PRAYERS;
            break; case SWAP_JAD_PRAYERS:
                if (prayAgainstJad(JadPrayerType.RANGED, 27))
                    state = State.REPOSITION_PHASE_SEVEN;
            break; case REPOSITION_PHASE_SEVEN:
                Movement.moveRelative(10, 7);
                timeout = 19;
                state = State.ATTACK_PHASE_SEVEN;
            break; case ATTACK_PHASE_SEVEN:
                attackZulrah();
                state = State.DODGE_ONE;
            break; case DODGE_ONE:
                Movement.moveRelative(1, 0);
                state = State.ATTACK_AFTER_DODGE_ONE;
            break; case ATTACK_AFTER_DODGE_ONE:
                attackZulrah();
                state = State.DODGE_TWO;
                timeout = 6;
            break; case DODGE_TWO:
                Movement.moveRelative(-2, 1);
                state = State.ATTACK_AFTER_DODGE_TWO;
                timeout = 1;
            break; case ATTACK_AFTER_DODGE_TWO:
                attackZulrah();
                timeout = 2;
                state = State.RETURN_STARTKILL;
            break; case RETURN_STARTKILL:
                return StateChange.LOOP;
        }

        return StateChange.MELEE_SOUTH;
    }

    private enum State
    {
        ATTACK_PHASE_ONE,
        ATTACK_PHASE_TWO,
        SWAP_FOR_MAGE,
        ATTACK_MAGE,
        REPOSITION_AFTER_MAGE,
        ATTACK_PHASE_THREE,
        SWAP_FOR_MAGE_TWO,
        ATTACK_MAGE_TWO,
        MOVE_TO_STUTTER,
        STUTTER_WEST,
        REPOSITION_PHASE_FIVE,
        REATTACK_PHASE_FOUR,
        SWAP_PHASE_FIVE,
        START_JAD_PHASE,
        SWAP_JAD_PRAYERS,
        REPOSITION_PHASE_SEVEN,
        ATTACK_PHASE_SEVEN,
        DODGE_ONE,
        ATTACK_AFTER_DODGE_ONE,
        DODGE_TWO,
        ATTACK_AFTER_DODGE_TWO,
        RETURN_STARTKILL

    }
}
