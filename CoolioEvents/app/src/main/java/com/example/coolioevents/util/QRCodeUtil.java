package com.example.coolioevents.util;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
/**
 * Copyright 2025 Parth Mittal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * PURPOSE:
 * This utility class provides static methods to generate QR codes as Bitmap images.
 * It supports both the newer and older method names (“generate” and “make”) for
 * compatibility with teammate code. The QR code is generated using the ZXing library.
 *
 * RATIONALE:
 * Keeping QR generation logic in a separate utility class ensures reusability across
 * different activities and services, such as event creation or sharing links.
 * It centralizes encoding behavior and prevents duplicate code.
 *
 * OUTSTANDING ISSUES:
 * The current implementation does not handle invalid or empty content strings.
 * QR code color customization or error correction level adjustment is not yet supported.
 * Future versions could include automatic scaling or saving to storage options.
 *
 * @author Parth Mittal
 * @version 1.0
 * @since 2025-11-07
 */
public class QRCodeUtil {
    //  Original method used by code
    public static Bitmap generateQRCode(String content) throws WriterException {
        return generate(content, 512);
    }
    //  Teammates older method call compatibility
    public static Bitmap make(String content, int sizePx) throws WriterException {
        return generate(content, sizePx);
    }
    //  Core implementation
    public static Bitmap generate(String content, int sizePx) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx);
        Bitmap bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < sizePx; x++) {
            for (int y = 0; y < sizePx; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
