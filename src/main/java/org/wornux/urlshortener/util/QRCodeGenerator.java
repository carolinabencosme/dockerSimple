package org.wornux.urlshortener.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** Utility class for generating QR codes. */
public class QRCodeGenerator {

  /**
   * Generates a QR code for the given URL and returns it as a byte array.
   *
   * @param url The URL to encode in the QR code.
   * @return A byte array representing the QR code image.
   * @throws IOException If an error occurs during QR code generation.
   */
  public static byte[] generateQRCode(String url) throws IOException {
    QRCodeWriter writer = new QRCodeWriter();
    int width = 500;
    int height = 500;
    try {
      BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
      // Convert the ByteArrayOutputStream to a byte array
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new IOException("Failed to generate QR code", e);
    }
  }
}
