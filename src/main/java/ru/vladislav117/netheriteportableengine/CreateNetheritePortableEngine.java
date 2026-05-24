package ru.vladislav117.netheriteportableengine;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import ru.vladislav117.netheriteportableengine.content.NetheritePortableEngineBlock;
import ru.vladislav117.netheriteportableengine.content.recipe.PortableEngineDyeingRecipe;
import ru.vladislav117.netheriteportableengine.data.NetheritePortableEngineDataGen;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(CreateNetheritePortableEngine.ID)
public class CreateNetheritePortableEngine {
    public static final String ID = "netheriteportableengine";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ID);
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PortableEngineDyeingRecipe>> PORTABLE_ENGINE_DYEING =
            RECIPE_SERIALIZERS.register("portable_engine_dyeing", () -> new SimpleCraftingRecipeSerializer<>(PortableEngineDyeingRecipe::new));
    private static final ResourceLocation NETHERITE_PORTABLE_ENGINE_ID = ResourceLocation.fromNamespaceAndPath(ID, "netherite_portable_engine");
    public static final DeferredBlock<NetheritePortableEngineBlock> NETHERITE_PORTABLE_ENGINE = BLOCKS.register(NETHERITE_PORTABLE_ENGINE_ID.getPath(), () -> new NetheritePortableEngineBlock(BlockBehaviour.Properties.ofFullCopy(SharedProperties.stone())
            .sound(SoundType.NETHERITE_BLOCK)
            .lightLevel(state -> NetheritePortableEngineBlock.isLitState(state) ? 6 : 0)
            .noOcclusion()));
    public static final DeferredItem<BlockItem> NETHERITE_PORTABLE_ENGINE_ITEM = ITEMS.registerSimpleBlockItem(NETHERITE_PORTABLE_ENGINE_ID.getPath(), NETHERITE_PORTABLE_ENGINE);
    private static final ResourceLocation RED_PORTABLE_ENGINE = ResourceLocation.fromNamespaceAndPath(Simulated.MOD_ID, "red_portable_engine");

    public CreateNetheritePortableEngine(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NetheritePortableEngineDataGen::gatherData);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }

    private static PortableEngineBlockEntity createNetheritePortableEngineBlockEntity(BlockPos pos, BlockState state) {
        return new PortableEngineBlockEntity(NETHERITE_PORTABLE_ENGINE_BE.get(), pos, state);
    }    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PortableEngineBlockEntity>> NETHERITE_PORTABLE_ENGINE_BE =
            BLOCK_ENTITY_TYPES.register(NETHERITE_PORTABLE_ENGINE_ID.getPath(), () -> BlockEntityType.Builder
                    .of(CreateNetheritePortableEngine::createNetheritePortableEngineBlockEntity, NETHERITE_PORTABLE_ENGINE.get())
                    .build(null));

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockStressValues.CAPACITIES.register(NETHERITE_PORTABLE_ENGINE.get(), () -> 256.0);
            BlockStressValues.RPM.register(NETHERITE_PORTABLE_ENGINE.get(), new BlockStressValues.GeneratedRpm(32, false));
            registerTooltips();
            addToSimulatedCreativeTab();
        });
    }

    private void registerTooltips() {
        Item item = NETHERITE_PORTABLE_ENGINE_ITEM.get();
        TooltipModifier tooltip = TooltipModifier.mapNull(KineticStats.create(item));
        TooltipModifier.REGISTRY.register(item, tooltip);
    }

    @SuppressWarnings("unchecked")
    private void addToSimulatedCreativeTab() {
        try {
            Class<?> simulatedRegistrate = Class.forName("dev.simulated_team.simulated.registrate.SimulatedRegistrate");
            Field tabItemsField = simulatedRegistrate.getField("TAB_ITEMS");
            Field itemToSectionField = simulatedRegistrate.getField("ITEM_TO_SECTION");
            List<Supplier<Item>> tabItems = (List<Supplier<Item>>) tabItemsField.get(null);
            Map<ResourceLocation, ResourceLocation> itemToSection = (Map<ResourceLocation, ResourceLocation>) itemToSectionField.get(null);

            ResourceLocation redSection = itemToSection.get(RED_PORTABLE_ENGINE);
            if (redSection != null) {
                itemToSection.put(NETHERITE_PORTABLE_ENGINE_ID, redSection);
            } else {
                LOGGER.warn("Failed to find Simulated creative tab section for {}", RED_PORTABLE_ENGINE);
            }

            synchronized (tabItems) {
                if (containsItem(tabItems, NETHERITE_PORTABLE_ENGINE_ID)) {
                    return;
                }

                int redEngineIndex = indexOfItem(tabItems, RED_PORTABLE_ENGINE);
                Supplier<Item> netheriteEngine = NETHERITE_PORTABLE_ENGINE_ITEM::get;
                if (redEngineIndex >= 0) {
                    tabItems.add(redEngineIndex + 1, netheriteEngine);
                } else {
                    tabItems.add(netheriteEngine);
                    LOGGER.warn("Failed to find {} in Simulated creative tab; added {} to the end", RED_PORTABLE_ENGINE, NETHERITE_PORTABLE_ENGINE_ID);
                }
            }
        } catch (ReflectiveOperationException exception) {
            LOGGER.warn("Failed to add {} to the Simulated creative tab", NETHERITE_PORTABLE_ENGINE_ID, exception);
        }
    }

    private boolean containsItem(List<Supplier<Item>> items, ResourceLocation itemId) {
        return indexOfItem(items, itemId) >= 0;
    }

    private int indexOfItem(List<Supplier<Item>> items, ResourceLocation itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (itemId.equals(getItemId(items.get(i)))) {
                return i;
            }
        }
        return -1;
    }

    private ResourceLocation getItemId(Supplier<Item> itemSupplier) {
        try {
            Item item = itemSupplier.get();
            return item == null ? null : BuiltInRegistries.ITEM.getKey(item);
        } catch (RuntimeException exception) {
            return null;
        }
    }


}
