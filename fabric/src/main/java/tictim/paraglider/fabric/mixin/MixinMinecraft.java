package tictim.paraglider.fabric.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.client.screen.ParagliderSettingScreen;
import tictim.paraglider.fabric.util.ClientPreAttackCallback;
import tictim.paraglider.mixin.KeyMappingAccessor;
import tictim.paraglider.wind.Wind;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft{
	@Shadow public ClientLevel level;

	@Shadow public abstract void setScreen(Screen screen);

	@Shadow @Final public Options options;

	@Shadow @Nullable public LocalPlayer player;

	@Shadow @Nullable public MultiPlayerGameMode gameMode;

	@Inject(at = @At("HEAD"), method = "handleKeybinds()V")
	public void onHandleKeybinds(CallbackInfo info){
		if(Screen.hasControlDown()){
			// I won't bow down to you and your evil scheme to prevent me from using CTRL+P you satanic company that goes by the name of Mojang
			// replace paraglider settings keymapping instance with registered keymapping instance with identical input key
			// to bypass keybinding collision (which most likely would happen because P is already occupied by
			// social interactions key added by vanilla Minecraft. wtf does social interactions key do anyway????)
			InputConstants.Key key = ParagliderUtils.getKey(ParagliderMod.instance().getParagliderSettingsKey());
			KeyMapping keyMapping = KeyMapping.MAP.get(key);
			if(keyMapping==null) return;
			if(keyMapping.consumeClick()){
				// shut up intellij idea
				//noinspection StatementWithEmptyBody
				while(keyMapping.consumeClick()) ; // clear all clicks, idk whatever
				setScreen(new ParagliderSettingScreen());
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "setLevel(Lnet/minecraft/client/multiplayer/ClientLevel;)V")
	public void onSetLevel(ClientLevel level, CallbackInfo info){
		Wind.registerLevel(level);
		if(this.level!=null) Wind.unregisterLevel(this.level);
	}

	@Inject(at = @At("HEAD"), method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V")
	public void onClearLevel(Screen screen, CallbackInfo info){
		if(this.level!=null) Wind.unregisterLevel(this.level);
	}

	private boolean fabric_attackCancelled;

	@Inject(
			method = "handleKeybinds",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z",
					ordinal = 0
			)
	)
	private void injectHandleInputEventsForPreAttackCallback(CallbackInfo ci) {
		int attackKeyPressCount = ((KeyMappingAccessor) options.keyAttack).getClickCount();

		if (options.keyAttack.consumeClick() || attackKeyPressCount != 0) {
			fabric_attackCancelled = ClientPreAttackCallback.EVENT.invoker().onClientPlayerPreAttack(
					(Minecraft) (Object) this, player, attackKeyPressCount
			);
		} else {
			fabric_attackCancelled = false;
		}
	}

	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void injectDoAttackForCancelling(CallbackInfoReturnable<Boolean> cir) {
		if (fabric_attackCancelled) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void injectHandleBlockBreakingForCancelling(boolean breaking, CallbackInfo ci) {
		if (fabric_attackCancelled) {
			if (gameMode != null) {
				gameMode.stopDestroyBlock();
			}

			ci.cancel();
		}
	}
}
