package io.github.riiimc.extraconstruct.modules.eternisstarrysky

import io.github.riiimc.extraconstruct.ExtraConstruct
import io.github.riiimc.extraconstruct.modifiers.eternisstarrysky.PurpleiteGalaxyModifier
import io.github.riiimc.extraconstruct.utils.ExtraRegistries
import io.github.riiimc.extraconstruct.utils.ExtraVariables
import moffy.addonapi.AddonModule
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.fluids.FluidType

class ExtraEternissStarrySky:AddonModule() {
    init {
        ExtraVariables.MOLTEN_PURPLEITE_GALAXY = ExtraRegistries.FLUIDS.register(ExtraConstruct.MODID)
            .type(FluidType.Properties.create().temperature(3500))
            .block(MapColor.COLOR_PURPLE,15)
            .commonTag()
            .bucket()
            .flowing()
        ExtraVariables.PURPLEITE_GALAXY_MODIFIER = ExtraRegistries.MODIFIER.register("purpleite_galaxy",::PurpleiteGalaxyModifier)
        println("EternisStarrySky Module is loaded!")
    }
}