package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

/**
 * @author Nikolay Radzivon
 * @Date 26.05.2024
 */
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private Item item;

    private User owner;

    @BeforeEach
    void setUp() {
        this.owner = createUser("testOwner", "testOwner@email.com");
        this.item = createItem("testItem", "testDescription", true,
                owner);
    }

    private User createUser(String name, String email) {
        var user = User.builder()
                .name(name)
                .email(email)
                .build();

        return userRepository.save(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        var i = Item.builder()
                .owner(owner)
                .description(description)
                .available(available)
                .name(name)
                .build();

        return itemRepository.save(i);
    }


    @Test
    void searchItemTest() {
        var item2 = createItem("honda", "CaRDescription", Boolean.TRUE, owner);

        var search = itemRepository.searchItem("item", PageRequest.of(0, 2));
        var content = search.getContent();

        Assertions.assertEquals(search.getSize(), 2);
        Assertions.assertEquals(search.getTotalElements(), 1);
        Assertions.assertEquals(content.size(), 1);
        Assertions.assertEquals(content.get(0), item);
        Assertions.assertEquals(content.get(0).getId(), item.getId());
        Assertions.assertEquals(content.get(0).getName(), item.getName());
        Assertions.assertEquals(content.get(0).getAvailable(), item.getAvailable());
        Assertions.assertEquals(content.get(0).getOwner(), item.getOwner());
        Assertions.assertEquals(content.get(0).getDescription(), item.getDescription());
        Assertions.assertNull(content.get(0).getComments());

        var searchItem = itemRepository.searchItem("description", PageRequest.of(0, 3));
        var content1 = searchItem.getContent();

        Assertions.assertEquals(searchItem.getTotalElements(), 2);
        Assertions.assertEquals(searchItem.getSize(), 3);
        Assertions.assertEquals(content1.get(0), item);
        Assertions.assertEquals(content1.get(0).getId(), item.getId());
        Assertions.assertEquals(content1.get(0).getName(), item.getName());
        Assertions.assertEquals(content1.get(0).getAvailable(), item.getAvailable());
        Assertions.assertEquals(content1.get(0).getOwner(), item.getOwner());
        Assertions.assertEquals(content1.get(0).getDescription(), item.getDescription());
        Assertions.assertNull(content1.get(0).getComments());
        Assertions.assertEquals(content1.get(1), item2);
        Assertions.assertEquals(content1.get(1).getId(), item2.getId());
        Assertions.assertEquals(content1.get(1).getName(), item2.getName());
        Assertions.assertEquals(content1.get(1).getAvailable(), item2.getAvailable());
        Assertions.assertEquals(content1.get(1).getOwner(), item2.getOwner());
        Assertions.assertEquals(content1.get(1).getDescription(), item2.getDescription());
        Assertions.assertNull(content1.get(1).getComments());

        var searchItem1 = itemRepository.searchItem("car", PageRequest.of(0, 1));
        var content2 = searchItem1.getContent();

        Assertions.assertEquals(searchItem1.getTotalElements(), 1);
        Assertions.assertEquals(searchItem1.getSize(), 1);
        Assertions.assertEquals(content2.get(0), item2);
        Assertions.assertEquals(content2.get(0).getId(), item2.getId());
        Assertions.assertEquals(content2.get(0).getName(), item2.getName());
        Assertions.assertEquals(content2.get(0).getAvailable(), item2.getAvailable());
        Assertions.assertEquals(content2.get(0).getOwner(), item2.getOwner());
        Assertions.assertEquals(content2.get(0).getDescription(), item2.getDescription());
        Assertions.assertNull(content2.get(0).getComments());
    }

    @Test
    void deleteAllByOwner_IdTest() {
        var item1 = createItem("Дрель", "Описание дрели", Boolean.TRUE, owner);
        var item2 = createItem("Болгарка", "Описание болгарки", Boolean.TRUE, owner);
        var item3 = createItem("honda", "CaRDescription", Boolean.TRUE, createUser("TestOwner2", "testEmail@owner.test"));

        var all = itemRepository.findAll();

        Assertions.assertEquals(all.size(), 4);

        itemRepository.deleteAllByOwner_Id(owner.getId());

        var all1 = itemRepository.findAll();

        Assertions.assertEquals(all1.size(), 1);
        Assertions.assertEquals(all1.get(0), item3);
        Assertions.assertNotEquals(all1.get(0), item);
        Assertions.assertNotEquals(all1.get(0), item1);
        Assertions.assertNotEquals(all1.get(0), item2);
    }

    @Test
    void findAllByOwnerId() {
        var item1 = createItem("Дрель", "Описание дрели", Boolean.TRUE, owner);
        var item2 = createItem("Болгарка", "Описание болгарки", Boolean.TRUE, owner);
        var item3 = createItem("honda", "CaRDescription", Boolean.TRUE, createUser("TestOwner2", "testEmail@owner.test"));

        var all = itemRepository.findAll();

        Assertions.assertEquals(all.size(), 4);

        Page<Item> allByOwnerId = itemRepository.findAllByOwnerId(owner.getId(), PageRequest.of(0, 4));
        List<Item> content = allByOwnerId.getContent();

        Assertions.assertEquals(allByOwnerId.getSize(), 4);
        Assertions.assertEquals(allByOwnerId.getTotalElements(), 3);
        Assertions.assertEquals(content.get(0), item);
        Assertions.assertEquals(content.get(1), item1);
        Assertions.assertEquals(content.get(2), item2);
    }
}