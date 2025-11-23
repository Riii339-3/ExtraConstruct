package io.github.riiimc.extraconstruct.modules

import io.github.riiimc.extraconstruct.ExtraConstruct
import io.github.riiimc.extraconstruct.modules.eternisstarrysky.ExtraEternissStarrySky
import io.github.riiimc.extraconstruct.modules.fantasyending.ExtraFantasyEnding
import moffy.addonapi.AddonModuleProvider
import net.minecraft.resources.ResourceLocation

class ModuleRegistries: AddonModuleProvider() {
    override fun registerRawModules() {
        addRawModule(
            ResourceLocation(ExtraConstruct.MODID,"default"),
            "Default",
            ExtraDefault::class.java,
            arrayOf<String>("tconstruct")
        )

        addRawModule(
            ResourceLocation(ExtraConstruct.MODID,"fantasy_ending"),
            "FantasyEnding",
            ExtraFantasyEnding::class.java,
            arrayOf<String>("tconstruct", "fantasy_ending")
        )/*
        addRawModule(
            ResourceLocation(ExtraConstruct.MODID,"revelationfix"),
            "Goety: Revelation",
            ExtraGoetyRevelation::class.java,
            arrayOf("tconstruct","revelationfix")
        )*/
        addRawModule(
            ResourceLocation(ExtraConstruct.MODID,"eternisstarrysky"),
            "EternisStarrySKy",
            ExtraEternissStarrySky::class.java,
            arrayOf("tconstruct","eternisstarrysky")
        )
    }

    override fun getModId(): String {
        return ExtraConstruct.MODID
    }

}