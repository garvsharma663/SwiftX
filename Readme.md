# SwiftX

SwiftX is a simple CLI-based file transfer tool written in Java. It is designed to transfer files over a local network using raw TCP sockets, without relying on third‑party services, cloud storage, or external servers.

The goal of this project is to understand and implement how real file transfer systems work at a low level: sockets, streams, protocols, encryption, and integrity verification.

---

## What SwiftX does

* Transfers files over a local network (LAN / hotspot)
* Uses TCP sockets directly
* Encrypts file data during transfer using AES
* Verifies file integrity using SHA‑256 checksum
* Works as a command‑line tool (CLI)

There is no UI at the moment. The focus is correctness, protocol design, and reliability.

---

## How it works (high‑level flow)

SwiftX follows a strict sender → receiver protocol. Both sides must follow the same order while reading and writing data.

### Receiver side

1. Receiver starts first and listens on a port
2. Waits for incoming socket connections
3. For each connection, spawns a new thread
4. Reads metadata, IV, encrypted file bytes, and checksum
5. Decrypts and writes the file to disk
6. Verifies file integrity using SHA‑256

### Sender side

1. Connects to receiver IP and port
2. Sends file metadata (name + size)
3. Generates IV and initializes AES encryption
4. Streams encrypted file bytes
5. Sends checksum at the end

---

## Usage

Start receiver (must be started first):

```
swiftx.bat receive <port>
```

Send a file:

```
swiftx.bat send <ip> <port> <file>
```

Example:

```
swiftx.bat receive 5000
swiftx.bat send 127.0.0.1 5000 test.txt
```

---

## Protocol design

The protocol is binary and order‑based. TCP only transfers bytes, so both sender and receiver must strictly follow the same structure.

### Data order

1. File metadata

    * File name length (int)
    * File name (UTF‑8 bytes)
    * File size (long)

2. Encryption IV

    * IV length (int)
    * IV bytes

3. Encrypted file bytes

    * Streamed using Cipher streams

4. Checksum

    * Hash length (int)
    * SHA‑256 hash bytes

If the order is broken, transfer fails.

---

## File responsibilities

### `SwiftXApp`

* Entry point of the application
* Parses CLI arguments
* Routes commands to sender or receiver
* Prints help and version info

### `FileSender`

* Connects to receiver via socket
* Sends metadata
* Encrypts file using AES (CBC mode)
* Streams file bytes
* Generates and sends SHA‑256 checksum

### `FileReceiverServer`

* Opens a `ServerSocket`
* Listens on a given port
* Accepts incoming connections
* Spawns a new thread per connection

### `FileReceiveTask`

* Handles a single file receive operation
* Reads metadata, IV, encrypted bytes, checksum
* Decrypts and writes file to disk
* Verifies integrity using SHA‑256

### `FileMetaData`

* Defines the binary metadata protocol
* Responsible for writing and reading file name and size

### `CryptoUtils`

* Central place for cryptographic operations
* AES configuration (algorithm, mode, padding)
* IV generation
* Cipher initialization for encryption/decryption

---

## Security notes

* AES encryption is used (CBC mode with PKCS5 padding)
* IV is generated per transfer
* SHA‑256 checksum ensures file integrity
* AES key is currently hardcoded (will be improved)

Detailed design decisions are documented separately in the security documentation.

---

## Limitations (current)

* CLI only (no GUI)
* Hardcoded encryption key
* No resume support for interrupted transfers
* No authentication or key exchange
* Java must be installed on the system
* Windows BAT launcher required for convenience

---

## Future improvements

* Portable single‑file EXE
* Bundled JRE (no Java dependency for users)
* GUI version of SwiftX
* Secure key exchange
* Resume support for large files
* Transfer progress UI

---

## Release

Download the latest release from GitHub:

**GitHub Release:** [Release v1.0.0](https://github.com/garvsharma663/SwiftX/releases)

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.
