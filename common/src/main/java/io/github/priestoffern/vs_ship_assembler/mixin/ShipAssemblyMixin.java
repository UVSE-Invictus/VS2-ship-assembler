package io.github.priestoffern.vs_ship_assembler.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import io.github.priestoffern.vs_ship_assembler.VsShipAssemblerTags;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import static io.github.priestoffern.vs_ship_assembler.physicify.ShipPhysicifyKt.physicifyBlocks;

@Mixin(ShipAssemblyKt.class)
public final class ShipAssemblyMixin {

    // Overwrite the createNewShipWithBlocks function with our own function which won't break redstone stuff
    // and create duplication glitch on create stuff
    @Inject(method = "createNewShipWithBlocks", at = @At("HEAD"), cancellable = true)
    private static void onCreateNewShipWithBlocks(@NotNull BlockPos centerBlock, @NotNull DenseBlockPosSet blocks, @NotNull ServerLevel level, CallbackInfoReturnable<ServerShip> cir) {
        cir.setReturnValue((ServerShip) physicifyBlocks(level, blocks,1.0));
    }
}
