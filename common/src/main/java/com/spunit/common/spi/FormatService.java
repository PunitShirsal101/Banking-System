package com.spunit.common.spi;

/**
 * Core Java – Advanced Concepts: ServiceLoader & SPI
 * - Interface to be discovered via META-INF/services using ServiceLoader.
 */
public interface FormatService {
    String formatAmount(long cents, String currencyCode);
}
