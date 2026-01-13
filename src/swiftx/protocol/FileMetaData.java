package swiftx.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileMetaData {

    // Protocol is strictly made

    public final String fileName;
    public final long fileSize;

    public FileMetaData(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    // Writing metadata method
    public void writeTo(DataOutputStream dos) throws IOException {

        /* Writing order is :
            1.) File Name Length(in bytes)
            2.) File Name (in bytes)
            3.) File Size (in long)
        */

        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(fileNameBytes.length);
        dos.write(fileNameBytes);
        dos.writeLong(fileSize);

    }
    public static FileMetaData readFrom(DataInputStream dis) throws IOException {
        /*
            Reading should also follow the same protocol as write method
            1.) File Name Length (As int)
            2.) File Name (As String)
            3.) File Size (in Long)
         */
        int nameLength = dis.readInt();
        byte[] nameBytes = new byte[nameLength];
        dis.readFully(nameBytes);

        String name = new String(nameBytes, StandardCharsets.UTF_8);

        long fileSize = dis.readLong();

        return new FileMetaData(name, fileSize);
    }

}
