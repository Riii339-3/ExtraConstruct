package io.github.riiimc.extraconstruct.modules

import io.github.riiimc.extraconstruct.utils.ExtraRegistries
import moffy.addonapi.AddonModule
import net.minecraft.world.item.Item
import thedarkcolour.kotlinforforge.forge.registerObject

class ExtraDefault:AddonModule() {
    init {
        val DEFAULT_ITEM by ExtraRegistries.ITEMS.registerObject("default_item") {
            Item(Item.Properties().stacksTo(1))
        }
        println("Default Module is loaded!")
    }

}