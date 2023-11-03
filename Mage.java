package net.runelite.client.plugins.autozulrah;

import com.example.RuneBotApi.Movement;

public class Mage extends ZulrahController {

    private State state;
    private int stutterCounter = 0;
    private boolean stutterAttack = false;

    Mage()
    {
        state = State.SWITCH_PHASE_ONE;
    }
    @Override
    StateChange eventLoop()
    {
//        StateChange eventLoopState = super.eventLoop();
//        if (eventLoopState == StateChange.LOOTING) return StateChange.LOOTING;
//        if (eventLoopState != null) return StateChange.MAGE;
        switch (super.eventLoop())
        {
            case LOOTING: return StateChange.LOOTING;
            case BANKING: return StateChange.BANKING;
            case TIMEOUT: return StateChange.MAGE;
        }

        switch (eventControl)
        {
            case EXEC: return StateChange.MAGE;
            case RESET:
            case YIELD:
        }

        switch (state)
        {
            case SWITCH_PHASE_ONE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 2;
                state = State.ATTACK_PHASE_ONE;
            break; case ATTACK_PHASE_ONE:
                attackZulrah();
                timeout = 20;
                state = State.STUTTER_SOUTH;
            break; case STUTTER_SOUTH:
                if (stutterCounter++ == 6) {
                    state = State.MOVE_PHASE_TWO;
                    break;
                }
                if (stutterAttack) attackZulrah();
                else Movement.moveRelative(0, -2);
                stutterAttack = !stutterAttack;
            break; case MOVE_PHASE_TWO:
                Movement.moveRelative(-10, 1);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 5;
                state = State.ATTACK_PHASE_TWO;
            break; case ATTACK_PHASE_TWO:
                attackZulrah();
                timeout = 16;
                state = State.SWAP_MAGE_TWO;
            break; case SWAP_MAGE_TWO:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 7;
                state = State.ATTACK_PHASE_THREE;
            break; case ATTACK_PHASE_THREE:
                attackZulrah();
                timeout = 23;
                state = State.REPOSITION_PHASE_FOUR;
            break; case REPOSITION_PHASE_FOUR:
                Movement.moveRelative(9, 0);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 7;
                state = State.ATTACK_PHASE_FOUR;
            break; case ATTACK_PHASE_FOUR:
                attackZulrah();
                timeout = 28;
                state = State.ATTACK_PHASE_FIVE;
            break; case ATTACK_PHASE_FIVE:
                attackZulrah();
                timeout = 18;
                state = State.ATTACK_PHASE_SIX;
            break; case ATTACK_PHASE_SIX:
                attackZulrah();
                timeout = 3;
                state = State.REPOSITION_PHASE_SEVEN;
            break; case REPOSITION_PHASE_SEVEN:
                Movement.moveRelative(-9, -1);
                timeout = 6;
                state = State.REATTACK_PHASE_SIX;
            break; case REATTACK_PHASE_SIX:
                attackZulrah();
                timeout = 18;
                state = State.SWAP_MAGE_THREE;
            break; case SWAP_MAGE_THREE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 3;
                state = State.ATTACK_PHASE_SEVEN;
            break; case ATTACK_PHASE_SEVEN:
                attackZulrah();
                timeout = 26;
                state = State.REPOSITION_PHASE_EIGHT;
            break; case REPOSITION_PHASE_EIGHT:
                Movement.moveRelative(9, 0);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 5;
                state = State.ATTACK_PHASE_EIGHT;
            break; case ATTACK_PHASE_EIGHT:
                attackZulrah();
                timeout = 16;
                state = State.SWAP_PHASE_NINE;
            break; case SWAP_PHASE_NINE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 3;
                state = State.ATTACK_PHASE_NINE;
            break; case ATTACK_PHASE_NINE:
                attackZulrah();
                timeout = 22;
                state = State.SWAP_FOR_JAD;
            break; case SWAP_FOR_JAD:
                swapGearAndPrayer(GearType.MAGE);
                timeout = 3;
                state = State.PRAY_FLICK_JAD;
            break; case PRAY_FLICK_JAD:
                if (prayAgainstJad(JadPrayerType.MAGE, 24))
                    state = State.SWAP_PHASE_ELEVEN;
            break; case SWAP_PHASE_ELEVEN:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 4;
                state = State.ATTACK_PHASE_ELEVEN;
            break; case ATTACK_PHASE_ELEVEN:
                attackZulrah();
                timeout = 6;
                state = State.SWAP_FOR_START;
            break; case SWAP_FOR_START:
                swapGearAndPrayer(GearType.MAGE);
                timeout = 3;
                state = State.RETURN_STARTKILL;
            break; case RETURN_STARTKILL:
                return StateChange.LOOP;
        }

        return StateChange.MAGE;
    }

    private enum State
    {
        SWITCH_PHASE_ONE,
        ATTACK_PHASE_ONE,
        STUTTER_SOUTH,
        MOVE_PHASE_TWO,
        ATTACK_PHASE_TWO,
        SWAP_MAGE_TWO,
        SWAP_PHASE_FOUR,
        ATTACK_PHASE_THREE,
        REPOSITION_PHASE_FOUR,
        ATTACK_PHASE_FOUR,
        ATTACK_PHASE_FIVE,
        ATTACK_PHASE_SIX,
        REPOSITION_PHASE_SEVEN,
        REATTACK_PHASE_SIX,
        SWAP_MAGE_THREE,
        ATTACK_PHASE_SEVEN,
        REPOSITION_PHASE_EIGHT,
        ATTACK_PHASE_EIGHT,
        SWAP_PHASE_NINE,
        ATTACK_PHASE_NINE,
        SWAP_FOR_JAD,
        PRAY_FLICK_JAD,
        SWAP_PHASE_ELEVEN,
        ATTACK_PHASE_ELEVEN,
        RETURN_STARTKILL,
        SWAP_FOR_START

    }
}
