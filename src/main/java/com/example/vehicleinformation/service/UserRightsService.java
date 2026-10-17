package com.example.vehicleinformation.service;

import com.example.vehicleinformation.model.UserRights;
import com.example.vehicleinformation.repository.UserRightsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRightsService {

    private static final Logger log = LoggerFactory.getLogger(UserRightsService.class);

    private final UserRightsRepository repository;

    public UserRightsService(UserRightsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public UserRights getRights() {
        return repository.findById(UserRights.SINGLETON_ID)
                .orElseGet(() -> new UserRights(false, false));
    }

    @Transactional
    public UserRights ensureDefaults() {
        return repository.findById(UserRights.SINGLETON_ID).orElseGet(() -> {
            UserRights defaults = new UserRights(false, false);
            UserRights saved = repository.save(defaults);
            log.info("Initialized user rights defaults: allowEdit=false, allowDelete=false");
            return saved;
        });
    }

    @Transactional
    public UserRights update(boolean allowEdit, boolean allowDelete) {
        UserRights rights = repository.findById(UserRights.SINGLETON_ID)
                .orElseGet(() -> new UserRights(false, false));
        rights.setId(UserRights.SINGLETON_ID);
        rights.setAllowEdit(allowEdit);
        rights.setAllowDelete(allowDelete);
        UserRights saved = repository.save(rights);
        log.info("User rights updated: allowEdit={}, allowDelete={}", allowEdit, allowDelete);
        return saved;
    }

    @Transactional(readOnly = true)
    public boolean isPublicEditAllowed() {
        return getRights().isAllowEdit();
    }

    @Transactional(readOnly = true)
    public boolean isPublicDeleteAllowed() {
        return getRights().isAllowDelete();
    }
}
