package ru.vladislav117.netheriteportableengine.data;

import dev.simulated_team.simulated.Simulated;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import ru.vladislav117.netheriteportableengine.CreateNetheritePortableEngine;
import ru.vladislav117.netheriteportableengine.content.recipe.PortableEngineDyeingRecipe;

import java.util.concurrent.CompletableFuture;

public class NetheritePortableEngineItemTagsProvider extends ItemTagsProvider {
    public NetheritePortableEngineItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CreateNetheritePortableEngine.ID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        TagsProvider.TagAppender<Item> engines = tag(PortableEngineDyeingRecipe.COMMON_PORTABLE_ENGINES);
        for (DyeColor color : DyeColor.values()) {
            engines.add(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Simulated.MOD_ID, color.getName() + "_portable_engine")));
        }
    }
}
