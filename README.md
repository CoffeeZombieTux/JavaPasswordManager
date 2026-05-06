# Password Manager

A desktop password manager built with JavaFX 21. Credentials are encrypted with AES-256-GCM and protected by a master password. Everything is stored locally — no servers, no cloud sync built in.

## Requirements

- Java 21+
- Gradle (wrapper included)

## Build & Run

```bash
# Build
./gradlew build

# Run
./gradlew run
```

> Note: `./gradlew build` may fail due to a Gradle–JUnit compatibility issue in the test task. Use `assemble` for a full build without tests.

## Building a .deb Installer (Ubuntu / Debian)

The build produces a `.deb` package that bundles its own JRE — users do not need Java installed.

**Prerequisites (build machine only):**

The packaging toolchain (`jlink` + `jpackage`) requires Java 21. If you use SDKMAN (recommended), install and activate it once:

```bash
sdk install java 21.0.11-tem
sdk env   # activates Java 21 for this project directory
```

Also install the native packaging tools:

```bash
sudo apt install fakeroot binutils
```

**Build:**

```bash
./gradlew jpackage
```

The installer is written to `build/jpackage/`:

```
build/jpackage/passwordmanager_2.0.0_amd64.deb
```

**Install on any Ubuntu / Debian machine:**

```bash
sudo dpkg -i build/jpackage/passwordmanager_2.0.0_amd64.deb
```

The app will appear in the application menu under **Utilities** and can also be launched from a terminal:

```bash
PasswordManager
```

**Uninstall:**

```bash
sudo dpkg -r passwordmanager
```

---

## First Launch

On first launch the **Welcome screen** explains how the app works: it is fully local, how to export the encrypted vault for cloud backup, and how to restore from a backup. From here you can:

- **Create New Vault** — set a master password (minimum 8 characters). This password encrypts the vault and cannot be recovered if lost.
- **Import Existing Vault** — pick a `.vault` file exported from another device. You will need the master password that was used when that vault was created.

On subsequent launches the master password dialog opens directly to unlock the existing vault.

## Backup & Export

Because the app has no built-in cloud sync, use **Export** (topbar button) to copy the encrypted vault file to any directory — for example, a folder synced by Google Drive, Dropbox, or another cloud service. The file stays encrypted, so it is safe to store in the cloud.

The last used export directory is remembered and pre-filled on the next export.

## Import

Import is only available on first launch when no vault exists. Select a `.vault` file previously exported from this app and enter the master password that was set when it was created.

## Deleting the Vault

The **Delete Vault** button in the topbar opens a confirmation dialog. You must enter your master password to confirm. The action is permanent and cannot be undone — export a backup first if you want to keep your data.

After deletion the app closes. The next launch will show the Welcome screen.

## Storage Files

| Platform | Vault | Preferences |
|----------|-------|-------------|
| Linux    | `~/.local/share/PasswordManager/credentials.vault` | `~/.local/share/PasswordManager/preferences.json` |
| macOS    | `~/Library/Application Support/PasswordManager/credentials.vault` | `~/Library/Application Support/PasswordManager/preferences.json` |
| Windows  | `%APPDATA%\PasswordManager\credentials.vault` | `%APPDATA%\PasswordManager\preferences.json` |

`preferences.json` stores non-sensitive settings (e.g. last export directory) in plain JSON.

> If a vault file is owned by `root` (e.g. the app was accidentally run with `sudo`), fix ownership before deleting:
> ```bash
> sudo chown $USER ~/.local/share/PasswordManager/credentials.vault
> ```

## Encryption

Vault encryption uses:

- **Key derivation:** PBKDF2WithHmacSHA256 — 600,000 iterations, 256-bit key
- **Cipher:** AES-256-GCM (authenticated encryption)
- **File format:** `[16-byte salt][12-byte IV][GCM ciphertext + 128-bit tag]`

A random salt is generated when the vault is first created, so each vault is unique even with the same master password.

---

## Architecture

JavaFX desktop app (Java 21, JPMS modules) using an MVC pattern where a single `MainController` orchestrates the whole UI.

**Layer flow:**
```
UI (FXML + Controller) → CredentialService → CredentialRepository → credentials.vault
```

**Key design points:**

- `Credential` is an immutable record with a `@JsonCreator` constructor — all fields are `final`. Adding a field requires updating both the constructor signature and `CredentialService.save(...)`.
- `FileCredentialRepository` is the only `CredentialRepository` implementation. It holds an in-memory `List<Credential>` and writes the entire list to disk atomically (temp file + move) on every mutation.
- Storage paths are resolved by `StoragePathResolver` — exposes `credentialsFilePath()` and `preferencesFilePath()`, both under the same OS-specific app directory.
- `AppPreferences` / `PreferencesStore` — plain Jackson POJO + static load/save helpers for non-sensitive user preferences. `com.passwordmanager.storage` is opened to `com.fasterxml.jackson.databind` in `module-info.java`.
- `MainController` is instantiated by JavaFX/FXML and manually wires all sub-controllers in its `@FXML initialize()`. No DI framework — `FileCredentialRepository` is newed directly in `MainController`.
- Each UI panel is a separate FXML + controller pair under `ui/component/`. The right-hand panel switches between `CredentialDetailController` (view) and `AddCredentialFormController` (create/edit) via `.show()` / `.hide()`.
- Jackson (with `JavaTimeModule`) handles JSON serialisation of `Credential`. Any new model class that needs serialisation must be opened to `com.fasterxml.jackson.databind` in `module-info.java`.
- `requires java.desktop` in `module-info.java` is needed for `java.awt.Toolkit.getLockingKeyState` (caps lock detection in the Delete Vault dialog).
- `scenicview.jar` is a local lib in `libs/` used for UI debugging (`ScenicView.show(scene)` — commented out in code).

**Startup flow:**
1. If no vault file exists → `WelcomeController` is shown.
   - "Create New Vault" → master password dialog (create mode).
   - "Import Existing Vault" → FileChooser → file copied to app storage → master password dialog (unlock mode).
   - Close → `Platform.exit()`.
2. Vault exists → master password dialog directly.

## License

See [LICENSE](LICENSE).
