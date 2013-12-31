package mariculture.transport;

import mariculture.api.core.MaricultureRegistry;
import mariculture.core.Core;
import mariculture.core.Mariculture;
import mariculture.core.helpers.RegistryHelper;
import mariculture.core.lib.CraftingMeta;
import mariculture.core.lib.EntityIds;
import mariculture.core.lib.ItemIds;
import mariculture.core.lib.Modules.Module;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.EntityRegistry;

public class Transport extends Module {
	public static boolean isActive;
	
	@Override
	public boolean isActive() {
		return this.isActive;
	}
	
	@Override
	public void setActive(boolean active) {
		isActive = active;
	}

	public static Item speedBoat;
	
	@Override
	public void registerEntities() {
		EntityRegistry.registerModEntity(EntitySpeedBoat.class, "speedBoat", EntityIds.SPEED_BOAT, Mariculture.instance, 250, 5, false);
	}

	@Override
	public void registerItems() {
		speedBoat = new ItemSpeedBoat(ItemIds.speedBoat).setUnlocalizedName("speedBoat");
		RegistryHelper.register(new Object[] { speedBoat });
	}

	@Override
	public void addRecipes() {
		CraftingManager
				.getInstance()
				.getRecipeList()
				.add(new ShapedOreRecipe(new ItemStack(Transport.speedBoat), new Object[] { "G F", "AAA",
						Character.valueOf('G'), Block.thinGlass, 
						Character.valueOf('F'), new ItemStack(Core.craftingItem, 1, CraftingMeta.COOLER), 
						Character.valueOf('A'), "ingotAluminum" }));
	}
}