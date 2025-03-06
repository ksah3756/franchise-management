package com.goorm.friendchise.global.auth.managerevent;

import com.goorm.friendchise.domain.manager.domain.Manager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ManagerUpdateEvent {
    private final Long manageId;
    private final Manager manager;

    public static ManagerUpdateEvent create(Long manageId, Manager manager) {
        return new ManagerUpdateEvent(manageId, manager);
    }
}
