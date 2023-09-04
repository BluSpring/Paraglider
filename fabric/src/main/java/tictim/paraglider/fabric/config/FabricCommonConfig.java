package tictim.paraglider.fabric.config;

import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import tictim.paraglider.config.CommonConfig;

import static tictim.paraglider.api.ParagliderAPI.MODID;

public class FabricCommonConfig extends CommonConfig{
	public FabricCommonConfig(){
		ModLoadingContext.registerConfig(MODID, ModConfig.Type.COMMON, this.spec);
	}
}
