package ru.vladislav117.netheriteportableengine.data;

import dev.simulated_team.simulated.Simulated;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;
import ru.vladislav117.netheriteportableengine.content.recipe.PortableEngineDyeingRecipe;

import java.util.concurrent.CompletableFuture;

public class NetheritePortableEngineRecipeProvider extends RecipeProvider {
    private static final TagKey<Item> COMMON_PORTABLE_ENGINES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CreateNetheritePortableEngine.ID, "common_portable_engine"));

    public NetheritePortableEngineRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output, @NotNull HolderLookup.Provider registries) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(COMMON_PORTABLE_ENGINES),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        CreateNetheritePortableEngine.NETHERITE_PORTABLE_ENGINE_ITEM.get()
                )
                .unlocks("has_common_portable_engine", has(COMMON_PORTABLE_ENGINES))
                .save(output, CreateNetheritePortableEngine.NETHERITE_PORTABLE_ENGINE_ITEM.getRegisteredName());

        SpecialRecipeBuilder.special(PortableEngineDyeingRecipe::new).save(output, ResourceLocation.fromNamespaceAndPath(Simulated.MOD_ID, "crafting/portable_engine_dyeing"));
    }
}
