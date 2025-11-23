package io.github.riiimc.extraconstruct.tools.goety

import com.Polarice3.Goety.api.items.magic.IWand
import com.Polarice3.Goety.api.magic.SpellType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import slimeknights.tconstruct.library.tools.definition.ToolDefinition
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay

class GoetyWand: IModifiableDisplay,IWand {
    override fun asItem(): Item {
        TODO("Not yet implemented")
    }

    override fun getToolDefinition(): ToolDefinition {
        TODO("Not yet implemented")
    }

    override fun getSpellType(): SpellType {
        TODO("Not yet implemented")
    }

    override fun getRenderTool(): ItemStack {
        TODO("Not yet implemented")
    }

    override fun isRepairable(p0: ItemStack?): Boolean {
        TODO("Not yet implemented")
    }
}