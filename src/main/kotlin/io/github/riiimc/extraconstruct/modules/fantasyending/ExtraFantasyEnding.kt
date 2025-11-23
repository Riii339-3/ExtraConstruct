package io.github.riiimc.extraconstruct.modules.fantasyending

import com.mega.uom.common.items.ModTiers
import io.github.riiimc.extraconstruct.ExtraConstruct
import io.github.riiimc.extraconstruct.libs.tiers.FantasyEndingTier
import io.github.riiimc.extraconstruct.modifiers.fantasyending.FantasyEndingModifier
import io.github.riiimc.extraconstruct.utils.ExtraRegistries
import io.github.riiimc.extraconstruct.utils.ExtraVariables
import moffy.addonapi.AddonModule
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.item.Item
import net.minecraft.world.item.Tiers
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.common.TierSortingRegistry
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.registerObject


class ExtraFantasyEnding:AddonModule() {
    init {
        val TEST_ITEM by ExtraRegistries.ITEMS.registerObject("test_item") {
            Item(Item.Properties().stacksTo(1))
        }
        val FANTASYENDING_ATTRIBUTE:RegistryObject<Attribute> =
            ExtraRegistries.MANTLE_ATTRIBUTE.register(
                "fantasyending",0.0,0.0,4.0,true
            )
        ExtraVariables.FANTASY_ENDING_MODIFIER = ExtraRegistries.MODIFIER.register(
            "fantasy_ending",::FantasyEndingModifier
        )
        if (TierSortingRegistry.isTierSorted(ModTiers.FANTASY_ENDING)) {
            ExtraVariables.FANTASY_ENDING_TIER = TierSortingRegistry.registerTier(
                FantasyEndingTier.instance,
                ResourceLocation(ExtraConstruct.MODID, "fantasy_ending"),
                listOf<Any>(TierSortingRegistry.getSortedTiers()[TierSortingRegistry.getSortedTiers().size - 1]),
                listOf<Any>()
            )
        } else {
            ExtraVariables.FANTASY_ENDING_TIER = TierSortingRegistry.registerTier(
                FantasyEndingTier.instance,
                ResourceLocation(ExtraConstruct.MODID, "fantasy_ending"),
                listOf<Any>(Tiers.NETHERITE),
                listOf<Any>()
            )
        }
        ExtraVariables.MOLTEN_FANTASY_ENDING = ExtraRegistries.FLUIDS.register("molten_fantasy_ending")
            .type(FluidType.Properties.create().temperature(3500))
            .block(MapColor.COLOR_LIGHT_BLUE,15)
            .commonTag()
            .bucket()
            .flowing()
        println("FantasyEnding Module is loaded!")
    }
}