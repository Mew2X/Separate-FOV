package com.example.separatefov;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeparateFovClient implements ClientModInitializer {
	public static final String MOD_ID = "separatefov";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		FovConfig.load();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommandManager.literal("separatefov")
						.executes(ctx -> show(ctx))
						.then(ClientCommandManager.literal("show")
								.executes(ctx -> show(ctx)))
						.then(ClientCommandManager.literal("reload")
								.executes(ctx -> {
									FovConfig.load();
									return show(ctx);
								}))
						.then(ClientCommandManager.literal("first")
								.then(ClientCommandManager.argument("offset", DoubleArgumentType.doubleArg(-100.0, 100.0))
										.executes(ctx -> {
											FovConfig.setFirstPersonOffset(DoubleArgumentType.getDouble(ctx, "offset"));
											return show(ctx);
										})))
						.then(ClientCommandManager.literal("third")
								.then(ClientCommandManager.argument("offset", DoubleArgumentType.doubleArg(-100.0, 100.0))
										.executes(ctx -> {
											FovConfig.setThirdPersonOffset(DoubleArgumentType.getDouble(ctx, "offset"));
											return show(ctx);
										})))));
	}

	private static int show(CommandContext<FabricClientCommandSource> ctx) {
		ctx.getSource().sendFeedback(Text.literal(String.format(
				"Separate FOV offsets (added to the FOV slider): first person %+.1f, third person %+.1f",
				FovConfig.getFirstPersonOffset(), FovConfig.getThirdPersonOffset())));
		return 1;
	}
}
