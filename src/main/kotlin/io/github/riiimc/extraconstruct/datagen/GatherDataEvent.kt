package io.github.riiimc.extraconstruct.datagen

import io.github.riiimc.extraconstruct.ExtraConstruct
import io.github.riiimc.extraconstruct.datagen.fluid.FluidTextureDataGen
import moffy.addonapi.ModsAvailableCondition
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.DataGenerator
import net.minecraft.data.PackOutput
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import java.util.Set
import java.util.concurrent.CompletableFuture


@Mod.EventBusSubscriber(modid = ExtraConstruct.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
class GatherDataEvent {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator: DataGenerator = event.getGenerator()
        CraftingHelper.register(ModsAvailableCondition.Serializer())
        val packOutput:PackOutput = generator.getPackOutput()
        val lookupProvider: CompletableFuture<Provider> = event.lookupProvider
        val existingFileHelper = event.existingFileHelper
        val registrySetBuilder = RegistrySetBuilder()

        val server = event.includeServer()
        val client = event.includeClient()

        val registryProvider =
            DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of<String>(ExtraConstruct.MODID))
        generator.addProvider(server, registryProvider)

        generator.addProvider(client,FluidTextureDataGen(packOutput));

    }
}