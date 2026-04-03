package com.passwordmanager.service;

import com.passwordmanager.repository.CredentialRepository;
import com.passwordmanager.model.Credential;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CredentialService {

    private final CredentialRepository repository;


    public CredentialService(CredentialRepository repository) {
        this.repository = repository;
    }

    public int totalCredentials() {
        return repository.findAll().size();
    }

    public List<Credential> findAll() {
        return repository.findAll();
    }

    public Credential add(@NotNull Credential credential) {
        return repository.add(credential);
    }

    public boolean delete(@NotNull Credential credential) {
        return repository.deleteById(credential.getId());
    }

}
