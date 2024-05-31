package ru.practicum.shareit.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Optional;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class ItemCheckerTest {
    private ItemChecker itemChecker;
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        itemChecker = new ItemChecker(this.itemRepository);
    }

    @Test
    void checkItem() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemChecker.checkItem(1L, "Message"));
        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Message", throwable.getMessage());

        Mockito.verify(itemRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }
}