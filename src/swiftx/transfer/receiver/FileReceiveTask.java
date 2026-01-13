package swiftx.transfer.receiver;

import swiftx.crypto.CryptoUtils;
import swiftx.protocol.FileMetaData;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.security.MessageDigest;

public class FileReceiveTask implements Runnable {
        // Buffer Size (of how many bytes the buffer will be created)
        private static final int BUFFER_SIZE = 64 * 1024;

        // The key Bytes[] (hardcoded for now)
        private static final byte[] KEY_BYTES = "1234567890ABCDEFGHIJ".getBytes();

        private final Socket socket;

        FileReceiveTask(Socket socket) {
            this.socket = socket;
        }

        /*
        Reading should be done in the same order as per our protocol design
        1.) MetaData from DataInputStream
        2.) IV
        3.) File Bytes
        4.) Checksum
         */

    @Override
    public void run() {
        try(
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream())
                )
                ) {

            // Reading MetaData from DataInputStream
            FileMetaData metaData = FileMetaData.readFrom(dis);
            System.out.println("Receiving - "+ metaData.fileName + " | " + metaData.fileSize);

            // Reading IV as per our protocol
            // First length of IV
            int ivLen = dis.readInt();
            byte[] iv = new byte[ivLen];

            // Now we read the actual IV
            dis.readFully(iv);

            // Now we receive actual file bytes and checksum
            // But the file is encrypted so we DECRYPT it using Cipher for that
            // 1.) We have to get the SecretKey
            // 2.) Initialize CipherIInputStream
            SecretKey key = CryptoUtils.getKey(KEY_BYTES);
            Cipher decryptCipher = CryptoUtils.initDecryptCipher(key, iv);

            CipherInputStream cis = new CipherInputStream(dis, decryptCipher);

            // Now we read file
            File outputFile = new File(metaData.fileName);
            MessageDigest messageDigest =  MessageDigest.getInstance("SHA-256");

            try(
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    ) {
                byte[] buffer = new byte[BUFFER_SIZE];
                long remaining = metaData.fileSize;
                long received = 0;
                int read;
                int lastPercent = 0;

                while (remaining > 0 &&
                        (read = cis.read(
                                buffer,
                                0,
                                (int) Math.min(buffer.length, remaining)
                        )) != -1) {

                    fos.write(buffer, 0, read);
                    messageDigest.update(buffer, 0, read);

                    remaining -= read;
                    received += read;

                    int percent = (int) ((received * 100) / metaData.fileSize);
                    if (percent != lastPercent) {
                        System.out.print("\rReceiving: " + percent + "%");
                        lastPercent = percent;
                    }
                }
            }

            byte[] receiverHash = messageDigest.digest();

            // Reading Sender's Checksum
            int hashLen = dis.readInt();
            byte[] senderHash = new byte[hashLen];
            dis.readFully(senderHash);

            System.out.println();

            if (MessageDigest.isEqual(receiverHash, senderHash)) {
                System.out.println("File verified");
            } else {
                System.out.println("File corrupted");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }


}

