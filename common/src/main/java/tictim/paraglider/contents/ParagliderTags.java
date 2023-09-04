package tictim.paraglider.contents;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import static tictim.paraglider.api.ParagliderAPI.id;

public interface ParagliderTags{
	TagKey<Item> PARAGLIDERS = TagKey.create(Registry.ITEM_REGISTRY, id("paragliders"));
	TagKey<Item> STATUES = TagKey.create(Registry.ITEM_REGISTRY, id("statues"));
	TagKey<Item> STATUES_GODDESS = TagKey.create(Registry.ITEM_REGISTRY, id("statues/goddess"));

	interface Blocks{
		TagKey<Block> STATUES = TagKey.create(Registry.BLOCK_REGISTRY, id("statues"));
		TagKey<Block> STATUES_GODDESS = TagKey.create(Registry.BLOCK_REGISTRY, id("statues/goddess"));
	}

	interface Biomes{
		TagKey<Biome> HAS_STRUCTURE_UNDERGROUND_HORNED_STATUE = hasStructure(id("underground_horned_statue"));
		TagKey<Biome> HAS_STRUCTURE_NETHER_HORNED_STATUE = hasStructure(id("nether_horned_statue"));
		TagKey<Biome> HAS_STRUCTURE_TARREY_TOWN_GODDESS_STATUE = hasStructure(id("tarrey_town_goddess_statue"));

		@NotNull private static TagKey<Biome> hasStructure(@NotNull ResourceLocation id){
			return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(id.getNamespace(), "has_structure/" + id.getPath()));
		}
	}
}
