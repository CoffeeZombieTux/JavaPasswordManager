package com.passwordmanager.repository;

public class VaultException extends RuntimeException {
    public VaultException(String message, Throwable cause) {
        super(message, cause);
    }
}
