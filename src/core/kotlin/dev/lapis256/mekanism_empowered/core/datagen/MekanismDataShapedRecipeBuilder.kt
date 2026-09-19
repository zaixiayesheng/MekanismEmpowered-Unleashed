package dev.lapis256.mekanism_empowered.core.datagen

import mekanism.common.recipe.upgrade.MekanismShapedRecipe
import net.minecraft.core.Holder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.level.ItemLike


class MekanismDataShapedRecipeBuilder(item: ItemLike, amount: Int = 1, category: RecipeCategory = RecipeCategory.MISC) :
    EmpoweredShapedRecipeBuilder(item, amount, category) {

    constructor(stack: ItemStack, category: RecipeCategory = RecipeCategory.MISC) : this(stack.item, stack.count, category)

    constructor(holder: Holder<Item>, amount: Int = 1, category: RecipeCategory = RecipeCategory.MISC) : this(holder.value(), amount, category)

    override fun wrapRecipe(recipe: ShapedRecipe): Recipe<*> {
        return MekanismShapedRecipe(recipe)
    }
}
