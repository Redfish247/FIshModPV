package dev.fishmodpv.profile;

import com.google.gson.JsonObject;

/** A single pet entry from {@code pets_data.pets} (modern) / {@code pets} (legacy). */
public record PetData(String type, double exp, String tier, boolean active, String heldItem) {
	public static PetData parse(JsonObject pet) {
		String type = pet.has("type") ? pet.get("type").getAsString() : "UNKNOWN";
		double exp = pet.has("exp") ? pet.get("exp").getAsDouble() : 0;
		String tier = pet.has("tier") ? pet.get("tier").getAsString() : "COMMON";
		boolean active = pet.has("active") && pet.get("active").getAsBoolean();
		String heldItem = pet.has("heldItem") && !pet.get("heldItem").isJsonNull() ? pet.get("heldItem").getAsString() : null;
		return new PetData(type, exp, tier, active, heldItem);
	}

	public String displayName() {
		StringBuilder name = new StringBuilder();
		for (String word : type.replace('_', ' ').toLowerCase().split(" ")) {
			if (!word.isEmpty()) {
				name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
			}
		}
		return name.toString().trim();
	}
}
