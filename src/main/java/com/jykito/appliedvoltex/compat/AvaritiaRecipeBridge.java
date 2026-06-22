package com.jykito.appliedvoltex.compat;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import committee.nova.mods.avaritia.common.crafting.recipe.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.NoConsumeCatalystShapedRecipe;
import committee.nova.mods.avaritia.init.registry.ModItems;
import committee.nova.mods.avaritia.init.registry.ModRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class AvaritiaRecipeBridge {

    private AvaritiaRecipeBridge() {
    }

    public static List<EncoderEntry> listRecipes(Level level) {
        List<EncoderEntry> out = new ArrayList<>();
        for (ITierCraftingRecipe r : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CRAFTING_TABLE_RECIPE.get())) {
            ItemStack result = r.getResultItem(level.registryAccess());
            if (!result.isEmpty()) {
                out.add(new EncoderEntry(r.getId(), result));
            }
        }
        return out;
    }

    public static ItemStack encode(Level level, ResourceLocation recipeId) {
        var opt = level.getRecipeManager().byKey(recipeId);
        if (opt.isEmpty() || !(opt.get() instanceof ITierCraftingRecipe r)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = r.getResultItem(level.registryAccess());
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        boolean noConsumeCatalyst = r instanceof NoConsumeCatalystShapedRecipe;

        List<GenericStack> inputs = new ArrayList<>();
        List<GenericStack> outputs = new ArrayList<>();
        outputs.add(GenericStack.fromItemStack(result));

        for (Ingredient ing : r.getIngredients()) {
            if (ing.isEmpty()) continue;
            ItemStack[] items = ing.getItems();
            if (items.length == 0 || items[0].isEmpty()) continue;
            ItemStack rep = items[0];
            inputs.add(GenericStack.fromItemStack(rep));

            if (noConsumeCatalyst && rep.is(ModItems.infinity_catalyst.get())) {
                outputs.add(GenericStack.fromItemStack(rep));
            }
        }

        if (inputs.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return PatternDetailsHelper.encodeProcessingPattern(
                inputs.toArray(new GenericStack[0]), outputs.toArray(new GenericStack[0]));
    }
}
