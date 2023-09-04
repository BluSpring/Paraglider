package tictim.paraglider.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import tictim.paraglider.client.util.Matrix4fAccess;

import java.util.*;
import java.util.stream.Collectors;

@Environment(value=EnvType.CLIENT)
public class GuiGraphics {
    public static final float MAX_GUI_Z = 10000.0f;
    public static final float MIN_GUI_Z = -10000.0f;
    private static final int EXTRA_SPACE_AFTER_FIRST_TOOLTIP_LINE = 2;
    private final Minecraft minecraft;
    private final PoseStack pose;
    private final MultiBufferSource.BufferSource bufferSource;
    private final ScissorStack scissorStack = new ScissorStack();
    private boolean managed;

    private GuiGraphics(Minecraft minecraft, PoseStack pose, MultiBufferSource.BufferSource bufferSource) {
        this.minecraft = minecraft;
        this.pose = pose;
        this.bufferSource = Objects.requireNonNullElseGet(bufferSource, () -> minecraft.renderBuffers().bufferSource());
    }

    public GuiGraphics(Minecraft minecraft, MultiBufferSource.BufferSource bufferSource) {
        this(minecraft, new PoseStack(), bufferSource);
    }

    public GuiGraphics(PoseStack stack, MultiBufferSource.BufferSource bufferSource) {
        this(Minecraft.getInstance(), stack, bufferSource);
    }

    public GuiGraphics(PoseStack stack) {
        this(stack, null);
    }

    @Deprecated
    public void drawManaged(Runnable runnable) {
        this.flush();
        this.managed = true;
        runnable.run();
        this.managed = false;
        this.flush();
    }

    @Deprecated
    private void flushIfUnmanaged() {
        if (!this.managed) {
            this.flush();
        }
    }

    @Deprecated
    private void flushIfManaged() {
        if (this.managed) {
            this.flush();
        }
    }

    public int guiWidth() {
        return this.minecraft.getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return this.minecraft.getWindow().getGuiScaledHeight();
    }

    public PoseStack pose() {
        return this.pose;
    }

    public MultiBufferSource.BufferSource bufferSource() {
        return this.bufferSource;
    }

    public void flush() {
        RenderSystem.disableDepthTest();
        this.bufferSource.endBatch();
        RenderSystem.enableDepthTest();
    }

    public void setColor(float red, float green, float blue, float alpha) {
        this.flushIfManaged();
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public void fill(int minX, int minY, int maxX, int maxY, int color) {
        this.fill(minX, minY, maxX, maxY, 0, color);
    }

    public void fill(int minX, int minY, int maxX, int maxY, int z, int color) {
        this.fill(RenderType.translucentNoCrumbling(), minX, minY, maxX, maxY, z, color);
    }

    public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int color) {
        this.fill(renderType, minX, minY, maxX, maxY, 0, color);
    }

