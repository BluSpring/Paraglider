package tictim.paraglider.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockHelper {
    public static boolean isSolid(BlockState state, Level level, BlockPos blockPos) {
        var shape = state.getCollisionShape(level, blockPos);

        if (shape.isEmpty())
            return false;

        var aabb = shape.bounds();
        if (aabb.getSize() >= 0.7291666666666666)
            return true;

        return aabb.getYsize() >= 1;
    }
}
