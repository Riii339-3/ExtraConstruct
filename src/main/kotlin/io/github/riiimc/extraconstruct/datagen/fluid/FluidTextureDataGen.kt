package io.github.riiimc.extraconstruct.datagen.fluid

import io.github.riiimc.extraconstruct.ExtraConstruct
import io.github.riiimc.extraconstruct.utils.ExtraVariables
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider
import slimeknights.mantle.fluid.texture.FluidTexture
import slimeknights.mantle.registration.`object`.FluidObject


class FluidTextureDataGen(packOutput: PackOutput):AbstractFluidTextureProvider(packOutput,ExtraConstruct.MODID) {
    private val MOLTEN_LENGTH: Int = "molten_".length

    override fun getName(): String {
        return "ExCon Fluid Textures"
    }

    override fun addTextures() {
        ExtraVariables.MOLTEN_FANTASY_ENDING?.let { molten(it) }
    }
    private fun named(fluid: FluidObject<*>, name: String): FluidTexture.Builder {
        return texture(fluid).textures(ResourceLocation(ExtraConstruct.MODID, "fluid/$name/"), false, false)
    }

    private fun molten(fluid: FluidObject<*>): FluidTexture.Builder {
        return named(fluid, "molten/" + withoutMolten(fluid))
    }

    fun withoutMolten(fluid: FluidObject<*>): String {
        return fluid.id.path.substring(MOLTEN_LENGTH)
    }
}