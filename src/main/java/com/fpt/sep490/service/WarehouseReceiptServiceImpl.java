package com.fpt.sep490.service;

import com.fpt.sep490.Enum.ReceiptType;
import com.fpt.sep490.dto.WarehouseReceiptDto;
import com.fpt.sep490.model.Batch;
import com.fpt.sep490.model.WarehouseReceipt;
import com.fpt.sep490.repository.BatchRepository;
import com.fpt.sep490.repository.WarehouseReceiptRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;


@Service
public class WarehouseReceiptServiceImpl implements WarehouseReceiptService {

    private final BatchRepository batchRepository;
    private final WarehouseReceiptRepository warehouseReceiptRepository;

    public WarehouseReceiptServiceImpl(BatchRepository batchRepository, WarehouseReceiptRepository warehouseReceiptRepository) {
        this.batchRepository = batchRepository;
        this.warehouseReceiptRepository = warehouseReceiptRepository;
    }

    @Override
    public WarehouseReceipt createWarehouseReceipt(WarehouseReceiptDto receiptDto, String batchCode) {
        WarehouseReceipt receipt = new WarehouseReceipt();
        receipt.setReceiptDate(receiptDto.getReceiptDate());
        receipt.setReceiptType(ReceiptType.valueOf(receiptDto.getReceiptType()));
        Batch batch = batchRepository.findByBatchCode(batchCode);
        receipt.setBatch(batch);
        warehouseReceiptRepository.save(receipt);
        return receipt;
    }

//    @Override
//    public void createImportPDF(WarehouseReceipt receipt, String htmlTemplatePath, String dest) throws IOException, DocumentException {
//        String html = getHtmlFromTemplate(receipt, htmlTemplatePath);
//        Document document = new Document();
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
//        document.open();
//        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(html.getBytes()));
//        document.close();
//    }

//    private static String getHtmlFromTemplate(WarehouseReceipt receipt, String htmlTemplatePath) throws IOException {
//        String html = new String(Files.readAllBytes(Paths.get(htmlTemplatePath)));
//        html = html.replace("#{receiptId}", receipt.getId());
//        html = html.replace("#{batchId}", receipt.getBatch().getId());
//        html = html.replace("#{receiptType}", receipt.getReceiptType());
//        html = html.replace("#{document}", receipt.getDocument());
//        String batchProductsHtml = receipt.getBatchProductList().stream()
//                .map(batchProduct -> "<tr><td>" + batchProduct.getId() + "</td><td>" + batchProduct.getName() + "</td></tr>")
//                .collect(Collectors.joining(""));
//        html = html.replace("#{batchProducts}", batchProductsHtml);
//        return html;
//    }
}
