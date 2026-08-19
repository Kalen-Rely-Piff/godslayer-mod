package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    
    public static final DeferredItem<GodSlayerSwordItem> GOD_SLAYER_SWORD =
        ITEMS.register("god_slayer_sword",
            () -> new GodSlayerSwordItem(Tiers.IRON, 3, -2.4F,
                new Item.Properties().stacksTo(1).durability(2031)));

    public ExampleMod(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof GodSlayerSwordItem) {
            BlockHitResult hitResult = event.getHitVec();
            if (hitResult != null) {
                BlockPos pos = hitResult.getBlockPos().relative(hitResult.getDirection());
                if (!level.isClientSide) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    if (lightning != null) {
                        lightning.setPos(pos.getX(), pos.getY(), pos.getZ());
                        serverLevel.addFreshEntity(lightning);
                    }
                }
                event.setCanceled(true);
            }
        }
    }

    public static class GodSlayerSwordItem extends SwordItem {
        public GodSlayerSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
        @Override
        public boolean isFoil(ItemStack stack) { return true; }
    }
}
