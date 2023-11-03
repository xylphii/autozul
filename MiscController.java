package net.runelite.client.plugins.autozulrah;

import com.example.RuneBotApi.RbBanker.RbBankController;

import java.util.Objects;

public class MiscController extends StateController {

    protected RbBankController bankController = new RbBankController(config.inventoryItems());

    @Override
    StateChange eventLoop() {

        if (Objects.requireNonNull(state) == State.SWAPPING_GEAR)
            swapGearAndPrayer(gearType);

        if (0 < timeout--) return StateChange.TIMEOUT;

        // if state controller = startkill and login time > 5.5h, return "sell"
        return null;
    }
}
