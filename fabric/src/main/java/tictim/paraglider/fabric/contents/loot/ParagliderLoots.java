package tictim.paraglider.fabric.contents.loot;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import tictim.paraglider.api.ParagliderAPI;

public final class ParagliderLoots{
	private ParagliderLoots(){}

	public static final LootPoolEntryType VESSEL_LOOT_ENTRY = new LootPoolEntryType(new VesselLootEntry.Serializer());
	public static final LootPoolEntryType PARAGLIDER_LOOT_ENTRY = new LootPoolEntryType(new ParagliderLootEntry.Serializer());

	public static final LootItemConditionType WITHER_DROPS_VESSEL = new LootItemConditionType(LootConditions.WITHER_DROPS_VESSEL);
	public static final LootItemConditionType SPIRIT_ORB_LOOTS = new LootItemConditionType(LootConditions.SPIRIT_ORB_LOOTS);
	public static final LootItemConditionType FEATURES_SPIRIT_ORBS = new LootItemConditionType(LootConditions.FEATURES_SPIRIT_ORBS);

	public static final LootItemFunctionType SPAWNER_SPIRIT_ORB_COUNT = new LootItemFunctionType(LootFunctions.SPAWNER_SPIRIT_ORB_COUNT);

	public static void register(){
		Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, ParagliderAPI.id("vessel"), VESSEL_LOOT_ENTRY);
		Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, ParagliderAPI.id("paraglider"), PARAGLIDER_LOOT_ENTRY);

		Registry.register(Registry.LOOT_CONDITION_TYPE, ParagliderAPI.id("config_wither_drops_vessel"), WITHER_DROPS_VESSEL);
		Registry.register(Registry.LOOT_CONDITION_TYPE, ParagliderAPI.id("config_spirit_orb_loots"), SPIRIT_ORB_LOOTS);
		Registry.register(Registry.LOOT_CONDITION_TYPE, ParagliderAPI.id("feature_spirit_orbs"), FEATURES_SPIRIT_ORBS);

		Registry.register(Registry.LOOT_FUNCTION_TYPE, ParagliderAPI.id("spawner_spirit_orb_count"), SPAWNER_SPIRIT_ORB_COUNT);
	}
}
