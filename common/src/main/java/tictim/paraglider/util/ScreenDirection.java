package tictim.paraglider.util;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum ScreenDirection {
    UP, DOWN, LEFT, RIGHT;

    private final IntComparator coordinateValueComparator = (i, j) -> i == j ? 0 : (this.isBefore(i, j) ? -1 : 1);

    public ScreenAxis getAxis() {
        return switch (this) {
            case UP, DOWN -> ScreenAxis.VERTICAL;
            case LEFT, RIGHT -> ScreenAxis.HORIZONTAL;
        };
    }

    public ScreenDirection getOpposite() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case UP, LEFT -> false;
            case DOWN, RIGHT -> true;
        };
    }

    public boolean isAfter(int first, int second) {
        if (this.isPositive()) {
            return first > second;
        }
        return second > first;
    }

    public boolean isBefore(int first, int second) {
        if (this.isPositive()) {
            return first < second;
        }
        return second < first;
    }

    public IntComparator coordinateValueComparator() {
        return this.coordinateValueComparator;
    }
}