    public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int z, int color) {
        int i;
        Matrix4f matrix4f = this.pose.last().pose();
        if (minX < maxX) {
            i = minX;
            minX = maxX;
            maxX = i;
        }
        if (minY < maxY) {
            i = minY;
            minY = maxY;
            maxY = i;
        }
        float f = (float)FastColor.ARGB32.alpha(color) / 255.0f;
        float g = (float)FastColor.ARGB32.red(color) / 255.0f;
        float h = (float)FastColor.ARGB32.green(color) / 255.0f;
        float j = (float)FastColor.ARGB32.blue(color) / 255.0f;
        VertexConsumer vertexConsumer = this.bufferSource.getBuffer(renderType);
        vertexConsumer.vertex(matrix4f, minX, minY, z).color(g, h, j, f).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        vertexConsumer.vertex(matrix4f, minX, maxY, z).color(g, h, j, f).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        vertexConsumer.vertex(matrix4f, maxX, maxY, z).color(g, h, j, f).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        vertexConsumer.vertex(matrix4f, maxX, minY, z).color(g, h, j, f).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        this.flushIfUnmanaged();
    }

    public void fillGradient(int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        this.fillGradient(x1, y1, x2, y2, 0, colorFrom, colorTo);
    }

    public void fillGradient(int x1, int y1, int x2, int y2, int z, int colorFrom, int colorTo) {
        this.fillGradient(RenderType.translucentNoCrumbling(), x1, y1, x2, y2, colorFrom, colorTo, z);
    }

    public void fillGradient(RenderType renderType, int x1, int y1, int x2, int y2, int colorFrom, int colorTo, int z) {
        VertexConsumer vertexConsumer = this.bufferSource.getBuffer(renderType);
        this.fillGradient(vertexConsumer, x1, y1, x2, y2, z, colorFrom, colorTo);
        this.flushIfUnmanaged();
    }

    private void fillGradient(VertexConsumer consumer, int x1, int y1, int x2, int y2, int z, int colorFrom, int colorTo) {
        float f = (float)FastColor.ARGB32.alpha(colorFrom) / 255.0f;
        float g = (float)FastColor.ARGB32.red(colorFrom) / 255.0f;
        float h = (float)FastColor.ARGB32.green(colorFrom) / 255.0f;
        float i = (float)FastColor.ARGB32.blue(colorFrom) / 255.0f;
        float j = (float)FastColor.ARGB32.alpha(colorTo) / 255.0f;
        float k = (float)FastColor.ARGB32.red(colorTo) / 255.0f;
        float l = (float)FastColor.ARGB32.green(colorTo) / 255.0f;
        float m = (float)FastColor.ARGB32.blue(colorTo) / 255.0f;
        Matrix4f matrix4f = this.pose.last().pose();
        consumer.vertex(matrix4f, x1, y1, z).color(g, h, i, f).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        consumer.vertex(matrix4f, x1, y2, z).color(k, l, m, j).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        consumer.vertex(matrix4f, x2, y2, z).color(k, l, m, j).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
        consumer.vertex(matrix4f, x2, y1, z).color(g, h, i, f).uv(0, 0).uv2(0, 0).normal(1,1,1).endVertex();
    }

    public void drawCenteredString(Font font, String text, int x, int y, int color) {
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        FormattedCharSequence formattedCharSequence = text.getVisualOrderText();
        this.drawString(font, formattedCharSequence, x - font.width(formattedCharSequence) / 2, y, color);
    }

    public void drawCenteredString(Font font, FormattedCharSequence text, int x, int y, int color) {
        this.drawString(font, text, x - font.width(text) / 2, y, color);
    }

    public int drawString(Font font, @Nullable String text, int x, int y, int color) {
        return this.drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, @Nullable String text, int x, int y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        }
        int i = font.drawInBatch(text, x, y, color, dropShadow, this.pose.last().pose(), this.bufferSource, false, 0, 0xF000F0, font.isBidirectional());
        this.flushIfUnmanaged();
        return i;
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        return this.drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        int i = font.drawInBatch(text, (float)x, (float)y, color, dropShadow, this.pose.last().pose(), this.bufferSource, false, 0, 0xF000F0);
        this.flushIfUnmanaged();
        return i;
    }

    public int drawString(Font font, Component text, int x, int y, int color) {
        return this.drawString(font, text, x, y, color, true);
    }

    public int drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
        return this.drawString(font, text.getVisualOrderText(), x, y, color, dropShadow);
    }

    public void drawWordWrap(Font font, FormattedText text, int x, int y, int lineWidth, int color) {
        for (FormattedCharSequence formattedCharSequence : font.split(text, lineWidth)) {
            this.drawString(font, formattedCharSequence, x, y, color, false);
            Objects.requireNonNull(font);
            y += 9;
        }
    }

    public void blit(int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite) {
        this.innerBlit(sprite.atlas().location(), x, x + width, y, y + height, blitOffset, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
    }

    public void blit(int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite, float red, float green, float blue, float alpha) {
        this.innerBlit(sprite.atlas().location(), x, x + width, y, y + height, blitOffset, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), red, green, blue, alpha);
    }

    public void renderOutline(int x, int y, int width, int height, int color) {
        this.fill(x, y, x + width, y + 1, color);
        this.fill(x, y + height - 1, x + width, y + height, color);
        this.fill(x, y + 1, x + 1, y + height - 1, color);
        this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public void blit(ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        this.blit(atlasLocation, x, y, 0, (float)uOffset, vOffset, uWidth, vHeight, 256, 256);
    }

    public void blit(ResourceLocation atlasLocation, int x, int y, int blitOffset, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        this.blit(atlasLocation, x, x + uWidth, y, y + vHeight, blitOffset, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public void blit(ResourceLocation atlasLocation, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        this.blit(atlasLocation, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    public void blit(ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight) {
        this.blit(atlasLocation, x, y, width, height, uOffset, vOffset, width, height, textureWidth, textureHeight);
    }

    void blit(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight) {
        this.innerBlit(atlasLocation, x1, x2, y1, y2, blitOffset, (uOffset + 0.0f) / (float)textureWidth, (uOffset + (float)uWidth) / (float)textureWidth, (vOffset + 0.0f) / (float)textureHeight, (vOffset + (float)vHeight) / (float)textureHeight);
    }

    void innerBlit(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = this.pose.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, x1, y1, blitOffset).uv(minU, minV).endVertex();
        bufferBuilder.vertex(matrix4f, x1, y2, blitOffset).uv(minU, maxV).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y2, blitOffset).uv(maxU, maxV).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y1, blitOffset).uv(maxU, minV).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    void innerBlit(ResourceLocation atlasLocation, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV, float red, float green, float blue, float alpha) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = this.pose.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferBuilder.vertex(matrix4f, x1, y1, blitOffset).color(red, green, blue, alpha).uv(minU, minV).endVertex();
        bufferBuilder.vertex(matrix4f, x1, y2, blitOffset).color(red, green, blue, alpha).uv(minU, maxV).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y2, blitOffset).color(red, green, blue, alpha).uv(maxU, maxV).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y1, blitOffset).color(red, green, blue, alpha).uv(maxU, minV).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public void blitNineSliced(ResourceLocation atlasLocation, int targetX, int targetY, int targetWidth, int targetHeight, int sliceSize, int sourceWidth, int sourceHeight, int sourceX, int sourceY) {
        this.blitNineSliced(atlasLocation, targetX, targetY, targetWidth, targetHeight, sliceSize, sliceSize, sliceSize, sliceSize, sourceWidth, sourceHeight, sourceX, sourceY);
    }

    public void blitNineSliced(ResourceLocation atlasLocation, int targetX, int targetY, int targetWidth, int targetHeight, int sliceWidth, int sliceHeight, int sourceWidth, int sourceHeight, int sourceX, int sourceY) {
        this.blitNineSliced(atlasLocation, targetX, targetY, targetWidth, targetHeight, sliceWidth, sliceHeight, sliceWidth, sliceHeight, sourceWidth, sourceHeight, sourceX, sourceY);
    }

    public void blitNineSliced(ResourceLocation atlasLocation, int targetX, int targetY, int targetWidth, int targetHeight, int cornerWidth, int cornerHeight, int edgeWidth, int edgeHeight, int sourceWidth, int sourceHeight, int sourceX, int sourceY) {
        cornerWidth = Math.min(cornerWidth, targetWidth / 2);
        edgeWidth = Math.min(edgeWidth, targetWidth / 2);
        cornerHeight = Math.min(cornerHeight, targetHeight / 2);
        edgeHeight = Math.min(edgeHeight, targetHeight / 2);
        if (targetWidth == sourceWidth && targetHeight == sourceHeight) {
            this.blit(atlasLocation, targetX, targetY, sourceX, sourceY, targetWidth, targetHeight);
            return;
        }
        if (targetHeight == sourceHeight) {
            this.blit(atlasLocation, targetX, targetY, sourceX, sourceY, cornerWidth, targetHeight);
            this.blitRepeating(atlasLocation, targetX + cornerWidth, targetY, targetWidth - edgeWidth - cornerWidth, targetHeight, sourceX + cornerWidth, sourceY, sourceWidth - edgeWidth - cornerWidth, sourceHeight);
            this.blit(atlasLocation, targetX + targetWidth - edgeWidth, targetY, sourceX + sourceWidth - edgeWidth, sourceY, edgeWidth, targetHeight);
            return;
        }
        if (targetWidth == sourceWidth) {
            this.blit(atlasLocation, targetX, targetY, sourceX, sourceY, targetWidth, cornerHeight);
            this.blitRepeating(atlasLocation, targetX, targetY + cornerHeight, targetWidth, targetHeight - edgeHeight - cornerHeight, sourceX, sourceY + cornerHeight, sourceWidth, sourceHeight - edgeHeight - cornerHeight);
            this.blit(atlasLocation, targetX, targetY + targetHeight - edgeHeight, sourceX, sourceY + sourceHeight - edgeHeight, targetWidth, edgeHeight);
            return;
        }
        this.blit(atlasLocation, targetX, targetY, sourceX, sourceY, cornerWidth, cornerHeight);
        this.blitRepeating(atlasLocation, targetX + cornerWidth, targetY, targetWidth - edgeWidth - cornerWidth, cornerHeight, sourceX + cornerWidth, sourceY, sourceWidth - edgeWidth - cornerWidth, cornerHeight);
        this.blit(atlasLocation, targetX + targetWidth - edgeWidth, targetY, sourceX + sourceWidth - edgeWidth, sourceY, edgeWidth, cornerHeight);
        this.blit(atlasLocation, targetX, targetY + targetHeight - edgeHeight, sourceX, sourceY + sourceHeight - edgeHeight, cornerWidth, edgeHeight);
        this.blitRepeating(atlasLocation, targetX + cornerWidth, targetY + targetHeight - edgeHeight, targetWidth - edgeWidth - cornerWidth, edgeHeight, sourceX + cornerWidth, sourceY + sourceHeight - edgeHeight, sourceWidth - edgeWidth - cornerWidth, edgeHeight);
        this.blit(atlasLocation, targetX + targetWidth - edgeWidth, targetY + targetHeight - edgeHeight, sourceX + sourceWidth - edgeWidth, sourceY + sourceHeight - edgeHeight, edgeWidth, edgeHeight);
        this.blitRepeating(atlasLocation, targetX, targetY + cornerHeight, cornerWidth, targetHeight - edgeHeight - cornerHeight, sourceX, sourceY + cornerHeight, cornerWidth, sourceHeight - edgeHeight - cornerHeight);
        this.blitRepeating(atlasLocation, targetX + cornerWidth, targetY + cornerHeight, targetWidth - edgeWidth - cornerWidth, targetHeight - edgeHeight - cornerHeight, sourceX + cornerWidth, sourceY + cornerHeight, sourceWidth - edgeWidth - cornerWidth, sourceHeight - edgeHeight - cornerHeight);
        this.blitRepeating(atlasLocation, targetX + targetWidth - edgeWidth, targetY + cornerHeight, cornerWidth, targetHeight - edgeHeight - cornerHeight, sourceX + sourceWidth - edgeWidth, sourceY + cornerHeight, edgeWidth, sourceHeight - edgeHeight - cornerHeight);
    }

    public void blitRepeating(ResourceLocation atlasLocation, int targetX, int targetY, int targetWidth, int targetHeight, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        int i = targetX;
        IntIterator intIterator = GuiGraphics.slices(targetWidth, sourceWidth);
        while (intIterator.hasNext()) {
            int j = intIterator.nextInt();
            int k = (sourceWidth - j) / 2;
            int l = targetY;
            IntIterator intIterator2 = GuiGraphics.slices(targetHeight, sourceHeight);
            while (intIterator2.hasNext()) {
                int m = intIterator2.nextInt();
                int n = (sourceHeight - m) / 2;
                this.blit(atlasLocation, i, l, sourceX + k, sourceY + n, j, m);
                l += m;
            }
            i += j;
        }
    }

    private static IntIterator slices(int i, int j) {
        int k = Mth.positiveCeilDiv(i, j);
        return new Divisor(i, k);
    }

    public void renderItem(ItemStack stack, int x, int y) {
        this.renderItem(this.minecraft.player, this.minecraft.level, stack, x, y, 0);
    }

    public void renderItem(ItemStack stack, int x, int y, int seed) {
        this.renderItem(this.minecraft.player, this.minecraft.level, stack, x, y, seed);
    }

    public void renderItem(ItemStack stack, int x, int y, int seed, int i) {
        this.renderItem(this.minecraft.player, this.minecraft.level, stack, x, y, seed, i);
    }

    public void renderFakeItem(ItemStack stack, int x, int y) {
        this.renderItem(null, this.minecraft.level, stack, x, y, 0);
    }

    public void renderItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
        this.renderItem(entity, entity.level, stack, x, y, seed);
    }

    private void renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed) {
        this.renderItem(entity, level, stack, x, y, seed, 0);
    }

    private void renderItem(@Nullable LivingEntity entity, @Nullable Level level, ItemStack stack, int x, int y, int seed, int i) {
        if (stack.isEmpty()) {
            return;
        }
        BakedModel bakedModel = this.minecraft.getItemRenderer().getModel(stack, level, entity, seed);
        this.pose.pushPose();
        this.pose.translate(x + 8, y + 8, 150 + (bakedModel.isGui3d() ? i : 0));
        try {
            boolean bl;
            var jomlMatrix = new org.joml.Matrix4f().scaling(1f, -1f, 1f);
            var mcMatrix = new Matrix4f();

            var arr = jomlMatrix.get(new float[16]);
            ((Matrix4fAccess) (Object) mcMatrix).copyFromArray(arr);

            this.pose.mulPoseMatrix(mcMatrix);
            this.pose.scale(16.0f, 16.0f, 16.0f);
            boolean bl2 = bl = !bakedModel.usesBlockLight();
            if (bl) {
                Lighting.setupForFlatItems();
            }
            this.minecraft.getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, this.pose, this.bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);
            this.flush();
            if (bl) {
                Lighting.setupFor3DItems();
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
            crashReportCategory.setDetail("Item Type", () -> String.valueOf(stack.getItem()));
            crashReportCategory.setDetail("Item Damage", () -> String.valueOf(stack.getDamageValue()));
            crashReportCategory.setDetail("Item NBT", () -> String.valueOf(stack.getTag()));
            crashReportCategory.setDetail("Item Foil", () -> String.valueOf(stack.hasFoil()));
            throw new ReportedException(crashReport);
        }
        this.pose.popPose();
    }

    public void renderItemDecorations(Font font, ItemStack stack, int x, int y) {
        this.renderItemDecorations(font, stack, x, y, null);
    }

    public void renderItemDecorations(Font font, ItemStack stack, int x, int y, @Nullable String text) {
        LocalPlayer localPlayer;
        float f;
        int l;
        int k;
        if (stack.isEmpty()) {
            return;
        }
        this.pose.pushPose();
        if (stack.getCount() != 1 || text != null) {
            String string = text == null ? String.valueOf(stack.getCount()) : text;
            this.pose.translate(0.0f, 0.0f, 200.0f);
            this.drawString(font, string, x + 19 - 2 - font.width(string), y + 6 + 3, 0xFFFFFF, true);
        }
        if (stack.isBarVisible()) {
            int i = stack.getBarWidth();
            int j = stack.getBarColor();
            k = x + 2;
            l = y + 13;
            this.fill( RenderType.translucentNoCrumbling(), k, l, k + 13, l + 2, -16777216);
            this.fill( RenderType.translucentNoCrumbling(), k, l, k + i, l + 1, j | 0xFF000000);
        }
        float f2 = f = (localPlayer = this.minecraft.player) == null ? 0.0f : localPlayer.getCooldowns().getCooldownPercent(stack.getItem(), this.minecraft.getFrameTime());
        if (f > 0.0f) {
            k = y + Mth.floor(16.0f * (1.0f - f));
            l = k + Mth.ceil(16.0f * f);
            this.fill( RenderType.translucentNoCrumbling(), x, k, x + 16, l, Integer.MAX_VALUE);
        }
        this.pose.popPose();
    }

    public void renderTooltip(Font font, ItemStack stack, int mouseX, int mouseY) {
        this.renderTooltip(font, stack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL), stack.getTooltipImage(), mouseX, mouseY);
    }

    public void renderTooltip(Font font, List<Component> tooltipLines, Optional<TooltipComponent> visualTooltipComponent, int mouseX, int mouseY) {
        List<ClientTooltipComponent> list = tooltipLines.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).collect(Collectors.toList());
        visualTooltipComponent.ifPresent(tooltipComponent -> list.add(1, ClientTooltipComponent.create(tooltipComponent)));
        this.renderTooltipInternal(font, list, mouseX, mouseY);
    }

    public void renderTooltip(Font font, Component text, int mouseX, int mouseY) {
        this.renderTooltip(font, List.of(text.getVisualOrderText()), mouseX, mouseY);
    }

    public void renderComponentTooltip(Font font, List<Component> tooltipLines, int mouseX, int mouseY) {
        this.renderTooltip(font, Lists.transform(tooltipLines, Component::getVisualOrderText), mouseX, mouseY);
    }

    public void renderTooltip(Font font, List<? extends FormattedCharSequence> tooltipLines, int mouseX, int mouseY) {
        this.renderTooltipInternal(font, tooltipLines.stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), mouseX, mouseY);
    }

    private void positionTooltip(int screenWidth, int screenHeight, Vector2i tooltipPos, int tooltipWidth, int tooltipHeight) {
        int i;
        if (tooltipPos.x + tooltipWidth > screenWidth) {
            tooltipPos.x = Math.max(tooltipPos.x - 24 - tooltipWidth, 4);
        }
        if (tooltipPos.y + (i = tooltipHeight + 3) > screenHeight) {
            tooltipPos.y = screenHeight - i;
        }
    }

    private void renderTooltipInternal(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY) {
        ClientTooltipComponent clientTooltipComponent2;
        int r;
        if (components.isEmpty()) {
            return;
        }
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent clientTooltipComponent : components) {
            int k = clientTooltipComponent.getWidth(font);
            if (k > i) {
                i = k;
            }
            j += clientTooltipComponent.getHeight();
        }
        int l = i;
        int m = j;
        Vector2i vector2ic = new Vector2i(mouseX, mouseY).add(12, -12);
        positionTooltip(this.guiWidth(), this.guiHeight(), vector2ic, l, m);
        int n = vector2ic.x();
        int o = vector2ic.y();
        this.pose.pushPose();
        int p = 400;
        this.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground(this, n, o, l, m, 400));
        this.pose.translate(0.0f, 0.0f, 400.0f);
        int q = o;
        for (r = 0; r < components.size(); ++r) {
            clientTooltipComponent2 = components.get(r);
            clientTooltipComponent2.renderText(font, n, q, this.pose.last().pose(), this.bufferSource);
            q += clientTooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
        }
        q = o;
        for (r = 0; r < components.size(); ++r) {
            clientTooltipComponent2 = components.get(r);
            clientTooltipComponent2.renderImage(font, n, q, this.pose, this.minecraft.getItemRenderer(), 0);
            q += clientTooltipComponent2.getHeight() + (r == 0 ? 2 : 0);
        }
        this.pose.popPose();
    }

    public void renderComponentHoverEffect(Font font, @Nullable Style style, int mouseX, int mouseY) {
        if (style == null || style.getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = style.getHoverEvent();
        HoverEvent.ItemStackInfo itemStackInfo = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
        if (itemStackInfo != null) {
            this.renderTooltip(font, itemStackInfo.getItemStack(), mouseX, mouseY);
        } else {
            HoverEvent.EntityTooltipInfo entityTooltipInfo = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (entityTooltipInfo != null) {
                if (this.minecraft.options.advancedItemTooltips) {
                    this.renderComponentTooltip(font, entityTooltipInfo.getTooltipLines(), mouseX, mouseY);
                }
            } else {
                Component component = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                if (component != null) {
                    this.renderTooltip(font, font.split(component, Math.max(this.guiWidth() / 2, 200)), mouseX, mouseY);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ScissorStack {
        private final Deque<ScreenRectangle> stack = new ArrayDeque<ScreenRectangle>();

        ScissorStack() {
        }

        public ScreenRectangle push(ScreenRectangle sciccor) {
            ScreenRectangle screenRectangle = this.stack.peekLast();
            if (screenRectangle != null) {
                ScreenRectangle screenRectangle2 = Objects.requireNonNullElse(sciccor.intersection(screenRectangle), ScreenRectangle.empty());
                this.stack.addLast(screenRectangle2);
                return screenRectangle2;
            }
            this.stack.addLast(sciccor);
            return sciccor;
        }

        @Nullable
        public ScreenRectangle pop() {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException("Scissor stack underflow");
            }
            this.stack.removeLast();
            return this.stack.peekLast();
        }
    }
}
