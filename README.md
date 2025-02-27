This is a little bit tweaked version of delight.im-Android-AdvancedWebView.

# Quick Build

- **Renaming:** If you need a quick renaming, checkout my profile and visit **android-package-renamer**, you can easily rename the package/app name.

- **Change URL:** Do not forget to put your own url in **MainActivity**.

- **Icon Set:** Set your custom icon set and you are ready to go!


# Example Usage of Blob Interface (html2canvas used in the example)

```javascript
         
          const exportAsPDF = async (element, filename, width = 1366) => {
              try {
                  const canvas = await captureElement(element, width);
                  const imgData = canvas.toDataURL('image/png');
                  const pdf = new jsPDF({
                      orientation: 'portrait',
                      unit: 'mm',
                      format: 'a4',
                  });
     
                  const imgProps = pdf.getImageProperties(imgData);
                  const pdfWidth = pdf.internal.pageSize.getWidth();
                  const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;
     
                  pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
     
                  // Check for Android native interface
                  if (window.AndroidInterface && typeof window.AndroidInterface.handleBlobDownload === 'function') {
                      // Get the PDF as a data URL and pass it directly to Android
                      const pdfDataUrl = pdf.output('datauristring');
                      window.AndroidInterface.handleBlobDownload(pdfDataUrl, filename + ".pdf");
                  } else {
                      // Fallback for normal browsers
                      pdf.save(`${filename}.pdf`);
                  }
              } catch (error) {
                  console.error('Failed to export as PDF:', error);
                  throw error;
              }
          };
     
     
          const exportAsImage = async (element, filename, width = 1366) => {
              try {
                  const canvas = await captureElement(element, width);
                  // Check if running in Android WebView (assuming AndroidInterface is injected)
                  if (window.AndroidInterface && typeof window.AndroidInterface.handleBlobDownload === 'function') {
                      // Directly convert canvas to data URL and pass it to the native interface.
                      // This avoids creating a blob URL and triggering onDownloadRequested.
                      const dataUrl = canvas.toDataURL('image/png');
                      // Append the extension if needed
                      window.AndroidInterface.handleBlobDownload(dataUrl, filename + ".png");
                  } else {
                      // For non-Android environments, use the normal blob download
                      canvas.toBlob((blob) => {
                          if (blob) {
                              const url = URL.createObjectURL(blob);
                              const link = document.createElement('a');
                              link.href = url;
                              link.download = `${filename}.png`;
                              document.body.appendChild(link);
                              link.click();
                              document.body.removeChild(link);
                              URL.revokeObjectURL(url);
                          } else {
                              console.error('Failed to create blob for image.');
                          }
                      }, 'image/png');
                  }
              } catch (error) {
                  console.error('Failed to export as image:', error);
                  throw error;
              }
          };
```
