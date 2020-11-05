package be.niedel.proto.comparison;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedSizeComparisonService {

    public byte[] compressUsingGzip(byte[] uncompressedData) {
        byte[] compressedData = new byte[]{};
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(uncompressedData.length);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteStream)) {
            gzipOutputStream.write(uncompressedData);
            gzipOutputStream.close();
            compressedData = byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedData;
    }

    public byte[] unCompress(byte[] gzipCompressedData) {
        byte[] uncompressedData = new byte[]{};
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(gzipCompressedData);
             ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteInputStream)) {
            byte[] buffer = new byte[1024];
            int bytesLength;
            while ((bytesLength = gzipInputStream.read(buffer)) != -1) {
                byteOutputStream.write(buffer, 0, bytesLength);
            }
            uncompressedData = byteOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uncompressedData;
    }

}
