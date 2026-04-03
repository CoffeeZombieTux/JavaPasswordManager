package com.passwordmanager.repository;

import com.passwordmanager.model.Credential;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository {
    List<Credential> findAll();
    Optional<Credential> findById(UUID id);
    Credential add(Credential credential);
    boolean update(Credential credential);
    boolean deleteById(UUID id);
}
