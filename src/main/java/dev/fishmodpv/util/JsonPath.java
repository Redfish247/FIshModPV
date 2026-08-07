package dev.fishmodpv.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Defensive JSON navigation helpers.
 *
 * <p>Hypixel's SkyBlock API has reshuffled member fields between schema revisions several times
 * over the years (e.g. top-level {@code coin_purse} vs. nested {@code currencies.coin_purse}).
 * Rather than binding to one exact shape and silently returning nulls when Hypixel changes
 * something, callers pass a list of dotted candidate paths and the first one that resolves wins.
 */
public final class JsonPath {
	private JsonPath() {
	}

	/** Tries each dotted path (e.g. "currencies.coin_purse") in order, returns the first hit. */
	public static JsonElement firstOf(JsonObject root, String... dottedPaths) {
		for (String path : dottedPaths) {
			JsonElement found = get(root, path);
			if (found != null && !found.isJsonNull()) {
				return found;
			}
		}
		return null;
	}

	public static JsonElement get(JsonObject root, String dottedPath) {
		if (root == null) {
			return null;
		}
		JsonElement current = root;
		for (String part : dottedPath.split("\\.")) {
			if (current == null || !current.isJsonObject()) {
				return null;
			}
			current = current.getAsJsonObject().get(part);
		}
		return current;
	}

	public static double firstAsDouble(JsonObject root, double fallback, String... paths) {
		JsonElement e = firstOf(root, paths);
		return e != null && e.isJsonPrimitive() ? e.getAsDouble() : fallback;
	}

	public static long firstAsLong(JsonObject root, long fallback, String... paths) {
		JsonElement e = firstOf(root, paths);
		return e != null && e.isJsonPrimitive() ? e.getAsLong() : fallback;
	}

	public static String firstAsString(JsonObject root, String fallback, String... paths) {
		JsonElement e = firstOf(root, paths);
		return e != null && e.isJsonPrimitive() ? e.getAsString() : fallback;
	}

	public static JsonObject firstAsObject(JsonObject root, String... paths) {
		JsonElement e = firstOf(root, paths);
		return e != null && e.isJsonObject() ? e.getAsJsonObject() : null;
	}

	public static JsonArray firstAsArray(JsonObject root, String... paths) {
		JsonElement e = firstOf(root, paths);
		return e != null && e.isJsonArray() ? e.getAsJsonArray() : null;
	}
}
