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

package com.balugaq.jeg.utils.formatter;

/**
 * @author balugaq
 * @since 1.6
 */
public class Formats {
    public static final MainFormat main = new MainFormat();
    public static final NestedGroupFormat nested = new NestedGroupFormat();
    public static final SubGroupFormat sub = new SubGroupFormat();
    public static final RecipeFormat recipe = new RecipeFormat();
    public static final HelperFormat helper = new HelperFormat();
    public static final RecipeVanillaFormat recipe_vanilla = new RecipeVanillaFormat();
    public static final RecipeDisplayFormat recipe_display = new RecipeDisplayFormat();
    public static final SettingsFormat settings = new SettingsFormat();
    public static final ContributorsFormat contributors = new ContributorsFormat();

    static {
        main.loadMapping();
        nested.loadMapping();
        sub.loadMapping();
        recipe.loadMapping();
        helper.loadMapping();
        recipe_vanilla.loadMapping();
        recipe_display.loadMapping();
        settings.loadMapping();
        contributors.loadMapping();
    }
}
