package com.passwordmanager.ui.component.topbar;

import java.util.Objects;

public class TopBarActionsController {

    private Runnable addButtonCallback = () -> {};

    public void setAddButtonCallback(Runnable addButtonCallback) {
        this.addButtonCallback = Objects.requireNonNull(addButtonCallback);
    }

    public void onAddCredentialButtonClick() {
        addButtonCallback.run();
    }
}
