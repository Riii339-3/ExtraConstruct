package io.github.riiimc.extraconstruct.utils

import io.github.riiimc.extraconstruct.ExtraConstruct
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import slimeknights.mantle.registration.deferred.FluidDeferredRegister
import slimeknights.tconstruct.common.registration.AttributeDeferredRegister
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister

object ExtraRegistries {
    val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtraConstruct.MODID)
    val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtraConstruct.MODID)
    val MANTLE_ATTRIBUTE = AttributeDeferredRegister(ExtraConstruct.MODID)
    val MODIFIER = ModifierDeferredRegister.create(ExtraConstruct.MODID)
    val FLUIDS = FluidDeferredRegister(ExtraConstruct.MODID)
}