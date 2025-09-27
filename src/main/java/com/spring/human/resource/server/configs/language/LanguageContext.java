package com.spring.human.resource.server.configs.language;

import java.util.Locale;

public class LanguageContext {
    // ThreadLocal giúp lưu ngôn ngữ riêng cho từng thread (mỗi request có thể có locale khác nhau)
    private static final ThreadLocal<Locale> context = new ThreadLocal<>();

    // Gán Locale cho thread hiện tại
    public static void setLocale(Locale locale) {
        context.set(locale);
    }

    // Lấy Locale của thread hiện tại, mặc định ENGLISH nếu chưa set
    public static Locale getLocale() {
        return context.get() != null ? context.get() : Locale.ENGLISH;
    }

    // Xóa Locale sau khi xử lý xong để tránh rò rỉ bộ nhớ
    public static void clear() {
        context.remove();
    }
}
