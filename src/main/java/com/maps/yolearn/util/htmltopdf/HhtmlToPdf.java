//package com.maps.yolearn.util.htmltopdf;
//
//import com.itextpdf.text.DocumentException;
//import java.io.IOException;
//import org.apache.commons.io.output.ByteArrayOutputStream;
//import org.xhtmlrenderer.pdf.ITextRenderer;
//
//public class HhtmlToPdf {
//
//    /**
//     * Generate a PDF document
//     *
//     * @param html HTML as a string
//     * @return bytes of PDF document
//     * @throws com.itextpdf.text.DocumentException
//     * @throws java.io.IOException
//     * @throws com.lowagie.text.DocumentException
//     */
//    public static byte[] toPdf(String html) throws DocumentException, IOException, com.lowagie.text.DocumentException {
//        final ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(html);
//        renderer.layout();
//        try (ByteArrayOutputStream fos = new ByteArrayOutputStream(html.length())) {
//            renderer.createPDF(fos);
//            return fos.toByteArray();
//        }
//    }
//}
