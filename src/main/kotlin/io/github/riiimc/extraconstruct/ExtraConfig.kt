package io.github.riiimc.extraconstruct

import io.github.riiimc.extraconstruct.modules.ModuleRegistries
import moffy.addonapi.AddonModuleRegistry
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext


object ExtraConfig {
    lateinit var COMMON_SPEC: ForgeConfigSpec

    fun registerConfig() {
        val builder = ForgeConfigSpec.Builder()

        // モジュール読み込みは builder に対してのみ行う
        AddonModuleRegistry.INSTANCE.LoadModule(ModuleRegistries(), builder)

        // 🔥 builder を build する（これが必須）
        COMMON_SPEC = builder.build()

        // 🔥 Forge に config を登録する（これをやらないと読み込まれない）
        ModLoadingContext.get().registerConfig(
            net.minecraftforge.fml.config.ModConfig.Type.COMMON,
            COMMON_SPEC
        )
    }
}
