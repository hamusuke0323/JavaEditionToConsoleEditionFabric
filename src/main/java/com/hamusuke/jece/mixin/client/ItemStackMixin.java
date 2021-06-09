package com.hamusuke.jece.mixin.client;

import com.google.common.collect.*;
import com.google.gson.JsonParseException;
import com.hamusuke.jece.invoker.client.ItemInvoker;
import com.hamusuke.jece.invoker.client.ItemStackInvoker;
import com.hamusuke.jece.client.util.CEUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.DecimalFormat;
import java.util.*;

@Mixin(ItemStack.class)
@Environment(EnvType.CLIENT)
public abstract class ItemStackMixin implements ItemStackInvoker {
    @Shadow
    public abstract Text getName();

    @Shadow
    public abstract Rarity getRarity();

    @Shadow
    public abstract boolean hasCustomName();

    @Shadow
    public abstract Item getItem();

    @Shadow
    protected abstract int getHideFlags();

    @Shadow
    private static boolean isSectionHidden(int flags, ItemStack.TooltipSection tooltipSection) {
        return false;
    }

    @Shadow
    public abstract boolean hasTag();

    @Shadow
    public abstract ListTag getEnchantments();

    @Shadow
    private CompoundTag tag;

    @Shadow
    @Final
    private static Style LORE_STYLE;

    @Shadow
    public abstract Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot);

    @Shadow
    @Final
    public static DecimalFormat MODIFIER_FORMAT;

    @Shadow
    private static Collection<Text> parseBlockTag(String tag) {
        return null;
    }

    @Shadow
    public abstract boolean isDamaged();

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    public List<Text> getHandedTooltip(@Nullable PlayerEntity playerEntity, TooltipContext tooltipContext) {
        return this.addTooltip(playerEntity, tooltipContext, true);
    }

    @Inject(method = "getTooltip", at = @At("HEAD"), cancellable = true)
    public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        cir.setReturnValue(this.addTooltip(player, context, false));
        cir.cancel();
    }

    private List<Text> addTooltip(PlayerEntity player, TooltipContext context, boolean isHand) {
        List<Text> list = Lists.newArrayList();
        MutableText mutableText = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            mutableText.formatted(Formatting.GOLD);
        }

        list.add(mutableText);
        if (!context.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add((new LiteralText("#" + FilledMapItem.getMapId((ItemStack) (Object) this))).formatted(Formatting.WHITE));
        }

        int i = this.getHideFlags();
        if (isSectionHidden(i, ItemStack.TooltipSection.ADDITIONAL)) {
            this.getItem().appendTooltip((ItemStack) (Object) this, player == null ? null : player.world, list, context);
        }

        int j;
        if (this.hasTag()) {
            if (isSectionHidden(i, ItemStack.TooltipSection.ENCHANTMENTS)) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }

            if (this.tag.contains("display", 10)) {
                CompoundTag compoundTag = this.tag.getCompound("display");
                if (isSectionHidden(i, ItemStack.TooltipSection.DYE) && compoundTag.contains("color", 99)) {
                    if (context.isAdvanced()) {
                        list.add((new TranslatableText("item.color", String.format("#%06X", compoundTag.getInt("color")))).formatted(Formatting.WHITE));
                    } else {
                        list.add((new TranslatableText("item.dyed")).formatted(Formatting.WHITE, Formatting.ITALIC));
                    }
                }

                if (compoundTag.getType("Lore") == 9) {
                    ListTag listTag = compoundTag.getList("Lore", 8);

                    for (j = 0; j < listTag.size(); ++j) {
                        String string = listTag.getString(j);

                        try {
                            MutableText mutableText2 = Text.Serializer.fromJson(string);
                            if (mutableText2 != null) {
                                list.add(Texts.setStyleIfAbsent(mutableText2, LORE_STYLE));
                            }
                        } catch (JsonParseException var19) {
                            compoundTag.remove("Lore");
                        }
                    }
                }
            }
        }

        int l;
        if (isSectionHidden(i, ItemStack.TooltipSection.MODIFIERS)) {
            EquipmentSlot[] var20 = EquipmentSlot.values();
            l = var20.length;

            for (j = 0; j < l; ++j) {
                EquipmentSlot equipmentSlot = var20[j];
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(equipmentSlot);
                if (!multimap.isEmpty()) {
                    list.add(LiteralText.EMPTY);

                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        double d = entityAttributeModifier.getValue();
                        boolean bl = false;
                        if (player != null) {
                            if (entityAttributeModifier.getId() == ((ItemInvoker) this.getItem()).ATTACK_DAMAGE_MODIFIER_ID()) {
                                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                                d += EnchantmentHelper.getAttackDamage((ItemStack) (Object) this, EntityGroup.DEFAULT);
                                bl = true;
                            } else if (entityAttributeModifier.getId() == ((ItemInvoker) this.getItem()).ATTACK_SPEED_MODIFIER_ID()) {
                                d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                                bl = true;
                            }
                        }

                        double g;
                        if (entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (entry.getKey().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
                                g = d * 10.0D;
                            } else {
                                g = d;
                            }
                        } else {
                            g = d * 100.0D;
                        }

                        if (bl) {
                            list.add(new LiteralText(" ").append(new TranslatableText("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(entry.getKey().getTranslationKey()))).styled(ItemStackMixin::appendSkyBlue));
                        } else if (d > 0.0D) {
                            list.add(new TranslatableText("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(entry.getKey().getTranslationKey())).styled(ItemStackMixin::appendSkyBlue));
                        } else if (d < 0.0D) {
                            g *= -1.0D;
                            list.add((new TranslatableText("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(entry.getKey().getTranslationKey()))).formatted(Formatting.RED));
                        }
                    }
                }
            }
        }

        if (this.hasTag()) {
            if (isSectionHidden(i, ItemStack.TooltipSection.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                list.add((new TranslatableText("item.unbreakable")).formatted(Formatting.WHITE));
            }

            ListTag listTag3;
            if (isSectionHidden(i, ItemStack.TooltipSection.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
                listTag3 = this.tag.getList("CanDestroy", 8);
                if (!listTag3.isEmpty()) {
                    list.add(LiteralText.EMPTY);
                    list.add((new TranslatableText("item.canBreak")).formatted(Formatting.WHITE));

                    for (l = 0; l < listTag3.size(); ++l) {
                        list.addAll(parseBlockTag(listTag3.getString(l)));
                    }
                }
            }

            if (isSectionHidden(i, ItemStack.TooltipSection.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
                listTag3 = this.tag.getList("CanPlaceOn", 8);
                if (!listTag3.isEmpty()) {
                    list.add(LiteralText.EMPTY);
                    list.add((new TranslatableText("item.canPlace")).formatted(Formatting.WHITE));

                    for (l = 0; l < listTag3.size(); ++l) {
                        list.addAll(parseBlockTag(listTag3.getString(l)));
                    }
                }
            }
        }

        if (context.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
            }

            if (!isHand) {
                list.add((new LiteralText(Registry.ITEM.getId(this.getItem()).toString())).formatted(Formatting.WHITE));
                if (this.hasTag()) {
                    list.add((new TranslatableText("item.nbt_tags", this.tag.getKeys().size())).formatted(Formatting.WHITE));
                }
            }
        }

        return list;
    }

    private static Style appendSkyBlue(Style style) {
        return style.withColor(CEUtil.SKY_BLUE);
    }
}
