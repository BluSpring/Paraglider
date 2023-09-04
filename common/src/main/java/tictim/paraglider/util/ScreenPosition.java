package tictim.paraglider.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record ScreenPosition(int x, int y) {
    public static ScreenPosition of(ScreenAxis axis, int primaryPosition, int secondaryPosition) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> new ScreenPosition(primaryPosition, secondaryPosition);
            case VERTICAL -> new ScreenPosition(secondaryPosition, primaryPosition);
        };
    }

    public ScreenPosition step(ScreenDirection direction) {
        return switch (direction) {
            default -> throw new IncompatibleClassChangeError();
            case DOWN -> new ScreenPosition(this.x, this.y + 1);
            case UP -> new ScreenPosition(this.x, this.y - 1);
            case LEFT -> new ScreenPosition(this.x - 1, this.y);
            case RIGHT -> new ScreenPosition(this.x + 1, this.y);
        };
    }

    public int getCoordinate(ScreenAxis axis) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case HORIZONTAL -> this.x;
            case VERTICAL -> this.y;
        };
    }
}

