package nana;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import processing.core.PImage;

public class NanaWebcamClient implements Callable {

    Socket client;
    String clientId;
    OutputStream os;
    BufferedOutputStream bos;
    public boolean exit;

    static String encoder = "bmp";

    public NanaWebcamClient(Socket client) throws IOException {
        this.client = client;
        String clientId = client.getInetAddress().getHostAddress();
        os = client.getOutputStream();
        bos = new BufferedOutputStream(os);
        bos.write(buildHttpHeader().getBytes());
        bos.flush();
        System.out.println("accepted client " + clientId);
    }

    public void closeClient() {
        try {
            exit = true;
            client.close();
            System.out.println(clientId + " closed");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(clientId + " error");
        }
    }

    public String buildHttpHeader() {
        return "HTTP/1.1 200 OK\n"
                + "Connection: close\n"
                + "Server: Nana_Webcam\n"
                + "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\n"
                + "Pragma: no-cache\n"
                + "Expires: -1\n"
                + "Access-Control-Allow-Origin: *\n"
                + "Content-Type: multipart/x-mixed-replace;boundary=nanaaaaa\n"
                + "\n"
                + "\n";
    }

    public String buildPartHeader(int length) {
        return "--nanaaaaa\n"
                + "Content-Type: image/" + encoder
                + "\nContent-Length: " + length
                + "\n\n";
    }

    PImage cache;

    public Callable setImage(PImage image) {
        if (client.isClosed()) {
            return this;
        } else {
            this.cache = image;
            return null;
        }
    }

    private static byte[] N = "\n".getBytes();
    private static byte[] TIFF_HEADER = new byte[]{
        77, 77, 42, 8, 9, -2, 4, 1,
        1, 3,
        1, 1, 1, 3, 1,
        1, 2, 3, 3, 122,
        1, 6, 3,
        1, 2, 1, 17, 4, 1, 3,
        1, 21,
        3, 1, 3, 1, 22, 3, 1,
        1,
        23, 4, 1, 8, 8, 8};

    public static boolean saveTIFF(PImage pImage, OutputStream output) {
        try {
            encoder = "tiff";
            byte[] tiff = new byte[768];
            System.arraycopy(TIFF_HEADER, 0, tiff, 0, TIFF_HEADER.length);
            tiff[30] = (byte) (pImage.pixelWidth >> 8 & 0xFF);
            tiff[31] = (byte) (pImage.pixelWidth & 0xFF);
            tiff[102] = (byte) (pImage.pixelHeight >> 8 & 0xFF);
            tiff[42] = (byte) (pImage.pixelHeight >> 8 & 0xFF);
            tiff[103] = (byte) (pImage.pixelHeight & 0xFF);
            tiff[43] = (byte) (pImage.pixelHeight & 0xFF);
            int count = pImage.pixelWidth * pImage.pixelHeight * 3;
            tiff[114] = (byte) (count >> 24 & 0xFF);
            tiff[115] = (byte) (count >> 16 & 0xFF);
            tiff[116] = (byte) (count >> 8 & 0xFF);
            tiff[117] = (byte) (count & 0xFF);
            output.write(tiff);
            for (int i = 0; i < pImage.pixels.length; i++) {
                output.write(pImage.pixels[i] >> 16 & 0xFF);
                output.write(pImage.pixels[i] >> 8 & 0xFF);
                output.write(pImage.pixels[i] & 0xFF);
            }
            output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveJPEG(PImage pImage, OutputStream output) {
        BufferedImage bImage = new BufferedImage(pImage.pixelWidth, pImage.pixelHeight, 1);
        bImage.setRGB(0, 0, pImage.pixelWidth, pImage.pixelHeight, pImage.pixels, 0, pImage.pixelWidth);
        ImageWriter writer = null;
        ImageWriteParam param = null;
        IIOMetadata metadata = null;
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        if (iter.hasNext()) {
            writer = iter.next();
        } else {
            writer = null;
        }
        if (writer != null) {
            encoder = "jpeg";
            param = writer.getDefaultWriteParam();
            param.setCompressionMode(MODE_EXPLICIT);
            param.setCompressionQuality(1.0F);
            try {
                writer.setOutput(ImageIO.createImageOutputStream(output));
                writer.write(metadata, new IIOImage(bImage, null, metadata), param);
                writer.dispose();
                output.flush();
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(NanaWebcamClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public static boolean saveBitmap(PImage pImage, OutputStream output) {
        encoder = "bmp";
        return true;
    }

    public String call() {
        long current = System.currentTimeMillis();
        if (client.isClosed()) {
            this.closeClient();
            System.out.println("client is closed!");
            return "";
        }
        PImage image = cache;
        if (!Thread.currentThread().isInterrupted()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                saveJPEG(image, baos);
                if (!Thread.currentThread().isInterrupted()) {

                }

                if (!Thread.currentThread().isInterrupted()) {
                    bos.write(buildPartHeader(baos.size()).getBytes());
                    baos.writeTo(bos);
                    bos.write(N);
                    bos.flush();
                } else {
                    return "";
                }
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("I/O error");
            }
        } else {
            System.out.println("compress error");
        }
        closeClient();
        return "";
    }
}
