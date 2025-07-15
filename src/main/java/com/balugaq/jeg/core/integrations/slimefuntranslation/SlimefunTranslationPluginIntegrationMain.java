/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.core.integrations.slimefuntranslation;

import com.balugaq.jeg.core.integrations.Integration;
import com.balugaq.jeg.utils.ReflectionUtil;
import lombok.Getter;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author balugaq
 * @since 1.9
 */
public class SlimefunTranslationPluginIntegrationMain implements Integration {
    @Getter
    @Nullable
    private Boolean interceptSearch_SlimefunTranslation;

    @Override
    public @NotNull String getHookPlugin() {
        return "SlimefunTranslation";
    }

    @Override
    public void onEnable() {
        Object value = ReflectionUtil.getValue(SlimefunTranslation.getConfigService(), "interceptSearch");
        if (value instanceof Boolean bool) {
            interceptSearch_SlimefunTranslation = bool;
            ReflectionUtil.setValue(SlimefunTranslation.getConfigService(), "interceptSearch", false);
        }
    }

    @Override
    public void onDisable() {
        // Rollback SlimefunTranslation interceptSearch
        if (interceptSearch_SlimefunTranslation != null) {
            ReflectionUtil.setValue(SlimefunTranslation.getConfigService(), "interceptSearch", interceptSearch_SlimefunTranslation);
        }
    }
}
