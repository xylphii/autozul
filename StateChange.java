package net.runelite.client.plugins.autozulrah;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum StateChange {
    BANKING(Banking.class),
    RANGE(Range.class),
    MAGE(Mage.class),
    MELEE_SOUTH(MeleeSouth.class),
    MELEE_WEST(MeleeWest.class),
    START_KILL(StartKill.class),
    LOOP(LoopHelper.class),
    DEAD(Dead.class),
    TIMEOUT(Class.class),
    CONT(Class.class),
    LOOTING(Looting.class);

    private final Class clazz;
}
