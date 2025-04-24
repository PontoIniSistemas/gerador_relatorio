package br.com.pontoini;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public byte[] gerarPdf(String html) throws IOException {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, pdfStream);
        return pdfStream.toByteArray();
    }

    private static String getOutputFileName(String inputFileName) {
        int dotIndex = inputFileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return inputFileName + ".pdf";
        } else {
            return inputFileName.substring(0, dotIndex) + ".pdf";
        }
    }

    public static void main(String[] args) {
        boolean isMac = System.getProperty("os.name").equals("Mac OS X");
        String tempDir;

        if (args.length == 0) {
            System.err.println("parametro nao informado - nome do arquivo");
            System.exit(1);
        }

        if (isMac && args.length != 2) {
            System.err.println("parametro nao informado - pasta temp - para mac e necessario informar pasta");
        }

        if (args.length == 2) {
            tempDir = args[1];
        } else {
            tempDir = System.getProperty("java.io.tmpdir");
        }

        String htmlFilePath = tempDir + args[0] + ".html";
        try {
            Main main = new Main();
            Path inputPath = Paths.get(htmlFilePath);
            if (!Files.exists(inputPath)) {
                System.out.println("arquivo nao encontrado - o arquivo html deve ser criado no temp do computador");
                System.exit(1);
            }
            byte[] htmlBytes = Files.readAllBytes(inputPath);
            String htmlContent = new String(htmlBytes, StandardCharsets.UTF_8);
            byte[] pdfBytes = main.gerarPdf(htmlContent);
            String outputFileName = getOutputFileName(inputPath.getFileName().toString());
            Path outputPath = Paths.get(tempDir, outputFileName);
            Files.write(outputPath, pdfBytes);
        } catch (IOException e) {
            System.exit(1);
        }
    }
}