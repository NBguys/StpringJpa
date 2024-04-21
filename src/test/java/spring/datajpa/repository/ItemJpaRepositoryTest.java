package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import spring.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

class ItemJpaRepositoryTest {


    @Autowired
    ItemJpaRepository itemJpaRepository;

    @Test
    void save() {
        Item item = new Item("testId");

        itemJpaRepository.save(item);

    }
}