package dev.lapis256.mekanism_empowered.core.datagen

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.Holder
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.conditions.ICondition
import java.util.*


open class EmpoweredShapedRecipeBuilder(val stack: ItemStack, val category: RecipeCategory = RecipeCategory.MISC) : ShapedRecipeBuilder(category, stack) {

    constructor(item: ItemLike, amount: Int = 1, category: RecipeCategory = RecipeCategory.MISC) : this(ItemStack(item, amount), category)

    constructor(holder: Holder<Item>, amount: Int = 1, category: RecipeCategory = RecipeCategory.MISC) : this(holder.value(), amount, category)

    private val conditions = mutableListOf<ICondition>()

    fun addCondition(condition: ICondition) = apply { conditions.add(condition) }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val advancementHolder = if (!criteria.isEmpty()) {
            val builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR)
            this.criteria.forEach(builder::addCriterion)
            builder.build(id.withPrefix("recipes/"))
        } else { null }

        val recipe = wrapRecipe(ShapedRecipe(
            Objects.requireNonNullElse<String>(group, ""),
            RecipeBuilder.determineBookCategory(category),
            ShapedRecipePattern.of(key, rows),
            stack,
            showNotification
        ))

        recipeOutput.accept(id, recipe, advancementHolder, *conditions.toTypedArray())
    }

    protected open fun wrapRecipe(recipe: ShapedRecipe): Recipe<*> = recipe
}
