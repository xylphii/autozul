package net.runelite.client.plugins.autozulrah;

import com.example.RuneBotApi.Movement;

public class Range extends ZulrahController {

    private State state;

    Range()
    {
        timeout = 3;
        state = State.ATTACK_PHASE_ONE;
    }

    @Override
    StateChange eventLoop()
    {
//        StateChange eventLoopState = super.eventLoop();
//        if (eventLoopState == StateChange.LOOTING) return StateChange.LOOTING;
//        if (eventLoopState != null) return StateChange.RANGE;
        switch (super.eventLoop())
        {
            case LOOTING: return StateChange.LOOTING;
            case BANKING: return StateChange.BANKING;
            case TIMEOUT: return StateChange.RANGE;
        }

        switch (eventControl)
        {
            case EXEC: return StateChange.RANGE;
            case RESET:
            case YIELD:
        }

        switch (state)
        {
            case ATTACK_PHASE_ONE:
                attackZulrah();
                timeout = 20;
                state = State.RUN_PHASE_TWO;
            break; case RUN_PHASE_TWO:
                Movement.moveRelative(-9, -6);
                timeout = 7;
                state = State.STUTTER_PASE_TWO;
            break; case STUTTER_PASE_TWO:
                attackZulrah();
                state = State.RUN_PHASE_TWO_SAFESPOT;
            break; case RUN_PHASE_TWO_SAFESPOT:
                Movement.moveRelative(-1, 5);
                timeout = 2;
                state = State.ATTACK_PHASE_TWO;
            break; case ATTACK_PHASE_TWO:
                attackZulrah();
                timeout = 13;
                state = State.DODGE_PHASE_TWO;
            break; case DODGE_PHASE_TWO:
                Movement.moveRelative(1, 2);
                state = State.ATTACK_AFTER_DODGE_ONE;
            break; case ATTACK_AFTER_DODGE_ONE:
                attackZulrah();
                timeout = 5;
                state = State.DODGE_PHASE_TWO_AGAIN;
            break; case DODGE_PHASE_TWO_AGAIN:
                Movement.moveRelative(-2, -3);
                timeout = 1;
                state = State.ATTACK_AFTER_SECOND_DODGE;
            break; case ATTACK_AFTER_SECOND_DODGE:
                attackZulrah();
                timeout = 7;
                state = State.SWAP_FOR_MAGE;
            break; case SWAP_FOR_MAGE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 5;
                state = State.ATTACK_MAGE;
            break; case ATTACK_MAGE:
                attackZulrah();
                timeout = 13;
                state = State.REPOSITION_PHASE_FOUR;
            break; case REPOSITION_PHASE_FOUR:
                Movement.moveRelative(11, -4);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 6;
                state = State.ATTACK_PHASE_FOUR;
            break; case ATTACK_PHASE_FOUR:
                attackZulrah();
                timeout = 15;
                state = State.SWAP_PHASE_FIVE;
            break; case SWAP_PHASE_FIVE:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 4;
                state = State.ATTACK_PHASE_FIVE;
            break; case ATTACK_PHASE_FIVE:
                attackZulrah();
                timeout = 13;
                state = State.REPOSITION_PHASE_SIX;
            break; case REPOSITION_PHASE_SIX:
                Movement.moveRelative(-10, 0);
                swapGearAndPrayer(GearType.MAGE);
                timeout = 6;
                state = State.ATTACK_PHASE_SIX;
            break; case ATTACK_PHASE_SIX:
                attackZulrah();
                timeout = 25;
                state = State.ATTACK_PHASE_SEVEN;
            break; case ATTACK_PHASE_SEVEN:
                attackZulrah();
                timeout = 12;
                state = State.REPOSITION_PHASE_EIGHT;
            break; case REPOSITION_PHASE_EIGHT:
                Movement.moveRelative(9, 0);
                swapGearAndPrayer(GearType.RANGED);
                timeout = 7;
                state = State.ATTACK_PHASE_EIGHT;
            break; case ATTACK_PHASE_EIGHT:
                attackZulrah();
                timeout = 30;
                state = State.SWAP_FOR_JAD;
            break; case SWAP_FOR_JAD:
                swapGearAndPrayer(GearType.MAGE);
                timeout = 3;
                state = State.PRAY_FLICK_JAD;
            break; case PRAY_FLICK_JAD:
                if (prayAgainstJad(JadPrayerType.MAGE, 30)) state = State.SWAP_PHASE_TEN;
            break; case SWAP_PHASE_TEN:
                swapGearAndPrayer(GearType.RANGED);
                timeout = 4;
                state = State.ATTACK_PHASE_TEN;
            break; case ATTACK_PHASE_TEN:
                attackZulrah();
                timeout = 9;
                state = State.SWAP_TO_RESTART;
            break; case SWAP_TO_RESTART:
                swapGearAndPrayer(GearType.MAGE);
                state = State.RETURN_STARTKILL;
            break; case RETURN_STARTKILL:
                return StateChange.LOOP;
        } // final check

        return StateChange.RANGE;
    }

    private enum State
    {
        ATTACK_PHASE_ONE,
        RUN_PHASE_TWO,
        STUTTER_PASE_TWO,
        RUN_PHASE_TWO_SAFESPOT,
        ATTACK_PHASE_TWO,
        DODGE_PHASE_TWO,
        ATTACK_AFTER_DODGE_ONE,
        DODGE_PHASE_TWO_AGAIN,
        ATTACK_AFTER_SECOND_DODGE,
        SWAP_FOR_MAGE,
        ATTACK_MAGE,
        REPOSITION_PHASE_FOUR,
        ATTACK_PHASE_FOUR,
        SWAP_PHASE_FIVE,
        ATTACK_PHASE_FIVE,
        REPOSITION_PHASE_SIX,
        ATTACK_PHASE_SIX,
        REPOSITION_PHASE_SEVEN,
        ATTACK_PHASE_SEVEN,
        REPOSITION_PHASE_EIGHT,
        ATTACK_PHASE_EIGHT,
        SWAP_FOR_JAD,
        PRAY_FLICK_JAD,
        SWAP_PHASE_TEN,
        ATTACK_PHASE_TEN,
        SWAP_TO_RESTART,
        RETURN_STARTKILL
    }
}
