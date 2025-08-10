package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.CommandExecutionCallback;
import me.bymartrixx.playerevents.api.event.PlayerLeaveCallback;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    // Just after the command is executed
    // 1.20
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;validateMessage(Ljava/lang/String;Ljava/lang/Runnable;)V"),
            method = "onCommandExecution", require = 0)
    private void onCommandExecuted(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        CommandExecutionCallback.EVENT.invoker().onExecuted(packet.command(), this.player.getCommandSource());
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerCommonNetworkHandler;onDisconnected(Lnet/minecraft/network/DisconnectionInfo;)V"), method = "onDisconnected")
    private void onPlayerLeave(DisconnectionInfo info, CallbackInfo ci) {
        PlayerLeaveCallback.EVENT.invoker().leaveServer(this.player, this.player.getServer());
    }
}
