package tictim.paraglider.bargain.preview;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.bargain.OfferPreview;

import java.util.List;
import java.util.Objects;

/**
 * Pair of an item and an unsigned int. Isn't it called {@link ItemStack}? Sure, but it ain't have support for
 * {@code 2^31-1} items.
 */
public record QuantifiedItem(@NotNull ItemStack item, int quantity) implements OfferPreview{
	@NotNull public static QuantifiedItem read(@NotNull FriendlyByteBuf buffer){
		return new QuantifiedItem(buffer.readItem(), buffer.readVarInt());
	}

	public QuantifiedItem(@NotNull Item item, int quantity){
		this(new ItemStack(item), Math.max(0, quantity));
	}

	public QuantifiedItem(@NotNull ItemStack item, int quantity){
		this.item = Objects.requireNonNull(item);
		this.quantity = Math.max(0, quantity);
	}

	public QuantifiedItem(@NotNull JsonObject object){
		this(parseItemStack(object), GsonHelper.getAsInt(object, "count", 1));
	}

	private static ItemStack parseItemStack(JsonObject object){
		ItemStack stack = ShapedRecipe.itemStackFromJson(object);
		stack.setCount(1);
		return stack;
	}

	@NotNull public ItemStack getItem(){
		return item;
	}
	public int getQuantity(){
		return quantity;
	}

	@Override @NotNull public ItemStack preview(){
		return item;
	}
	@Environment(EnvType.CLIENT)
	@Override @NotNull public List<@NotNull Component> getTooltip(){
		return item.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
	}

	@NotNull public ItemStack getItemWithQuantity(){
		ItemStack copy = item.copy();
		copy.setCount(quantity);
		return copy;
	}

	@NotNull public JsonObject serialize(){
		JsonObject obj = new JsonObject();
		obj.addProperty("item", ParagliderUtils.getKey(item.getItem())+"");
		if(quantity!=1) obj.addProperty("count", quantity);
		return obj;
	}

	public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeItem(item);
		buffer.writeVarInt(quantity);
	}
}
