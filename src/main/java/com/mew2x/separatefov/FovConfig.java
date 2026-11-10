package com.example.separatefov;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Offsets (in degrees) added to the vanilla FOV slider value, per perspective.
 * Stored in config/separatefov.json.
 */
public final class FovConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("separatefov.json");

	private static final double MIN_FOV = 1.0;
	private static final double MAX_FOV = 170.0;

	public static final double DEFAULT_FIRST_PERSON_OFFSET = 0.0;
	public static final double DEFAULT_THIRD_PERSON_OFFSET = -20.0;

	private static final class Data {
		double firstPersonOffset = DEFAULT_FIRST_PERSON_OFFSET;
		double thirdPersonOffset = DEFAULT_THIRD_PERSON_OFFSET;
	}

	private static Data data = new Data();

	private FovConfig() {
	}

	public static double getFirstPersonOffset() {
		return data.firstPersonOffset;
	}

	public static double getThirdPersonOffset() {
		return data.thirdPersonOffset;
	}

	public static void setFirstPersonOffset(double value) {
		data.firstPersonOffset = value;
		save();
	}

	public static void setThirdPersonOffset(double value) {
		data.thirdPersonOffset = value;
		save();
	}

	/** Sets both offsets and writes the file once (used by the config screen). */
	public static void setOffsets(double firstPerson, double thirdPerson) {
		data.firstPersonOffset = firstPerson;
		data.thirdPersonOffset = thirdPerson;
		save();
	}

	/**
	 * @param vanillaFov final FOV vanilla computed (includes sprint/spyglass/etc. effects)
	 * @param sliderFov  the value of the FOV slider in the video settings
	 */
	public static float adjust(float vanillaFov, int sliderFov, boolean thirdPerson) {
		double offset = thirdPerson ? data.thirdPersonOffset : data.firstPersonOffset;
		if (offset == 0.0 || sliderFov <= 0) {
			return vanillaFov;
		}
		double target = Math.max(MIN_FOV, Math.min(MAX_FOV, sliderFov + offset));
		return (float) (vanillaFov * (target / sliderFov));
	}

	public static void load() {
		if (!Files.exists(PATH)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(PATH)) {
			Data loaded = GSON.fromJson(reader, Data.class);
			if (loaded != null) {
				data = loaded;
			}
		} catch (Exception e) {
			SeparateFovClient.LOGGER.warn("Could not read {}, using defaults", PATH, e);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			SeparateFovClient.LOGGER.warn("Could not write {}", PATH, e);
		}
	}
}
