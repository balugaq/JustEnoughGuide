/**
 * 多语言支持：{@code resources/lang/<语言>.yml} + {@link com.balugaq.jeg.core.lang.Lang} 静态门面。
 * <p>
 * 语言由 {@code config.yml} 的 {@code language} 键选择（运行时全局只有一种语言）；
 * 解析核心在 {@link com.balugaq.jeg.core.lang.LangRegistry}（可无服务器单测）。
 */
package com.balugaq.jeg.core.lang;
