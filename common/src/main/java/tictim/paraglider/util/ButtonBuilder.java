package tictim.paraglider.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ButtonBuilder {
    private final Component message;
    private final Button.OnPress onPress;
    @Nullable
    private Tooltip tooltip;
    private int x;
    private int y;
    private int width = 150;
    private int height = 20;

    public ButtonBuilder(Component message, Button.OnPress onPress) {
        this.message = message;
        this.onPress = onPress;
    }

    public ButtonBuilder pos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ButtonBuilder width(int width) {
        this.width = width;
        return this;
    }

    public ButtonBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ButtonBuilder bounds(int x, int y, int width, int height) {
        return this.pos(x, y).size(width, height);
    }

    public ButtonBuilder tooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public Button build() {
        Button button = new Button(this.x, this.y, this.width, this.height, this.message, this.onPress, (btn, poseStack, x, y) -> {
            Minecraft.getInstance().font.draw(poseStack, ((ButtonExtension) btn).getTooltip().getMessage(), x, y, 16777215);
        });
        ((ButtonExtension) button).setTooltip(this.tooltip);
        return button;
    }
}