package io.github.priestoffern.vs_ship_assembler

import dev.architectury.registry.CreativeTabRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import io.github.priestoffern.vs_ship_assembler.items.ShipAssemblerItem
import net.minecraft.world.item.CreativeModeTab.TAB_MISC

object VsShipAssemblerItems {
    val ITEMS = DeferredRegister.create(VsShipAssemblerMod.MOD_ID, Registry.ITEM_REGISTRY)

    var SHIP_ASSEMBLER: RegistrySupplier<Item> = ITEMS.register("ship_assembler") { ShipAssemblerItem(Item.Properties().tab(
        TAB_MISC
    ).stacksTo(1)) }
    fun register() {
        ITEMS.register()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}