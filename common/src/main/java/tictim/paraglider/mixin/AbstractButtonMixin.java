package tictim.paraglider.mixin;

import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tictim.paraglider.util.ButtonExtension;
import tictim.paraglider.util.Tooltip;

@Mixin(AbstractButton.class)
public class AbstractButtonMixin implements ButtonExtension {
    @Unique
    private Tooltip tooltip;

    @Override
    public void setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Tooltip getTooltip() {
        return this.tooltip;
    }
}
