package tictim.paraglider.util;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class FastColorHelper {
    public static int lerpInt(float delta, int start, int end) {
        return start + Mth.floor(delta * (float)(end - start));
    }

    public static int lerp(float delta, int min, int max) {
        int i = lerpInt(delta, FastColor.ARGB32.alpha(min), FastColor.ARGB32.alpha(max));
        int j = lerpInt(delta, FastColor.ARGB32.red(min), FastColor.ARGB32.red(max));
        int k = lerpInt(delta, FastColor.ARGB32.green(min), FastColor.ARGB32.green(max));
        int l = lerpInt(delta, FastColor.ARGB32.blue(min), FastColor.ARGB32.blue(max));
        return FastColor.ARGB32.color(i, j, k, l);
    }
}
