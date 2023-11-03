package net.runelite.client.plugins.autozulrah;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Provides;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor(
        name = "<html>[<strong><font color=#87CEFA>RB</font></strong>] Auto Zulrah</html>",
        description = "Fully automated zulrah bot",
        enabledByDefault = false,
        tags = {"rb", "RB", "zulrah", "zulrah bot"}
)
public class ZulrahPlugin extends Plugin {

    @Inject
    private ZulrahConfig config;

    @Provides
    ZulrahConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ZulrahConfig.class);
    }

    private Banking banking;
    private Mage mage;
    private MeleeWest melee;
    private MeleeSouth southRange;
    private Range eastRange;
    private StartKill startKill;
    private Dead dead;
    private Looting looting;

    protected StateController activeState;

    @Override
    protected void startUp()
    {
        // ZulrahController
        this.mage = new Mage();
        this.melee = new MeleeWest();
        this.southRange = new MeleeSouth();
        this.eastRange = new Range();
        this.startKill = new StartKill();

        // MiscController
        this.banking = new Banking();
        this.looting = new Looting();

        this.activeState = banking;

        Inventory.reloadInventory();
    }


    @Subscribe
    public void onGameTick(GameTick meow)
    {
        // if active state is startkill and login time > 5.5h, sell on ge
        setActiveState(this.activeState.eventLoop());
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged UwU)
    {
        if (UwU.getGameState() == GameState.LOGIN_SCREEN) {
            EthanApiPlugin.stopPlugin(this);
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
    }

    /**
     * maybe we could have this just reinstantiante the objects. this way we would have a fresh slate instead of
     * having to manage a branch for RESET logic
     */
    private void setActiveState(StateChange state) {

        if (this.activeState.getClass() != state.getClazz())
        {
            if (this.activeState instanceof Mage)       this.mage = new Mage();
            if (this.activeState instanceof MeleeWest)  this.melee = new MeleeWest();
            if (this.activeState instanceof Range)      this.eastRange = new Range();
            if (this.activeState instanceof MeleeSouth) this.southRange = new MeleeSouth();
            if (this.activeState instanceof StartKill)  this.startKill = new StartKill();

            // misc controllers
            if (this.activeState instanceof Banking)    this.banking = new Banking();
            if (this.activeState instanceof Looting)    this.looting = new Looting();

            // long kills
            if (state == StateChange.LOOP) {
                this.startKill = new StartKill(true);
            }
        }

        switch (state)
        {
                   case MAGE:        this.activeState = this.mage;
            break; case MELEE_WEST:  this.activeState = this.melee;
            break; case RANGE:       this.activeState = this.eastRange;
            break; case MELEE_SOUTH: this.activeState = this.southRange;
            break; case START_KILL:
                   case LOOP:        this.activeState = this.startKill;

            // misc controllers
            break; case LOOTING:     this.activeState = this.looting;
            break; case BANKING:     this.activeState = this.banking;
        }
    }
}
