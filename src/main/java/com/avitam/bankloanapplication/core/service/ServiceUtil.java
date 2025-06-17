package com.avitam.bankloanapplication.core.service;

import com.avitam.bankloanapplication.core.HotFolderConstants;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceUtil implements HotFolderConstants {

    public static final String BASE_DOCUMENTS = "BaseDocuments";
    public static final String STOCK_DOCUMENTS = "StockDocuments";
    public static final String PRICE_DOCUMENTS = "PriceDocuments";
    public static final String ADDON_DOCUMENTS = "AddonDocuments";
    public static final String SM_DOCUMENTS = "SmDocuments";
    public static final String TRADE_IN_DOCUMENTS = "TradeInDocuments";
    public static final String SC_PLUS_DOCUMENTS = "ScPlusDocuments";
    public static final String EUP_DOCUMENTS = "EupDocuments";
    public static final String EWARRANTY_DOCUMENTS = "EwarrantyDocuments";
    public static final String BENEFITS_DOCUMENTS = "BenefitsDocuments";
    public static final String AVAILABILITY_DOCUMENTS = "AvailabilityDocuments";
    private static final Set<String> FILE_FORMATS = ImmutableSet.of("Excel", "PDF", "XML", "JSON", "CSV", "UI");
    private static final Map<String, String> FILE_EXT_MAP = new HashMap<>() {
        {
            put("xlsx", "Excel");
            put("xlsm", "Excel");
            put("xlsb", "Excel");
            put("xltx", "Excel");
            put("xltm", "Excel");
            put("xls", "xls");
            put("xlt", "xls");
            put("pdf", "PDF");
            put("xml", "XML");
            put("json", "JSON");
            put("csv", "CSV");
            put("ui", "UI");
        }
    };
    private static final Map<String, String> REPOSITORIES = new HashMap<>() {
        {
            put("23", STOCK_DOCUMENTS);
            put("24", PRICE_DOCUMENTS);
            put("25", ADDON_DOCUMENTS);
            put("26", SM_DOCUMENTS);
            put("27", TRADE_IN_DOCUMENTS);
            put("28", SC_PLUS_DOCUMENTS);
            put("29", EUP_DOCUMENTS);
            put("30", EWARRANTY_DOCUMENTS);
            put("31", BENEFITS_DOCUMENTS);
            put("32", AVAILABILITY_DOCUMENTS);
        }
    };
    private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);
    Logger LOG = LoggerFactory.getLogger(ServiceUtil.class);

    public static Set<String> getSupportedFileFormats() {
        return FILE_FORMATS;
    }

    public static void moveFile(File file, String srcDir, String path) {
        boolean success = file.renameTo(FileUtils.getFile(srcDir + File.separator + path + File.separator + file.getName()));
        if (success) {
            file.delete();
        }
    }

    public static String getFormatForTheFileExt(String ext) {
        return FILE_EXT_MAP.get(ext);
    }

    public static String getProcessedFolderLocation(Environment env) {
        return StringUtils.isNotEmpty(env.getProperty("processed.folder.location")) ? env.getProperty("hot.folder.location") : PROCESSED_FOLDER_LOCATION;
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object value = null;

        switch (cell.getCellType()) {
            case BLANK:
                value = "";
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
        }
        return value;
    }

    public static String getDocumentRepoForReportId(String id) {
        return StringUtils.isNotEmpty(REPOSITORIES.get(id)) ? REPOSITORIES.get(id) : BASE_DOCUMENTS;
    }

    public Double calculateDiscount(String price, String promotionPrice) {
        if (StringUtils.isNotEmpty(price) && StringUtils.isNotEmpty(promotionPrice)) {
            try {
                Double doublePrice = Double.parseDouble(price);
                Double doublePromotionPrice = Double.parseDouble(promotionPrice);
                return (doublePrice - doublePromotionPrice) / doublePrice * 100;
            } catch (NumberFormatException e) {
                LOG.warn("Error parsing price values Price:" + price + " and promotionPrice:" + promotionPrice);
            }
        }
        return Double.valueOf(0);
    }
}
