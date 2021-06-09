package com.hamusuke.jece.mixin;

import com.hamusuke.jece.invoker.ServerWorldInvoker;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldInvoker {
    @Shadow
    @Final
    @Nullable
    private EnderDragonFight enderDragonFight;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    public abstract ServerChunkManager getChunkManager();

    private ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }

    public void saveLevel() {
        if (this.enderDragonFight != null) {
            this.server.getSaveProperties().setDragonFight(this.enderDragonFight.toTag());
        }

        this.getChunkManager().getPersistentStateManager().save();
    }
}
