package mariculture.plugins;

import static mariculture.core.lib.MCLib.stone;
import static mariculture.core.util.Fluids.getFluidName;
import static mariculture.core.util.Fluids.getFluidStack;
import static tconstruct.library.crafting.FluidType.getFluidType;

import java.util.ArrayList;
import java.util.Map;

import mariculture.api.core.MaricultureHandlers;
import mariculture.api.core.RecipeSmelter;
import mariculture.core.Core;
import mariculture.core.RecipesSmelting;
import mariculture.core.blocks.BlockAir;
import mariculture.core.handlers.LogHandler;
import mariculture.core.lib.LimestoneMeta;
import mariculture.core.lib.MetalMeta;
import mariculture.core.lib.MetalRates;
import mariculture.core.lib.RockMeta;
import mariculture.core.util.Fluids;
import mariculture.plugins.Plugins.Plugin;
import mariculture.plugins.tconstruct.TitaniumTools;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.Smeltery;
import cpw.mods.fml.common.registry.GameRegistry;

public class PluginTConstruct extends Plugin {
    public PluginTConstruct(String name) {
        super(name);
    }

    @Override
    public void preInit() {
        try {
            for (Map.Entry<String, FluidType> entry : FluidType.fluidTypes.entrySet()) {
                Fluids.add(entry.getKey().toLowerCase(), entry.getValue().fluid, 144);
            }

            Fluids.add("ender", getFluidType("Ender").fluid, 25, true);
            Fluids.add("blood", FluidRegistry.getFluid("blood"), 10, true);
        } catch (Exception e) {
            e.printStackTrace();
            LogHandler.log(Level.INFO, "Mariculture failed to sync up with TiC Fluids");
        }

        TitaniumTools.preInit();
    }

    @Override
    public void init() {
        ItemStack stack = new ItemStack(stone);
        ItemStack rack = new ItemStack(Blocks.netherrack);
        RecipesSmelting.addMetal(getFluidName("aluminumbrass"), "AluminumBrass", 940, stack, 2);
        RecipesSmelting.addMetal(getFluidName("obsidian"), "Obsidian", 1000, stack, 2);
        RecipesSmelting.addMetal(getFluidName("cobalt"), "Cobalt", 1495, rack, 2);
        RecipesSmelting.addMetal(getFluidName("ardite"), "Ardite", 1750, rack, 2);
        RecipesSmelting.addMetal(getFluidName("manyullyn"), "Manyullyn", 1950, rack, 2);
        RecipesSmelting.addMetal(getFluidName("alumite"), "Alumite", 387, stack, 2);
        RecipesSmelting.addMetal(getFluidName("platinum"), "Platinum", 1768, stack, 2);
        RecipesSmelting.addMetal(getFluidName("invar"), "Invar", 1427, stack, 2);
        Block torch = GameRegistry.findBlock("TConstruct", "decoration.stonetorch");
        if (torch != null) BlockAir.flammables.add(torch);
    }

    @Override
    public void postInit() {
        TitaniumTools.postInit();
        addAlloy();
        addMelting();
    }

