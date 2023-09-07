package tictim.paraglider.bargain.preview;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import tictim.paraglider.api.bargain.DemandPreview;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Pair of an ingredient and a non-negative int.
 */
public record QuantifiedIngredient(
		@NotNull Ingredient ingredient,
		@Range(from = 0, to = Integer.MAX_VALUE) int quantity
) implements Predicate<ItemStack>, DemandPreview{
	@NotNull public static QuantifiedIngredient read(@NotNull FriendlyByteBuf buffer){
		return new QuantifiedIngredient(Ingredient.fromNetwork(buffer), buffer.readVarInt());
	}

	public QuantifiedIngredient(@NotNull Ingredient ingredient, int quantity){
		this.ingredient = Objects.requireNonNull(ingredient);
		this.quantity = Math.max(0, quantity);
	}
	public QuantifiedIngredient(@NotNull JsonObject obj){
		this(Ingredient.fromJson(obj.get("ingredient")), Math.max(1, GsonHelper.getAsInt(obj, "quantity", 1)));
	}

	/**
	 * Test the ItemStack using ingredient. Does not count quantity.
	 */
	@Override public boolean test(ItemStack itemStack){
		return ingredient.test(itemStack);
	}

	@Override @NotNull @Unmodifiable public List<@NotNull ItemStack> preview(){
		return List.of(ingredient.getItems());
	}
	@Override public int count(@NotNull Player player){
		int count = 0;
		for(int i = 0; i<player.getInventory().getContainerSize(); i++){
			ItemStack stack = player.getInventory().getItem(i);
			if(stack.isEmpty()||!ingredient.test(stack)) continue;
			count += stack.getCount();
		}
		return count;
	}

	@Environment(EnvType.CLIENT)
	@Override @NotNull public List<@NotNull Component> getTooltip(int previewIndex){
		ItemStack[] items = ingredient.getItems();
		if(items.length==0) return List.of();
		ItemStack stack = items[previewIndex<0||items.length<=previewIndex ? 0 : previewIndex];
		return stack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
	}

	@NotNull public JsonElement serialize(){
		JsonObject obj = new JsonObject();
		obj.add("ingredient", ingredient.toJson());
		if(quantity!=1) obj.addProperty("quantity", quantity);
		return obj;
	}

	public void write(@NotNull FriendlyByteBuf buffer){
		ingredient.toNetwork(buffer);
		buffer.writeVarInt(quantity);
	}
}
