package io.github.riiimc.extraconstruct.utils

import net.minecraft.world.item.Tier
import net.minecraftforge.fluids.ForgeFlowingFluid
import slimeknights.mantle.registration.deferred.FluidDeferredRegister
import slimeknights.mantle.registration.`object`.FlowingFluidObject
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier
import slimeknights.tconstruct.library.modifiers.util.StaticModifier

object ExtraVariables {
    lateinit var FANTASY_ENDING_TIER: Tier
    lateinit var MOLTEN_FANTASY_ENDING: FlowingFluidObject<ForgeFlowingFluid>
    lateinit var FANTASY_ENDING_MODIFIER: StaticModifier<*>
    lateinit var APOCALYPTIUM_MODIFIER:StaticModifier<*>
    lateinit var MOLTEN_APOCALYPTIUM:FlowingFluidObject<ForgeFlowingFluid>
    lateinit var MOLTEN_PURPLEITE_GALAXY:FlowingFluidObject<ForgeFlowingFluid>
    lateinit var PURPLEITE_GALAXY_MODIFIER:StaticModifier<*>
}