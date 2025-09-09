package com.spunit.common.spi;

import java.util.ServiceLoader;

/**
 * Core Java – Advanced Concepts: ServiceLoader & SPI
 * - Discovers FormatService implementations registered in META-INF/services.
 */
public final class ServiceLoaderUtil {
    private ServiceLoaderUtil() {}

    public static String tryFormat(long cents, String currencyCode) {
        ServiceLoader<FormatService> loader = ServiceLoader.load(FormatService.class);
        for (FormatService svc : loader) {
            try {
                return svc.formatAmount(cents, currencyCode);
            } catch (Throwable ignored) { }
        }
        return Long.toString(cents);
    }
}