    public static void addMelting() {
        // Melt Down Titanium
        PluginTConstruct.addMelting(Core.limestone, LimestoneMeta.RAW, "blockLimestone", getFluidStack("quicklime", 900), 250);
        PluginTConstruct.addMelting(Core.rocks, RockMeta.RUTILE, "oreRutile", getFluidStack("rutile", MetalRates.ORE), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.RUTILE_BLOCK, "dustRutile", getFluidStack("rutile", MetalRates.INGOT), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.RUTILE_BLOCK, "ingotRutile", getFluidStack("rutile", MetalRates.INGOT), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.RUTILE_BLOCK, "blockRutile", getFluidStack("rutile", MetalRates.BLOCK), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.RUTILE_BLOCK, "nuggetRutile", getFluidStack("rutile", MetalRates.NUGGET), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.TITANIUM_BLOCK, "dustTitanium", getFluidStack("titanium", MetalRates.INGOT), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.TITANIUM_BLOCK, "ingotTitanium", getFluidStack("titanium", MetalRates.INGOT), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.TITANIUM_BLOCK, "blockTitanium", getFluidStack("titanium", MetalRates.BLOCK), 800);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.TITANIUM_BLOCK, "nuggetTitanium", getFluidStack("titanium", MetalRates.NUGGET), 800);
        // >> Form Ingot and Block
        PluginTConstruct.addCasting("nuggetRutile", getFluidStack("rutile", MetalRates.NUGGET), 100);
        PluginTConstruct.addCasting("ingotRutile", getFluidStack("rutile", MetalRates.INGOT), 100);
        PluginTConstruct.addCasting("nuggetRutile", getFluidStack("titanium", MetalRates.NUGGET), 100);
        PluginTConstruct.addCasting("ingotTitanium", getFluidStack("titanium", MetalRates.INGOT), 100);
        PluginTConstruct.addBlockCasting("blockTitanium", getFluidStack("titanium", MetalRates.BLOCK), 100);
        // Melt Down Magnesium
        PluginTConstruct.addMelting(Core.rocks, RockMeta.BAUXITE, "oreMagnesium", getFluidStack("magnesium", MetalRates.ORE), 300);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.MAGNESIUM_BLOCK, "dustMagnesium", getFluidStack("magnesium", MetalRates.INGOT), 300);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.MAGNESIUM_BLOCK, "ingotMagnesium", getFluidStack("magnesium", MetalRates.INGOT), 300);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.MAGNESIUM_BLOCK, "blockMagnesium", getFluidStack("magnesium", MetalRates.BLOCK), 300);
        PluginTConstruct.addMelting(Core.metals, MetalMeta.MAGNESIUM_BLOCK, "nuggetMagnesium", getFluidStack("magnesium", MetalRates.NUGGET), 300);
        // >> Form Ingot and Block
        PluginTConstruct.addCasting("ingotMagnesium", getFluidStack("magnesium", MetalRates.INGOT), 100);
        PluginTConstruct.addBlockCasting("blockMagnesium", getFluidStack("magnesium", MetalRates.BLOCK), 100);

        if (FluidRegistry.getFluid("xpjuice") != null) {
            ItemStack xpberry = TConstructRegistry.getItemStack("oreberryEssence");
            ArrayList<FluidStack> fluids = new ArrayList<FluidStack>();
            ArrayList<Integer> chances = new ArrayList<Integer>();

            int j = 3;
            for (int i = 80; i <= 120; i += 10) {
                chances.add(j);
                fluids.add(FluidRegistry.getFluidStack("xpjuice", i));
                j++;
            }

            MaricultureHandlers.crucible.addRecipe(new RecipeSmelter(xpberry, 1000, fluids.toArray(new FluidStack[fluids.size()]), chances.toArray(new Integer[chances.size()]), null, 0));
        }
    }

    private static void addAlloy() {
        FluidStack titanium = getFluidStack("titanium", 8);
        FluidStack rutile = getFluidStack("rutile", 8);
        FluidStack magnesium = getFluidStack("magnesium", 8);

        Smeltery.addAlloyMixing(titanium, new FluidStack[] { rutile, magnesium });
    }

    //Helper Methods
    public static void addMelting(Block block, int meta, String dic, FluidStack fluid, int temp) {
        if (fluid == null) return;
        if (OreDictionary.getOres(dic).size() > 0) {
            for (ItemStack ore : OreDictionary.getOres(dic))
                if (block != null) {
                    Smeltery.addMelting(ore, block, meta, temp, fluid);
                }
        }
    }

    public static void addCasting(String dic, FluidStack fluid, int delay) {
        if (OreDictionary.getOres(dic).size() > 0) {
            TConstructRegistry.getTableCasting().addCastingRecipe(OreDictionary.getOres(dic).get(0), fluid, TConstructRegistry.getItemStack("ingotCast"), delay);
        }
    }

    public static void addBlockCasting(String dic, FluidStack fluid, int delay) {
        if (OreDictionary.getOres(dic).size() > 0) {
            TConstructRegistry.getBasinCasting().addCastingRecipe(OreDictionary.getOres(dic).get(0), fluid, delay);
        }
    }

    public static void addPartCasting(ItemStack output, ItemStack cast, FluidStack fluid, int hardeningDelay) {
        TConstructRegistry.getTableCasting().addCastingRecipe(output, fluid, cast, hardeningDelay);
    }
}
