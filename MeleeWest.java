package net.runelite.client.plugins.autozulrah;

import com.example.RuneBotApi.Movement;

public class MeleeWest extends ZulrahController {

    private State state;
    private int stutterCounter = 0;
    private boolean stutterAttack = false;

    MeleeWest()
    {
        state = State.ATTACK_PHASE_ONE;
    }

    @Override
    StateChange eventLoop()
    {
//        StateChange eventLoopState = super.eventLoop();
//        if (eventLoopState == StateChange.LOOTING) return StateChange.LOOTING;
//        if (eventLoopState != null) return StateChange.MELEE_WEST;
        switch (super.eventLoop())
        {
            case LOOTING: return StateChange.LOOTING;
            case BANKING: return StateChange.BANKING;
            case TIMEOUT: return StateChange.MELEE_WEST;
        }

        switch (eventControl)
        {
            case EXEC: return StateChange.MELEE_WEST;
            case RESET:
            case YIELD:
        }

        switch (state)
        {
            case ATTACK_PHASE_ONE:
                attackZulrah();
                timeout = 22;
                state = State.SWAP_FOR_MAGE;
            break; case SWAP_FOR_MAGE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 4;
                state = State.ATTACK_PHASE_TWO;
            break; case ATTACK_PHASE_TWO:
                attackZulrah();
                timeout = 14;
                state = State.STUTTER_EAST;
            break; case STUTTER_EAST:
                if (stutterCounter++ == 8) {
                    state = State.RUN_MELEE_SAFESPOT;
                    break;
                }
                if (stutterAttack) attackZulrah();
                    else Movement.moveRelative(2, 0);
                stutterAttack = !stutterAttack;
            break; case RUN_MELEE_SAFESPOT:
                Movement.moveRelative(1, 7);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 15;
                state = State.ATTACK_PHASE_THREE;
            break; case ATTACK_PHASE_THREE:
                attackZulrah();
                timeout = 2;
                state = State.DODGE_ONE;
            break; case DODGE_ONE:
                Movement.moveRelative(1, 0);
                state = State.ATTACK_AFTER_DODGE_ONE;
            break; case ATTACK_AFTER_DODGE_ONE:
                attackZulrah();
                state = State.DODGE_TWO;
                timeout = 5;
            break; case DODGE_TWO:
                Movement.moveRelative(-2, 1);
                state = State.ATTACK_AFTER_DODGE_TWO;
                timeout = 1;
            break; case ATTACK_AFTER_DODGE_TWO:
                attackZulrah();
                state = State.REPOSITION_PHASE_FOUR;
                timeout = 3;
            break; case REPOSITION_PHASE_FOUR:
                Movement.moveRelative(0, -7);
                timeout = 6;
                state = State.ATTACK_PHASE_FOUR;
            break; case ATTACK_PHASE_FOUR:
                attackZulrah();
                timeout = 15;
                state = State.REPOSITION_PHASE_FIVE;
            break; case REPOSITION_PHASE_FIVE:
                Movement.moveRelative(-8, 0);
                swapGearAndPrayer(GearType.RANGED);
                timeout = 4;
                state = State.ATTACK_PHASE_FIVE;
            break; case ATTACK_PHASE_FIVE:
                attackZulrah();
                timeout = 30;
                state = State.SWAP_FOR_JAD;
            break; case SWAP_FOR_JAD:
                swapGearAndPrayer(GearType.MAGE);
                timeout = 4;
                state = State.SWAP_JAD_PRAYERS;
            break; case SWAP_JAD_PRAYERS:
                if (prayAgainstJad(JadPrayerType.RANGED, 27))
                    state = State.REPOSITION_PHASE_SEVEN;
            break; case REPOSITION_PHASE_SEVEN:
                Movement.regionMove(41, 37);
                timeout = 19;
                state = State.MELEE_START;
            break; case MELEE_START:
                attackZulrah();
                state = State.MELEE_DODGE_ONE;
            break; case MELEE_DODGE_ONE:
                Movement.moveRelative(1, 0);
                state = State.MELEE_ATTACK_AFTER_DODGE_ONE;
            break; case MELEE_ATTACK_AFTER_DODGE_ONE:
                attackZulrah();
                state = State.MELEE_DODGE_TWO;
                timeout = 7;
            break; case MELEE_DODGE_TWO:
                Movement.moveRelative(-2, 1);
                state = State.MELEE_ATTACK_AFTER_DODGE_TWO;
                timeout = 1;
            break; case MELEE_ATTACK_AFTER_DODGE_TWO:
                attackZulrah();
                timeout = 1;
                state = State.RETURN_STARTKILL;
            break; case RETURN_STARTKILL:
                return StateChange.LOOP;
        }

        return StateChange.MELEE_WEST;
    }

    private enum State
    {
        ATTACK_PHASE_ONE,
        SWAP_FOR_MAGE,
        ATTACK_PHASE_TWO,
        STUTTER_EAST,
        RUN_MELEE_SAFESPOT,
        ATTACK_PHASE_THREE,
        DODGE_ONE,
        ATTACK_AFTER_DODGE_ONE,
        DODGE_TWO,
        ATTACK_AFTER_DODGE_TWO,
        REPOSITION_PHASE_FOUR,
        ATTACK_PHASE_FOUR,
        REPOSITION_PHASE_FIVE,
        ATTACK_PHASE_FIVE,
        SWAP_FOR_JAD,
        SWAP_JAD_PRAYERS,
        REPOSITION_PHASE_SEVEN,
        MELEE_START,
        MELEE_DODGE_ONE,
        MELEE_ATTACK_AFTER_DODGE_ONE,
        MELEE_DODGE_TWO,
        MELEE_ATTACK_AFTER_DODGE_TWO,
        RETURN_STARTKILL
    }
}
