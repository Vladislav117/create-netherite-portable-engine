package ru.vladislav117.netheriteportableengine.content.recipe;

import dev.simulated_team.simulated.Simulated;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;

public class PortableEngineDyeingRecipe extends CustomRecipe {
    public static final TagKey<Item> COMMON_PORTABLE_ENGINES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CreateNetheritePortableEngine.ID, "common_portable_engine"));

    public PortableEngineDyeingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        int engines = 0;
        int dyes = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(COMMON_PORTABLE_ENGINES)) {
                engines++;
            } else if (stack.is(Tags.Items.DYES)) {
                dyes++;
            } else {
                return false;
            }

            if (engines > 1 || dyes > 1) {
                return false;
            }
        }

        return engines == 1 && dyes == 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, @NotNull HolderLookup.Provider registries) {
        ItemStack engine = ItemStack.EMPTY;
        DyeColor color = DyeColor.RED;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(COMMON_PORTABLE_ENGINES)) {
                engine = stack;
            } else {
                DyeColor dyeColor = DyeColor.getColor(stack);
                if (dyeColor != null) {
                    color = dyeColor;
                }
            }
        }

        ItemStack dyedEngine = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(Simulated.MOD_ID, color.getSerializedName() + "_portable_engine")).getDefaultInstance();
        if (!engine.isComponentsPatchEmpty()) {
            dyedEngine.applyComponents(engine.getComponentsPatch());
        }

        return dyedEngine;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CreateNetheritePortableEngine.PORTABLE_ENGINE_DYEING.get();
    }
}
