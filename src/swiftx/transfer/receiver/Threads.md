# Multithreading in `FileReceiverServer`

This document explains **why and how multithreading is used** in this project, starting from basic concepts and then mapping them directly to the `FileReceiverServer` implementation.

---

## 1. What is a Thread? (Intuition First)

A **thread** is an independent path of execution inside a single program.

Think of a program as a factory:

* The **program** is the building
* **Threads** are workers inside it

Without threads:

* Only one worker exists
* Tasks are handled **one after another**

With threads:

* Multiple workers exist
* Tasks can run **in parallel**

---

## 2. Single-Threaded Server Problem

Imagine this server **without threads**:

```java
Socket socket = serverSocket.accept();
receiveFile(socket); // blocks for minutes
```

What happens?

* Client A sends a 20 GB file
* Client B connects
* ❌ Client B must wait until Client A finishes

This is unacceptable for any real server.

---

## 3. Why Servers Use Threads

Servers must:

* Accept new connections continuously
* Handle slow clients
* Scale to multiple users

Threads allow:

* One client per thread
* Isolation between clients
* Better CPU utilization

---

## 4. Thread Mental Model (Very Important)

Use this analogy:

| Component    | Real-world analogy |
| ------------ | ------------------ |
| ServerSocket | Reception desk     |
| Socket       | Phone call         |
| Thread       | Personal assistant |

The receptionist:

* Answers calls
* Assigns an assistant
* Immediately waits for the next call

---

## 5. Thread Lifecycle (Simplified)

```
NEW → RUNNABLE → RUNNING → BLOCKED → TERMINATED
```

In this project:

* `accept()` → BLOCKED
* File transfer → RUNNING
* Waiting for network → BLOCKED
* File finished → TERMINATED

---

## 6. How Threads Are Used in This Project

### Code:

```java
while (true) {
    Socket socket = serverSocket.accept();
    new Thread(new FileReceiveTask(socket)).start();
}
```

### What actually happens:

1. Server waits for a connection
2. A client connects
3. A **new thread** is created
4. That thread handles file receiving
5. Main server thread immediately goes back to listening

---

## 7. Why `Runnable` Is Used

```java
class FileReceiveTask implements Runnable
```

Java separates:

* **What to do** → `Runnable`
* **How to run** → `Thread`

Benefits:

* Cleaner design
* Compatible with thread pools
* Easier to scale later

---

## 8. Why One Socket = One Thread

Each thread owns:

* One `Socket`
* One file
* One cipher
* One checksum

This prevents:

* Race conditions
* Shared-state bugs
* Data corruption

---

## 9. Basic Thread Example (Minimal)

```java
Runnable task = () -> {
    System.out.println("Running in parallel");
};

Thread t = new Thread(task);
t.start();
```

This runs **independently** of the main program flow.

---

## 10. Blocking vs Multithreaded Design

| Design          | Result                    |
| --------------- | ------------------------- |
| Single-threaded | One client at a time      |
| Multithreaded   | Many clients concurrently |
| Event-driven    | Complex but scalable      |

This project uses **classic thread-per-connection**, which is:

* Easy to reason about
* Correct for moderate scale
* Ideal for learning systems programming

---

## 11. Future Improvements

Later, this can be upgraded to:

* `ExecutorService` (thread pools)
* Non-blocking NIO
* Backpressure handling

But **thread-per-connection is the correct choice here**.

---

## 12. Final Rule to Remember

> **Threads exist to prevent waiting from stopping progress.**

If one client is slow, others must not suffer.

This file ensures exactly that.
