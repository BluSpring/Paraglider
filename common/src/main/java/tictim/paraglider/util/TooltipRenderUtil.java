package tictim.paraglider.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class TooltipRenderUtil {
    public static final int MOUSE_OFFSET = 12;
    private static final int PADDING = 3;
    public static final int PADDING_LEFT = 3;
    public static final int PADDING_RIGHT = 3;
    public static final int PADDING_TOP = 3;
    public static final int PADDING_BOTTOM = 3;
    private static final int BACKGROUND_COLOR = -267386864;
    private static final int BORDER_COLOR_TOP = 0x505000FF;
    private static final int BORDER_COLOR_BOTTOM = 1344798847;

    public static void renderTooltipBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, int z) {
        int i = x - 3;
        int j = y - 3;
        int k = width + 3 + 3;
        int l = height + 3 + 3;
        TooltipRenderUtil.renderHorizontalLine(guiGraphics, i, j - 1, k, z, -267386864);
        TooltipRenderUtil.renderHorizontalLine(guiGraphics, i, j + l, k, z, -267386864);
        TooltipRenderUtil.renderRectangle(guiGraphics, i, j, k, l, z, -267386864);
        TooltipRenderUtil.renderVerticalLine(guiGraphics, i - 1, j, l, z, -267386864);
        TooltipRenderUtil.renderVerticalLine(guiGraphics, i + k, j, l, z, -267386864);
        TooltipRenderUtil.renderFrameGradient(guiGraphics, i, j + 1, k, l, z, 0x505000FF, 1344798847);
    }

    private static void renderFrameGradient(GuiGraphics guiGraphics, int x, int y, int width, int height, int z, int topColor, int bottomColor) {
        TooltipRenderUtil.renderVerticalLineGradient(guiGraphics, x, y, height - 2, z, topColor, bottomColor);
        TooltipRenderUtil.renderVerticalLineGradient(guiGraphics, x + width - 1, y, height - 2, z, topColor, bottomColor);
        TooltipRenderUtil.renderHorizontalLine(guiGraphics, x, y - 1, width, z, topColor);
        TooltipRenderUtil.renderHorizontalLine(guiGraphics, x, y - 1 + height - 1, width, z, bottomColor);
    }

    private static void renderVerticalLine(GuiGraphics guiGraphics, int x, int y, int length, int z, int color) {
        guiGraphics.fill(x, y, x + 1, y + length, z, color);
    }

    private static void renderVerticalLineGradient(GuiGraphics guiGraphics, int x, int y, int length, int z, int topColor, int bottomColor) {
        guiGraphics.fillGradient(x, y, x + 1, y + length, z, topColor, bottomColor);
    }

    private static void renderHorizontalLine(GuiGraphics guiGraphics, int x, int y, int length, int z, int color) {
        guiGraphics.fill(x, y, x + length, y + 1, z, color);
    }

    private static void renderRectangle(GuiGraphics guiGraphics, int x, int y, int width, int height, int z, int color) {
        guiGraphics.fill(x, y, x + width, y + height, z, color);
    }
}

