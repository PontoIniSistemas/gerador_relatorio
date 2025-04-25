package br.com.pontoini;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public byte[] gerarPdf(String html) throws IOException {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(pdfStream);
             PdfDocument pdfDocument = new PdfDocument(writer)) {
            HtmlConverter.convertToPdf(html, pdfDocument, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pdfStream.toByteArray();
    }

    private static String getOutputFileName(String inputFileName) {
        int dotIndex = inputFileName.lastIndexOf('.');
        return (dotIndex == -1) ? inputFileName + ".pdf" : inputFileName.substring(0, dotIndex) + ".pdf";
    }

    public static void main(String[] args) {
        boolean isMac = System.getProperty("os.name").equals("Mac OS X");
        String tempDir = (args.length >= 2) ? args[1] : System.getProperty("java.io.tmpdir");

        if (args.length == 0) {
            System.err.println("Parâmetro não informado - nome do arquivo");
            System.exit(1);
        }

        if (isMac && args.length != 2) {
            System.err.println("Para Mac, informe a pasta temporária como segundo parâmetro");
            System.exit(1);
        }

        String htmlFilePath = tempDir + args[0] + ".html";
        try {
            Path inputPath = Paths.get(htmlFilePath);
            if (!Files.exists(inputPath)) {
                System.err.println("Arquivo HTML não encontrado: " + htmlFilePath);
                System.exit(1);
            }

            String htmlContent = Files.readString(inputPath, StandardCharsets.UTF_8);
            byte[] pdfBytes = new Main().gerarPdf(htmlContent);
            Path outputPath = Paths.get(tempDir, getOutputFileName(inputPath.getFileName().toString()));
            Files.write(outputPath, pdfBytes);
        } catch (IOException e) {
            System.err.println("Erro ao gerar PDF: " + e.getMessage());
            System.exit(1);
        }
    }
}