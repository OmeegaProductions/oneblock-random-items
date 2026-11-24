/*
 * Copyright 2026 Axel "Azn9" Joly <contact@azn9.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package dev.azn9.op.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.azn9.op.data.WorldContainer;

import java.lang.reflect.Type;

public class WorldContainerTypeAdapter implements JsonSerializer<WorldContainer>, JsonDeserializer<WorldContainer> {

    @Override
    public WorldContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException(json.toString());
        }

        JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
        if (!jsonPrimitive.isString()) {
            throw new JsonParseException(json.toString());
        }

        String worldName = jsonPrimitive.getAsString();
        return WorldUtil.generateWorld(worldName);
    }

    @Override
    public JsonElement serialize(WorldContainer src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.overworld().getName());
    }
}
