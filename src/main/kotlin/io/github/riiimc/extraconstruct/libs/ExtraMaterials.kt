package io.github.riiimc.extraconstruct.libs

import io.github.riiimc.extraconstruct.ExtraConstruct
import slimeknights.tconstruct.library.materials.definition.MaterialId
import java.util.*

object ExtraMaterials {
    val FANTASY_ENDING: MaterialId? = Objects.requireNonNull(MaterialId.tryBuild(ExtraConstruct.MODID,"fantasy_ending"))
}