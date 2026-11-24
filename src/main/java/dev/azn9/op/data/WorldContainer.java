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

package dev.azn9.op.data;

import org.bukkit.World;

import java.util.function.Consumer;

public record WorldContainer(
        World overworld,
        World nether,
        World end
) {
    public void apply(Consumer<World> action) {
        action.accept(this.overworld);
        action.accept(this.nether);
        action.accept(this.end);
    }
}
