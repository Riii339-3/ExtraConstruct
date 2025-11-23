package io.github.riiimc.extraconstruct.libs.tiers

import net.minecraft.world.item.Tier
import net.minecraft.world.item.crafting.Ingredient

class FantasyEndingTier:Tier {
    companion object {
        var instance:Tier = FantasyEndingTier()
    }
    override fun getUses(): Int {
        return Int.MAX_VALUE
    }

    override fun getSpeed(): Float {
        return Float.MAX_VALUE
    }

    override fun getEnchantmentValue(): Int {
        return Int.MAX_VALUE
    }

    override fun getRepairIngredient(): Ingredient? {
        return null
    }

    override fun getAttackDamageBonus(): Float {
        return Float.MAX_VALUE
    }

    @Deprecated("Deprecated in Java")
    override fun getLevel(): Int {
        return Int.MAX_VALUE
    }
}