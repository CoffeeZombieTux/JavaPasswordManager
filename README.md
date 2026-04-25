# Password Manager

A desktop password manager built with JavaFX 21. Credentials are encrypted with AES-256-GCM and protected by a master password.

## Requirements

- Java 21+
- Gradle (wrapper included)

## Build & Run

```bash
# Build
./gradlew build

# Run
./gradlew run

# Run tests
./gradlew test
```

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
build/jpackage/passwordmanager_1.0.0_amd64.deb
```

**Install on any Ubuntu / Debian machine:**

```bash
sudo dpkg -i build/jpackage/passwordmanager_1.0.0_amd64.deb
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

## First Run

On first launch you will be prompted to create a master password (minimum 8 characters). This password encrypts the vault — it cannot be recovered if lost.

On subsequent launches the same password unlocks the vault.

## Vault Location

The encrypted vault file is stored at:

| Platform | Path |
|----------|------|
| Linux    | `~/.local/share/PasswordManager/credentials.vault` |
| macOS    | `~/Library/Application Support/PasswordManager/credentials.vault` |
| Windows  | `%APPDATA%\PasswordManager\credentials.vault` |

## Resetting / Deleting the Vault

Deleting the vault permanently removes all stored credentials. There is no recovery.

**Linux / macOS**

```bash
rm ~/.local/share/PasswordManager/credentials.vault          # Linux
rm ~/Library/Application\ Support/PasswordManager/credentials.vault  # macOS
```

**Windows (PowerShell)**

```powershell
Remove-Item "$env:APPDATA\PasswordManager\credentials.vault"
```

> If the file is owned by `root` (e.g. the app was accidentally run with `sudo`), fix ownership first:
> ```bash
> sudo chown $USER ~/.local/share/PasswordManager/credentials.vault
> ```
> then delete it normally without `sudo`.

After deletion the next launch will prompt you to create a new master password.

## Encryption

Vault encryption uses:

- **Key derivation:** PBKDF2WithHmacSHA256 — 600,000 iterations, 256-bit key
- **Cipher:** AES-256-GCM (authenticated encryption)
- **File format:** `[16-byte salt][12-byte IV][GCM ciphertext + 128-bit tag]`

A random salt is generated when the vault is first created, so each vault is unique even with the same master password.

## License

See [LICENSE](LICENSE).
