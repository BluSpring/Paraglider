package tictim.paraglider.fabric.config;

import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.config.StateMapConfig;
import tictim.paraglider.impl.movement.PlayerStateMap;

import static tictim.paraglider.api.ParagliderAPI.MODID;

public class FabricStateMapConfig extends StateMapConfig{
	public FabricStateMapConfig(@NotNull PlayerStateMap originalStateMap){
		super(originalStateMap);
		ModLoadingContext.registerConfig(MODID, ModConfig.Type.COMMON, this.spec, FILENAME);
		ModConfigEvents.reloading(MODID).register(cfg -> {
			if(cfg.getSpec()==this.spec) scheduleReload();
		});
	}
}
