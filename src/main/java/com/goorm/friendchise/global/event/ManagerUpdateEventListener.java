package com.goorm.friendchise.global.event;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ManagerUpdateEventListener {
    private final ManagerRepository managerRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleManagerUpdateEvent(ManagerUpdateEvent event) {
        Manager manager = event.getManager();
        manager.updateManageId(event.getManageId());
        managerRepository.save(manager);
        log.info("manageId {}가 연결되었습니다. Role: {}", event.getManageId(), manager.getRole().getDescription());
    }
}
