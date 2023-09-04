package tictim.paraglider.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tictim.paraglider.ParagliderUtils;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class MixinDebugScreenOverlay{
	@Inject(
			method = "getSystemInformation", at = @At("RETURN")
	)
	public void onDrawSystemInformation(CallbackInfoReturnable<List<String>> cir){
		Minecraft mc = Minecraft.getInstance();
		if(mc.player==null) return;
		ParagliderUtils.addDebugText(mc.player, cir.getReturnValue());
	}
}
