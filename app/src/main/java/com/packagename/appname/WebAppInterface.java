package com.packagename.appname;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class WebAppInterface {
    Context mContext;

    public WebAppInterface(Context context) {
        mContext = context;
    }
    @JavascriptInterface
    public void handleBlobDownload(String base64Data, String filename) {
        try {
            base64Data = base64Data.substring(base64Data.indexOf("base64,") + 7);

            byte[] fileData = Base64.decode(base64Data, Base64.DEFAULT);

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, filename);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileData);
            }

            Toast.makeText(mContext, "Downloaded: " + filename, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Example Usage:
     *
     *     const exportAsPDF = async (element, filename, width = 1366) => {
     *         try {
     *             const canvas = await captureElement(element, width);
     *             const imgData = canvas.toDataURL('image/png');
     *             const pdf = new jsPDF({
     *                 orientation: 'portrait',
     *                 unit: 'mm',
     *                 format: 'a4',
     *             });
     *
     *             const imgProps = pdf.getImageProperties(imgData);
     *             const pdfWidth = pdf.internal.pageSize.getWidth();
     *             const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;
     *
     *             pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
     *
     *             // Check for Android native interface
     *             if (window.AndroidInterface && typeof window.AndroidInterface.handleBlobDownload === 'function') {
     *                 // Get the PDF as a data URL and pass it directly to Android
     *                 const pdfDataUrl = pdf.output('datauristring');
     *                 window.AndroidInterface.handleBlobDownload(pdfDataUrl, filename + ".pdf");
     *             } else {
     *                 // Fallback for normal browsers
     *                 pdf.save(`${filename}.pdf`);
     *             }
     *         } catch (error) {
     *             console.error('Failed to export as PDF:', error);
     *             throw error;
     *         }
     *     };
     *
     *
     *     const exportAsImage = async (element, filename, width = 1366) => {
     *         try {
     *             const canvas = await captureElement(element, width);
     *             // Check if running in Android WebView (assuming AndroidInterface is injected)
     *             if (window.AndroidInterface && typeof window.AndroidInterface.handleBlobDownload === 'function') {
     *                 // Directly convert canvas to data URL and pass it to the native interface.
     *                 // This avoids creating a blob URL and triggering onDownloadRequested.
     *                 const dataUrl = canvas.toDataURL('image/png');
     *                 // Append the extension if needed
     *                 window.AndroidInterface.handleBlobDownload(dataUrl, filename + ".png");
     *             } else {
     *                 // For non-Android environments, use the normal blob download
     *                 canvas.toBlob((blob) => {
     *                     if (blob) {
     *                         const url = URL.createObjectURL(blob);
     *                         const link = document.createElement('a');
     *                         link.href = url;
     *                         link.download = `${filename}.png`;
     *                         document.body.appendChild(link);
     *                         link.click();
     *                         document.body.removeChild(link);
     *                         URL.revokeObjectURL(url);
     *                     } else {
     *                         console.error('Failed to create blob for image.');
     *                     }
     *                 }, 'image/png');
     *             }
     *         } catch (error) {
     *             console.error('Failed to export as image:', error);
     *             throw error;
     *         }
     *     };
     */
}