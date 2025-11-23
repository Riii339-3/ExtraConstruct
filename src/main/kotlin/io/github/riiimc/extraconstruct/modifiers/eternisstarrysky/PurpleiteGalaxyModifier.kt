package io.github.riiimc.extraconstruct.modifiers.eternisstarrysky

import com.baizeli.Sounds
import com.baizeli.eternisstarrysky.EntityMarker
import com.baizeli.eternisstarrysky.Items.InfinitySword
import com.baizeli.eternisstarrysky.Items.InfinitySwordTrue
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent
import slimeknights.tconstruct.library.modifiers.ModifierEntry
import slimeknights.tconstruct.library.modifiers.ModifierHooks
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier
import slimeknights.tconstruct.library.module.ModuleHookMap
import slimeknights.tconstruct.library.tools.context.ToolAttackContext
import slimeknights.tconstruct.library.tools.nbt.IToolStackView
import slimeknights.tconstruct.library.tools.nbt.ToolStack
import java.util.function.Consumer
import java.util.function.Predicate

class PurpleiteGalaxyModifier:NoLevelsModifier(), MeleeHitModifierHook {
    private var KILL_RADIUS:Double = 5.0
    fun onInterMod(event: InterModProcessEvent) {
        val clazz = Class.forName("com.baizeli.eternisstarrysky")
        val field = clazz.getDeclaredField("KILL_RADIUS").apply { isAccessible = true }

        KILL_RADIUS = field.get(null as Number).toString().toDouble()
    }

    override fun registerHooks(hookBuilder: ModuleHookMap.Builder) {
        super.registerHooks(hookBuilder)
        hookBuilder.addHook(this,  ModifierHooks.MELEE_HIT)
    }
    override fun getPriority(): Int {
        return Int.MAX_VALUE
    }

    override fun beforeMeleeHit(
        tool: IToolStackView,
        modifier: ModifierEntry,
        context: ToolAttackContext,
        damage: Float,
        baseKnockback: Float,
        knockback: Float
    ): Float {
        val attacker: LivingEntity = context.attacker
        val entity: Entity = context.target
        val player: Player? = context.playerAttacker
        val stack: ItemStack? = player?.getItemBySlot(EquipmentSlot.MAINHAND)
        if (player !is ServerPlayer) {
            Sounds.play(SoundEvents.AMETHYST_BLOCK_STEP, player, 10.0f, 1.0f)
            if (entity is LivingEntity) EntityMarker.mark(entity, EntityMarker.ENTITY_DATA_HEALTH)
            return 0f
        }

        if (entity !is LivingEntity) return 0f

        val voidDamage: DamageSource = DamageSource(entity.damageSources().fellOutOfWorld().typeHolder(), player)
        val predicate =
            Predicate<LivingEntity> { e: LivingEntity -> e.id != player.getId() }
        val nearbyEntities = InfinitySword.getNearbyLivingEntities(entity, KILL_RADIUS, predicate)
        nearbyEntities.forEach(Consumer<LivingEntity> { e: LivingEntity ->
            player.crit(e)
            e.hurt(voidDamage, Float.MAX_VALUE)
        })

        InfinitySword.sweep(player, entity, stack, Float.MAX_VALUE.toDouble())

        player.resetAttackStrengthTicker()

        EntityMarker.mark(entity, EntityMarker.ENTITY_DATA_HEALTH)
        return super.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback)
    }
}