package tictim.paraglider.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;

public class BlockPosArgHelper {
    public static BlockPos getBlockPos(CommandContext<CommandSourceStack> context, String name) {
        return (context.getArgument(name, Coordinates.class)).getBlockPos(context.getSource());
    }
}
