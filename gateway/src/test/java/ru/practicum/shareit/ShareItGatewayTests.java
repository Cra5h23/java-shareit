package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
class ShareItGatewayTests {
    @Test
    void mainTest() {
        Assertions.assertDoesNotThrow(ShareItGateway::new);
        Assertions.assertDoesNotThrow(() -> ShareItGateway.main(new String[]{}));
    }
}