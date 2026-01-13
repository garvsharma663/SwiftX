# 🔐 Security Architecture & Cryptography Concepts

This document explains **why** cryptography is used in this project and **how each concept fits together**.
It is written for developers who may understand Java and networking, but are new to **security engineering depth**.

---

## 🎯 Threat Model (What are we protecting against?)

This project is designed for **local network / hotspot file transfer**.

Assumptions:

* Network cannot be fully trusted
* Other devices may be connected to the same Wi‑Fi
* Attackers may sniff or manipulate packets

Goals:

* **Confidentiality** → attacker cannot read file contents
* **Integrity** → attacker cannot silently modify data
* **Correctness** → large files transfer safely (GB-scale)

Non-goals:

* Nation‑state attackers
* Compromised operating systems
* Physical device access

---

## 🔑 What is AES (Advanced Encryption Standard)?

AES is a **symmetric encryption algorithm**:

* Same key is used for encryption and decryption
* Industry standard (TLS, SSH, disk encryption)

Why AES is used:

* Extremely well-studied
* Hardware accelerated
* Fast enough for large files

Trade-offs:

* Requires secure key handling
* Works only on fixed-size blocks

---

## 📦 Why AES is a BLOCK Cipher

AES encrypts data in **fixed-size blocks**:

* Block size = **16 bytes (128 bits)**
* Cannot encrypt arbitrary-length data directly

Why block design exists:

* Enables strong diffusion (small input change → large output change)
* Prevents byte-level pattern leakage

Trade-off:

* Requires modes of operation
* Requires padding

---

## 🔗 Why CBC Mode (Cipher Block Chaining)

CBC is a **mode of operation** that allows AES to encrypt large data streams.

How it works conceptually:

* Each plaintext block is XORed with the previous ciphertext block
* First block uses a random **IV**

Security benefits:

* Identical plaintext blocks encrypt differently
* Prevents pattern analysis

Trade-offs:

* Needs IV management
* No built-in integrity protection

---

## 🎲 What is an IV (Initialization Vector)

An IV is a **random, non-secret value** used to start encryption.

Why IV exists:

* Prevents deterministic encryption
* Same file + same key ≠ same ciphertext

Rules:

* Must be random
* Must never repeat with same key
* Can be safely sent in plaintext

Trade-off:

* Adds protocol complexity

---

## 🧩 Why Padding Exists (PKCS5Padding)

AES requires data to be a multiple of 16 bytes.

Padding:

* Adds extra bytes to the final block
* Allows encryption of arbitrary-length files

PKCS5Padding:

* Standardized
* Reversible
* Automatically handled by Java

Trade-off:

* Incorrect handling can lead to padding attacks

---

## 🧠 What is a Cipher in Java

`javax.crypto.Cipher` represents an **encryption engine**.

It is intentionally explicit:

* Algorithm
* Mode
* Padding
* Key
* IV

This prevents accidental insecure defaults.

Trade-off:

* Verbose API
* Safer by design

---

## 🔐 What is a Key (and why it matters)

The key is the **only secret** in symmetric encryption.

Security depends on:

* Key randomness
* Key length
* Key secrecy

AES security is meaningless if:

* Key is weak
* Key is reused incorrectly

---

## 🤝 Why Key Sharing Is a Separate Problem

AES assumes both sides already share a key.

In real systems:

* Keys must be exchanged securely
* Network is untrusted

Solutions:

* Password-based key derivation
* Diffie–Hellman key exchange

This project currently focuses on **data encryption**, not full authentication.

---

## 🧪 Why Encryption Alone Is NOT Enough

CBC encryption provides confidentiality only.

Without integrity checks:

* Attackers can flip bits
* Corrupted data may go unnoticed

This is why:

* Checksums or MACs are added
* Modern systems prefer AEAD modes (e.g., AES-GCM)

---

## 🧠 Design Philosophy

This project intentionally:

* Separates concerns (networking vs crypto)
* Uses explicit cryptographic primitives
* Avoids framework abstractions

Goal:

> **Understand security, not just use it**

---

## 📌 Summary Table

| Concept      | Purpose              |
| ------------ | -------------------- |
| AES          | Encrypt data         |
| Block cipher | Strong diffusion     |
| CBC          | Secure streaming     |
| IV           | Randomize encryption |
| Padding      | Block alignment      |
| Cipher       | Encryption engine    |
| Key          | Secret control       |
| Checksum     | Integrity            |

---

## ⚠️ Final Note

Security is about **trade-offs**, not perfection.

This project prioritizes:

* Learning depth
* Correct primitives
* Explicit decisions

Over:

* Magical defaults
* Surface-level security

---

If you are reading this and feel confused — that is **normal**.
Security engineering rewards patience and understanding.
