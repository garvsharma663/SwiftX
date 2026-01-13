package swiftx.transfer.sender;

import swiftx.crypto.CryptoUtils;
import swiftx.protocol.FileMetaData;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;

public class FileSender {

    /*
     Sending should be done in the same order as per our protocol design
        1.) MetaData
        2.) IV
        3.) File Bytes
        4.) Checksum

     */
    private static final int BUFFER_SIZE = 64*1024;

    // AES Key (Hardcoded for now)
    private static final byte[] KEY_BYTES = "1234567890ABCDEFGHIJ".getBytes();

    // File Sending Method
    public static void sendFile(String ip, int port, File file) throws Exception {

        // Initialize Socket, Streams
        try(
                Socket socket = new Socket(ip, port);
                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(socket.getOutputStream())
                );
                FileInputStream fis = new FileInputStream(file);
                ) {

            // Send MetaData first
            FileMetaData metaData = new FileMetaData(file.getName(), file.length());
            metaData.writeTo(dos);

            // AES, IV
            SecretKey secretKey = CryptoUtils.getKey(KEY_BYTES);

            byte[] iv = CryptoUtils.generateIV();
            dos.writeInt(iv.length);
            dos.write(iv);

            dos.flush();

            // Initializing EncryptCipher
            Cipher encryptCipher =CryptoUtils.initEncryptCipher(secretKey, iv);
            CipherOutputStream cos =  new CipherOutputStream(dos, encryptCipher);

            // File and Checksum
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            long sent = 0;

            while ((read = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, read);
                messageDigest.update(buffer, 0, read);
                sent += read;

                int percent = (int) ((sent * 100) / file.length());
                System.out.print("\rSending: " + percent + "%");
            }

            cos.flush();
            cos.close();

            byte[] checksum = messageDigest.digest();
            dos.writeInt(checksum.length);
            dos.write(checksum);
            dos.flush();

            socket.close();
        }





    }
}
