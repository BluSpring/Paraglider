package tictim.paraglider.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(value=EnvType.CLIENT)
public class Tooltip
        implements NarrationSupplier {
    private static final int MAX_WIDTH = 170;
    private final Component message;
    @Nullable
    private List<FormattedCharSequence> cachedTooltip;
    @Nullable
    private final Component narration;

    private Tooltip(Component message, @Nullable Component narration) {
        this.message = message;
        this.narration = narration;
    }

    public static Tooltip create(Component message, @Nullable Component narration) {
        return new Tooltip(message, narration);
    }

    public static Tooltip create(Component message) {
        return new Tooltip(message, message);
    }

    public Component getMessage() {
        return this.message;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        if (this.narration != null) {
            narrationElementOutput.add(NarratedElementType.HINT, this.narration);
        }
    }

    public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
        if (this.cachedTooltip == null) {
            this.cachedTooltip = Tooltip.splitTooltip(minecraft, this.message);
        }
        return this.cachedTooltip;
    }

    public static List<FormattedCharSequence> splitTooltip(Minecraft minecraft, Component message) {
        return minecraft.font.split(message, 170);
    }
}
