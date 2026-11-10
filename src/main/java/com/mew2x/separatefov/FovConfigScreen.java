package com.example.separatefov;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.DoubleConsumer;

/**
 * Simple vanilla config screen (no Cloth Config needed): one slider per perspective.
 * Values are written to config/separatefov.json when the screen is closed.
 */
public class FovConfigScreen extends Screen {
	private final Screen parent;

	// Pending values; they survive init() being called again (window resize, reset).
	private double firstPersonOffset = FovConfig.getFirstPersonOffset();
	private double thirdPersonOffset = FovConfig.getThirdPersonOffset();

	public FovConfigScreen(Screen parent) {
		super(Text.literal("Separate FOV"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int width = 200;
		int x = this.width / 2 - width / 2;
		int y = this.height / 4;

		int titleWidth = this.textRenderer.getWidth(this.title);
		TextWidget titleWidget = new TextWidget(titleWidth, 9, this.title, this.textRenderer);
		titleWidget.setPosition(this.width / 2 - titleWidth / 2, 20);
		this.addDrawableChild(titleWidget);

		OffsetSlider first = new OffsetSlider(x, y, width, 20, "First person offset",
				this.firstPersonOffset, value -> this.firstPersonOffset = value);
		first.setTooltip(Tooltip.of(Text.literal("Added to the FOV slider in Video Settings while in first person.")));
		this.addDrawableChild(first);

		OffsetSlider third = new OffsetSlider(x, y + 24, width, 20, "Third person offset",
				this.thirdPersonOffset, value -> this.thirdPersonOffset = value);
		third.setTooltip(Tooltip.of(Text.literal("Added to the FOV slider in Video Settings while in third person.")));
		this.addDrawableChild(third);

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset to defaults"), button -> {
			this.firstPersonOffset = FovConfig.DEFAULT_FIRST_PERSON_OFFSET;
			this.thirdPersonOffset = FovConfig.DEFAULT_THIRD_PERSON_OFFSET;
			this.clearAndInit();
		}).dimensions(x, y + 60, width, 20).build());

		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
				.dimensions(x, y + 84, width, 20).build());
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}

	@Override
	public void removed() {
		FovConfig.setOffsets(this.firstPersonOffset, this.thirdPersonOffset);
	}

	/** Slider from -60 to +60 degrees in steps of 1. */
	private static final class OffsetSlider extends SliderWidget {
		private static final double MIN = -60.0;
		private static final double MAX = 60.0;

		private final String label;
		private final DoubleConsumer onChange;

		OffsetSlider(int x, int y, int width, int height, String label, double offset, DoubleConsumer onChange) {
			super(x, y, width, height, Text.empty(), MathHelper.clamp((offset - MIN) / (MAX - MIN), 0.0, 1.0));
			this.label = label;
			this.onChange = onChange;
			// The super constructor calls updateMessage() before our fields exist, so refresh it here.
			this.updateMessage();
		}

		private double offset() {
			return Math.round(MIN + this.value * (MAX - MIN));
		}

		@Override
		protected void updateMessage() {
			if (this.label == null) {
				return;
			}
			this.setMessage(Text.literal(String.format("%s: %+d", this.label, (int) this.offset())));
		}

		@Override
		protected void applyValue() {
			this.onChange.accept(this.offset());
		}
	}
}
