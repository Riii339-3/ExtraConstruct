package io.github.riiimc.extraconstruct.modifiers.fantasyending

import com.google.common.collect.Collections2
import com.google.common.collect.Lists
import com.mega.uom.Config
import com.mega.uom.common.attribute.ModAttributes
import com.mega.uom.common.capability.ModCapabilities
import com.mega.uom.common.capability.entity.runic.IRunicShieldCapability
import com.mega.uom.common.damagesource.ModDamageSources
import com.mega.uom.common.items.armor.FantasyEndingArmorItem
import com.mega.uom.common.items.armor.FantasyEndingArmorItem.shield_damage_reduction
import com.mega.uom.common.items.combat.sword.FantasyEndingSword
import com.mega.uom.common.network.PacketHandler
import com.mega.uom.common.network.s2c.entity.EntityCapabilitySyncPacket
import com.mega.uom.common.network.s2c.render.RGBStrikeRendererPacket
import com.mega.uom.common.register.ModSoundEvents
import com.mega.uom.compat.SafeClass
import com.mega.uom.compat.WrappedClient
import com.mega.uom.util.entity.EntityASMUtil
import com.mega.uom.util.entity.EntityActuallyHurt
import com.mega.uom.util.helper.FlashSoundPlayer
import io.github.riiimc.extraconstruct.utils.ExtraVariables
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraftforge.entity.PartEntity
import net.minecraftforge.fml.common.Mod
import slimeknights.tconstruct.library.modifiers.ModifierEntry
import slimeknights.tconstruct.library.modifiers.ModifierHooks
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier
import slimeknights.tconstruct.library.module.ModuleHookMap
import slimeknights.tconstruct.library.tools.context.ToolAttackContext
import slimeknights.tconstruct.library.tools.item.IModifiable
import slimeknights.tconstruct.library.tools.nbt.IToolStackView
import slimeknights.tconstruct.library.tools.nbt.ToolStack

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
class FantasyEndingModifier:NoLevelsModifier(), MeleeHitModifierHook, InventoryTickModifierHook,EquipmentChangeModifierHook {
    override fun registerHooks(hookBuilder: ModuleHookMap.Builder) {
        super.registerHooks(hookBuilder)
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK,ModifierHooks.EQUIPMENT_CHANGE)
    }


    override fun getPriority(): Int {
        return 999
    }

    override fun onInventoryTick(
        tool: IToolStackView,
        entry: ModifierEntry,
        level: Level,
        entity: LivingEntity,
        itemSlot: Int,
        isSelected: Boolean,
        isCorrectSlot: Boolean,
        stack: ItemStack
    ) {
        lateinit var player: Player
        if (entity !is Player) {
            return
        }
        else {
            player = entity
            if (ToolStack.from(player.getItemBySlot(EquipmentSlot.HEAD)).getModifierLevel(ExtraVariables.FANTASY_ENDING_MODIFIER.get()) > 0 ) {
                if (!level.isClientSide) {
                    player.setAirSupply(300)
                    player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 1440, 0, false, false))
                }
            } else if (ToolStack.from(player.getItemBySlot(EquipmentSlot.CHEST)).getModifierLevel(ExtraVariables.FANTASY_ENDING_MODIFIER.get()) > 0 ) {
                player.getAbilities().mayfly = true
            } else if (ToolStack.from(player.getItemBySlot(EquipmentSlot.LEGS)).getModifierLevel(ExtraVariables.FANTASY_ENDING_MODIFIER.get()) > 0 ) {
                player.wasOnFire = false
                // player.hasVisualFire = false
                player.setRemainingFireTicks(0)
                player.clearFire()
            }
        }
        val tag = stack.getOrCreateTag()
        if (tag.getInt(FantasyEndingSword.COMBO_COOLTIME_TAG) <= 0) {
            tag.putInt(FantasyEndingSword.COMBO_COOLTIME_TAG, 0)

            tag.putInt(FantasyEndingSword.COMBO_TAG, Mth.clamp(tag.getInt(FantasyEndingSword.COMBO_TAG) - 1, 0, 16))
        } else if (tag.getInt(FantasyEndingSword.COMBO_COOLTIME_TAG) > 0) tag.putInt(
            FantasyEndingSword.COMBO_COOLTIME_TAG,
            tag.getInt(FantasyEndingSword.COMBO_COOLTIME_TAG) - 1
        )

        val armorSlots = listOf(
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        )
        if (hasFantasyEndingHead(entity) && hasFantasyEndingChest(entity) && hasFantasyEndingFeet(entity) && hasFantasyEndingLegs(entity)) {
            updateRunicShield(entity, shield_damage_reduction,level)
        }
    }

    override fun beforeMeleeHit(
        tool: IToolStackView,
        modifier: ModifierEntry,
        context: ToolAttackContext,
        damage: Float,
        baseKnockback: Float,
        knockback: Float
    ): Float {
        var entity = context.target
        val player:Player = context.playerAttacker!!
        val stack = player.getItemBySlot(EquipmentSlot.MAINHAND)
        if (entity is PartEntity<*>) entity = entity.getParent()
        if (entity.isAlive()) {
            FlashSoundPlayer.randPlaySound(player)
            if (stack.getOrCreateTag()
                    .getInt(FantasyEndingSword.COMBO_TAG) > 5 && Math.random() > 0.5f
            ) player.playSound(
                ModSoundEvents.COMBO_1.get(),
                .15f,
                ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2f + 1.0f) / 0.8f
            )
        }
        //if (entity is LivingEntity) entity.lastHurtByPlayer = player
        onSwordClickEntity(stack, player, entity)
        return super.beforeMeleeHit(tool, modifier, context, damage, baseKnockback, knockback)
    }
    override fun afterMeleeHit(
        tool: IToolStackView,
        modifier: ModifierEntry,
        context: ToolAttackContext,
        damageDealt: Float
    ) {
        val source = context.attacker
        val entity = context.target
        val itemStack:ItemStack = source.getItemBySlot(EquipmentSlot.MAINHAND)
        //float distance = 1.85f;
        //Vec3 hitLocation = source.position().add(0.0, source.getBbHeight() * 0.3F, 0.0).add(source.getForward().multiply(distance, 0.3499999940395355, distance));
        /*
        FlameStrike flameStrike = new FlameStrike(level, Math.random() > 0.5F);
        flameStrike.moveTo(hitLocation);
        flameStrike.setYRot(source.getYRot());
        flameStrike.setXRot(source.getRandom().nextFloat() * 60F - 30F);
        level.addFreshEntity(flameStrike);
         */
        if (source !is Player) {
            onSwordClickEntity(itemStack, source, entity)
        }
        val tag: CompoundTag = itemStack.getOrCreateTag()
        tag.putInt(FantasyEndingSword.COMBO_TAG, tag.getInt(FantasyEndingSword.COMBO_TAG) + 1)
        tag.putInt(FantasyEndingSword.COMBO_COOLTIME_TAG, 40)
        super.afterMeleeHit(tool, modifier, context, damageDealt)
    }

    private fun onSwordClickEntity(stack: ItemStack, source: LivingEntity, entity: Entity):Boolean {
        return onFeWeaponClickEntity(stack, source, entity, true, true)

    }
    private fun onFeWeaponClickEntity(
        stack: ItemStack,
        source: LivingEntity,
        entity: Entity,
        hasStrike: Boolean,
        swingModify: Boolean
    ): Boolean {
        if (source.level().isClientSide) return false
        val f2 = if (source is Player) source.getAttackStrengthScale(0.5f) else 0.8f
        var f = 0.2f + f2 * f2 * 0.8f
        if (!swingModify) f = 1f
        val radius = 3.25f
        val distance = 1.85f
        val hitLocation = source.position().add(0.0, (source.bbHeight * 0.3f).toDouble(), 0.0)
            .add(source.forward.multiply(distance.toDouble(), 0.3499999940395355, distance.toDouble()))
        val level = source.level()
        var entities = level.getEntities(
            source,
            AABB.ofSize(hitLocation, (radius * 2.0f).toDouble(), radius.toDouble(), (radius * 2.0f).toDouble())
        )
        if (stack.item is FantasyEndingSword && source is Player) {
            val feSword = stack.item as FantasyEndingSword
            entities = feSword.handledEntities(entities, stack)
        }

        if (hasStrike) {
            PacketHandler.sendToAll(
                RGBStrikeRendererPacket(
                    hitLocation.toVector3f(),
                    source.xRot + source.random.nextFloat() * 60f - 30f,
                    source.yRot
                )
            )
        }
        var hasTag = false
        val feSource = ModDamageSources.causeDeathFeDamage(source)
        if (entity is LivingEntity) {
            //entity.lastDamageSource = feSource
            hasTag = EntityActuallyHurt.died(entity)
        } else if (entity !is PartEntity<*> && entity.isAlive) {
            entity.setRemoved(Entity.RemovalReason.DISCARDED)
            entity.invalidateCaps()
            entity.gameEvent(GameEvent.ENTITY_DIE)
            //防止意外
            entity.kill()
        }
        FantasyEndingSword.sweepDealDamage(entities, source, 8.0f * f)
        if (entity is LivingEntity) {
            //entity.lastDamageSource = feSource
            val tag = stack.getOrCreateTag()
            tag.putInt("DamagedEntityByFeSword", tag.getInt("DamagedEntityByFeSword") + 1)
            val hurt = EntityActuallyHurt(entity)
            if (!entity.getType().descriptionId.startsWith("entity.dummmmmmy")) {
                if (!SafeClass.isGoetyRevLoaded() || !SafeClass.isApollyon(entity)) EntityASMUtil.addDelta(
                    entity, -((15 + source.getAttributeValue(
                        ModAttributes.getFeDamage()
                    )
                        .toFloat() / 10f + source.random.nextInt(5) + entity.maxHealth * 0.005f + entity.health * 0.005f) * f)
                )
                if (!entity.isDeadOrDying) hurt.actuallyHurt(
                    feSource,
                    (15 + source.random.nextInt(5) + entity.maxHealth * 0.005f + entity.health * 0.005f) * f,
                    true
                )
                //entity.lastDamageSource = feSource
            }
            if (!entity.isDeadOrDying && entity.deathTime > 0) entity.deathTime = 0
            if (entity.isDeadOrDying && (!hasTag)) hurt.playDeathSound()
        }
        return true
    }
    fun updateRunicShield(living: LivingEntity, shieldDamageReduction: Float, level: Level) {
        if (living.deathTime > 0 && FantasyEndingArmorItem.shouldNotDead(living)) living.deathTime = 0
        if (!level.isClientSide) {
            val effects: List<MobEffectInstance> = Lists.newArrayList(living.activeEffects)
            for (effectInstance in Collections2.filter(
                effects
            ) { e: MobEffectInstance -> e.effect.category == MobEffectCategory.HARMFUL }) living.removeEffect(
                effectInstance.effect
            )
            val delta = EntityASMUtil.getHealthDelta(living)
            if (delta <= -1) EntityASMUtil.addDelta(living, .1f)
            else if (delta >= 1) EntityASMUtil.addDelta(living, -.1f)
            else if (delta < 1) EntityASMUtil.setHealthDelta(living, 0f)
            val state: IRunicShieldCapability? = ModCapabilities.getCapability(living, ModCapabilities.FE_SHIELD_EC)
            println("Attach check: state=$state, tick=${living.tickCount}, entity=${living}, side=${living.level().isClientSide}")

            if (state != null) {
                if (living.tickCount % 10 == 0) {
                    state.setMaxLifeTime((1 + 1024 * 20).toLong())
                    state.perCooldowns = 20
                    state.damageResistance = shield_damage_reduction
                    state.outResistance = 0.25f
                    state.setSpawnRunic(false)
                    state.level = 4936.toShort()
                    println(state.isDirty)
                    val player = living as? ServerPlayer
                    if (player != null && state.isDirty()) {
                        PacketHandler.sendToPlayer(
                            player,
                            EntityCapabilitySyncPacket(
                                living.id,
                                ModCapabilities.ID_CAP_FE_SHIELD_EC.id,
                                state.serializeNBT()
                            )
                        )
                        println("complete: Dirty = ${state.isDirty}")
                    }

                }
            }
        }
        if (living is Player) {
            if (living.isDeadOrDying()) {
                if (FantasyEndingArmorItem.shouldNotDead(living)) {
                    if (!level.isClientSide) {
                        living.giveExperiencePoints((-FantasyEndingArmorItem.getRespawnXP(living) * 10).toInt())
                        (living as ServerPlayer).connection.send(
                            ClientboundSetExperiencePacket(
                                living.experienceProgress,
                                living.totalExperience,
                                living.experienceLevel
                            )
                        )
                        PacketHandler.playSound(living, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 3f, 0.8f)
                        living.setHealth(FantasyEndingArmorItem.getRespawnXP(living))
                        EntityASMUtil.setHealthDelta(living, 0f)
                    }
                    living.deathTime = 0
                }
            }
            living.setArrowCount(0)
            if (level.isClientSide && Config.Client.fe_armor_no_delay) WrappedClient.setClickDelay(0)
        }
    }




    private fun hasFantasyEndingHead(entity: LivingEntity): Boolean {
        val headStack = entity.getItemBySlot(EquipmentSlot.HEAD)
        if (headStack.item is IModifiable) {
            val head = ToolStack.from(headStack)
            //println("head detected")
            return ExtraVariables.FANTASY_ENDING_MODIFIER.get()?.let { modifier ->
                // 'it' (または 'modifier') を使ってレベルをチェックする
                head.getModifierLevel(modifier) > 0 // <-- これが最後の式。Boolean を返す
            } ?: false
        }
        return false
    }
    private fun hasFantasyEndingChest(entity: LivingEntity): Boolean {
        val headStack = entity.getItemBySlot(EquipmentSlot.CHEST)
        if (headStack.item is IModifiable) {
            val chest = ToolStack.from(headStack)
            //println("chest detected")
            return ExtraVariables.FANTASY_ENDING_MODIFIER.get()?.let { modifier ->
                // 'it' (または 'modifier') を使ってレベルをチェックする
                chest.getModifierLevel(modifier) > 0 // <-- これが最後の式。Boolean を返す
            } ?: false // .get() が null だった場合は false を返す
        }
        return false
    }
    private fun hasFantasyEndingLegs(entity: LivingEntity): Boolean {
        val headStack = entity.getItemBySlot(EquipmentSlot.LEGS)
        if (headStack.item is IModifiable) {
            val legs = ToolStack.from(headStack)
            //println("legs detected")
            return ExtraVariables.FANTASY_ENDING_MODIFIER.get()?.let { modifier ->
                // 'it' (または 'modifier') を使ってレベルをチェックする
                legs.getModifierLevel(modifier) > 0 // <-- これが最後の式。Boolean を返す
            } ?: false
        }
        return false
    }
    private fun hasFantasyEndingFeet(entity: LivingEntity): Boolean {
        val headStack = entity.getItemBySlot(EquipmentSlot.FEET)
        if (headStack.item is IModifiable) {
            val feet = ToolStack.from(headStack)
            //println("feet detected")
            return ExtraVariables.FANTASY_ENDING_MODIFIER.get()?.let { modifier ->
                // 'it' (または 'modifier') を使ってレベルをチェックする
                feet.getModifierLevel(modifier) > 0 // <-- これが最後の式。Boolean を返す
            } ?: false
        }
        return false
    }

    private fun onFantasyEndingAttack(attacker:Entity) {
        if (attacker is Player &&
            ToolStack.from(attacker.getItemBySlot(EquipmentSlot.MAINHAND)).getModifierLevel(ExtraVariables.FANTASY_ENDING_MODIFIER.get()) > 0
            ) {

        }
    }

}