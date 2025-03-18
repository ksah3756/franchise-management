package com.goorm.friendchise.domain.customer.infrastructure;

import com.goorm.friendchise.domain.headquarter.domain.RestaurantCategory;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.RestaurantSubCategory;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class FakeStoreRepository implements StoreRepository {
    private long idGenerator = 1L; // ID 자동 증가 변수
    Headquarter fakeHeadquarter = Headquarter.builder()
            .id(1L)
            .franchiseName("맥도날드")
            .restaurantCategory(RestaurantCategory.FASTFOOD)
            .restaurantSubCategory(RestaurantSubCategory.NONE)
            .certificationNumber(UUID.randomUUID().toString())
            .build();
    private final List<Store> stores = new ArrayList<>();
    Manager fakeManager = Manager.builder().username("admin").password("admin").role(Role.STORE).build();
    public FakeStoreRepository() {

        for( int i = 0; i < 10; i++ )
        {
            StoreReqDto storeRegisterDto = StoreReqDto
                    .builder()
                    .dong("동").address("Test:" + i).x(37.5665002 + (i * 0.0000001)).y(126.9780002 + (i * 0.0000001)).franchiseName("맥도날드").build();
            Store store=new Store(storeRegisterDto,fakeHeadquarter,fakeManager);
            setIdUsingReflection(store, idGenerator++);
            stores.add(store);
        }
    }

    @Override
    public Store  save(Store store) {
        setIdUsingReflection(store, idGenerator++);
        stores.add(store);
        return store;
    }

    private void setIdUsingReflection(Store store, Long id) {
        try {
            Field idField = Store.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(store, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("ID 설정 실패", e);
        }
    }
    @Override
    public <S extends Store> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Store> findById(Long aLong) {
        return stores.stream()
                .filter(store -> store.getId().equals(aLong))
                .findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Store> findAll() {
        return new ArrayList<>(stores);
    }

    @Override
    public List<Store> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Store entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Store> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Store> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Store> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Store> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Store getOne(Long aLong) {
        return null;
    }

    @Override
    public Store getById(Long aLong) {
        return null;
    }

    @Override
    public Store getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Store> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Store> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Store> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Store> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Store> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Store> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Store, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Store> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Store> findAll(Pageable pageable) {
        return null;
    }
}